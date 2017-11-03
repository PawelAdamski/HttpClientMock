package com.github.paweladamski.httpclientmock;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.github.paweladamski.httpclientmock.Rule.NOT_FOUND;

public class HttpClientMock extends CloseableHttpClient {

    private final HttpParams params = new BasicHttpParams();

    private final List<RuleBuilder> rulesUnderConstruction = new ArrayList<>();
    private final List<Rule> rules = new ArrayList<>();
    private final String defaultHost;
    private final List<Request> requests = new ArrayList<>();

    /**
     * Creates mock of Apache HttpClient
     */
    public HttpClientMock() {
        this("");
    }

    /**
     * Creates mock of Apache HttpClient with default host. All defined conditions without host will use default host
     *
     * @param defaultHost default host for later conditions
     */
    public HttpClientMock(String defaultHost) {
        this.defaultHost = defaultHost;
    }

    /**
     * Resets mock to initial state where there are no rules and no previous requests.
     */
    public void reset() {
        this.rulesUnderConstruction.clear();
        this.requests.clear();
    }

    /**
     * Creates verification builder.
     *
     * @return request number verification builder
     */
    public HttpClientVerify verify() {
        return new HttpClientVerify(defaultHost, requests);
    }

    /**
     * Starts defining new rule which requires HTTP POST method.
     *
     * @return HttpClientMockBuilder which allows  to define new rule
     */
    public HttpClientMockBuilder onPost() {
        return newRule("POST");
    }

    /**
     * Starts defining new rule which requires HTTP GET method.
     *
     * @return HttpClientMockBuilder which allows  to define new rule
     */
    public HttpClientMockBuilder onGet() {
        return newRule("GET");
    }

    /**
     * Starts defining new rule which requires HTTP DELETE method.
     *
     * @return HttpClientMockBuilder which allows  to define new rule
     */
    public HttpClientMockBuilder onDelete() {
        return newRule("DELETE");
    }

    /**
     * Starts defining new rule which requires HTTP HEAD method.
     *
     * @return HttpClientMockBuilder which allows  to define new rule
     */
    public HttpClientMockBuilder onHead() {
        return newRule("HEAD");
    }

    /**
     * Starts defining new rule which requires HTTP OPTION method.
     *
     * @return HttpClientMockBuilder which allows  to define new rule
     */
    public HttpClientMockBuilder onOption() {
        return newRule("OPTION");
    }

    /**
     * Starts defining new rule which requires HTTP PUT method.
     *
     * @return HttpClientMockBuilder which allows  to define new rule
     */
    public HttpClientMockBuilder onPut() {
        return newRule("PUT");
    }

    /**
     * Starts defining new rule which requires HTTP PATCH method.
     *
     * @return HttpClientMockBuilder which allows  to define new rule
     */
    public HttpClientMockBuilder onPatch() {
        return newRule("PATCH");
    }

    /**
     * Starts defining new rule which requires HTTP GET method and url. If provided url starts with "/" request url must be equal to concatenation of default
     * host and url. Otherwise request url must equal to provided url. If provided url contains query parameters and/or reference they are parsed and added as a
     * separate conditions. <p> For example:<br> <code> httpClientMock.onGet("http://localhost/login?user=Ben#edit"); </code> <br>is equal to<br> <code>
     * httpClientMock.onGet("http://localhost/login").withParameter("user","Ben").withReference("edit); </code>
     *
     * @param url required url
     * @return HttpClientMockBuilder which allows to define new rule
     */
    public HttpClientMockBuilder onGet(String url) {
        return newRule("GET", url);
    }

    /**
     * Starts defining new rule which requires HTTP POST method and url. URL works the same way as in {@link #onGet(String) onGet}
     *
     * @param url required url
     * @return HttpClientMockBuilder which allows to define new rule
     */
    public HttpClientMockBuilder onPost(String url) {
        return newRule("POST", url);
    }

    /**
     * Starts defining new rule which requires HTTP PUT method and url. URL works the same way as in {@link #onGet(String) onGet}
     *
     * @param url required url
     * @return HttpClientMockBuilder which allows to define new rule
     */
    public HttpClientMockBuilder onPut(String url) {
        return newRule("PUT", url);
    }

    /**
     * Starts defining new rule which requires HTTP DELETE method and url. URL works the same way as in {@link #onGet(String) onGet}
     *
     * @param url required url
     * @return HttpClientMockBuilder which allows to define new rule
     */
    public HttpClientMockBuilder onDelete(String url) {
        return newRule("DELETE", url);
    }

    /**
     * Starts defining new rule which requires HTTP HEAD method and url. URL works the same way as in {@link #onGet(String) onGet}
     *
     * @param url required url
     * @return HttpClientMockBuilder which allows to define new rule
     */
    public HttpClientMockBuilder onHead(String url) {
        return newRule("HEAD", url);
    }

    /**
     * Starts defining new rule which requires HTTP OPTIONS method and url. URL works the same way as in {@link #onGet(String) onGet}
     *
     * @param url required url
     * @return HttpClientMockBuilder which allows to define new rule
     */
    public HttpClientMockBuilder onOptions(String url) {
        return newRule("OPTIONS", url);
    }

    /**
     * Starts defining new rule which requires HTTP PATCH method and url. URL works the same way as in {@link #onGet(String) onGet}
     *
     * @param url required url
     * @return HttpClientMockBuilder which allows to define new rule
     */
    public HttpClientMockBuilder onPatch(String url) {
        return newRule("PATCH", url);
    }

    private HttpClientMockBuilder newRule(String method) {
        RuleBuilder r = new RuleBuilder(method);
        rulesUnderConstruction.add(r);
        return new HttpClientMockBuilder(r);
    }

    private HttpClientMockBuilder newRule(String method, String url) {
        RuleBuilder r = new RuleBuilder(method, defaultHost, url);
        rulesUnderConstruction.add(r);
        return new HttpClientMockBuilder(r);
    }

    @Override
    protected CloseableHttpResponse doExecute(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) throws IOException {
        synchronized (rulesUnderConstruction) {
            for (RuleBuilder ruleBuilder : rulesUnderConstruction) {
                rules.add(ruleBuilder.toRule());
            }
            rulesUnderConstruction.clear();
        }
        Request request = new Request(httpHost, httpRequest, httpContext);
        requests.add(request);
        Rule rule = rules.stream()
                .filter(r -> r.matches(httpHost, httpRequest, httpContext))
                .reduce((a, b) -> b)
                .orElse(NOT_FOUND);
        HttpResponse response = rule.nextResponse(request);
        return new HttpResponseProxy(response);
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public HttpParams getParams() {
        return params;
    }

    @Override
    public ClientConnectionManager getConnectionManager() {
        return null;
    }

}
