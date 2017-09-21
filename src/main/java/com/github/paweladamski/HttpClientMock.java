package com.github.paweladamski;

import com.github.paweladamski.condition.HostCondition;
import com.github.paweladamski.condition.HttpMethodCondition;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.github.paweladamski.Rule.NOT_FOUND;

public class HttpClientMock extends CloseableHttpClient {

    private final List<Rule> rules = new ArrayList<>();
    private final String host;
    private final List<Request> requests = new ArrayList<>();

    public HttpClientMock() {
        this("");
    }

    public HttpClientMock(String host) {
        this.host = host;
    }

    public HttpClientMockBuilder onGet(String url) {
        return newRule("GET", url);
    }

    public HttpClientMockBuilder onPost(String url) {
        return newRule("POST", url);
    }

    public HttpClientMockBuilder onPut(String url) {
        return newRule("PUT", url);
    }

    public HttpClientMockBuilder onDelete(String url) {
        return newRule("DELETE", url);
    }

    public HttpClientMockBuilder onHead(String url) {
        return newRule("HEAD", url);
    }

    public HttpClientMockBuilder onOptions(String url) {
        return newRule("OPTIONS", url);
    }

    public HttpClientMockBuilder onPatch(String url) {
        return newRule("PATCH", url);
    }

    private HttpClientMockBuilder newRule(String method, String url) {
        Rule rule = new Rule();
        rules.add(rule);
        rule.addCondition(new HttpMethodCondition(method));
        rule.addCondition(new HostCondition(host + url));
        return new HttpClientMockBuilder(rule);
    }

    @Override
    protected CloseableHttpResponse doExecute(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) throws IOException {
        requests.add(new Request(httpHost, httpRequest, httpContext));
        Rule rule = rules.stream()
                .filter(r -> r.matches(httpHost, httpRequest, httpContext))
                .reduce((a, b) -> b)
                .orElse(NOT_FOUND);
        HttpResponse response = rule.nextResponse();
        return new HttpResponseProxy(response);
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public HttpParams getParams() {
        return null;
    }

    @Override
    public ClientConnectionManager getConnectionManager() {
        return null;
    }

    public HttpClientVerifyBuilder verify() {
        return new HttpClientVerifyBuilder(host, requests);
    }
}
