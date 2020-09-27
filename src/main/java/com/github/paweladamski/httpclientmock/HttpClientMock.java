package com.github.paweladamski.httpclientmock;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import org.apache.hc.client5.http.classic.ExecRuntime;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.classic.methods.HttpOptions;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpEntityContainer;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpResponseInterceptor;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.io.CloseMode;

public class HttpClientMock extends CloseableHttpClient {

  private final Debugger debugger;

  private final List<RuleBuilder> rulesUnderConstruction = new ArrayList<>();
  private final List<Rule> rules = new ArrayList<>();
  private final String defaultHost;
  private final List<Request> requests = new ArrayList<>();
  private boolean isDebuggingTurnOn = false;

  private final List<HttpRequestInterceptor> requestInterceptors = new ArrayList<>();
  private final List<HttpResponseInterceptor> responseInterceptors = new ArrayList<>();

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
    this(defaultHost, new Debugger());
  }

  /**
   * Creates mock of Apache HttpClient with default host. All defined conditions without host will use default host
   *
   * @param defaultHost default host for later conditions
   * @param debugger debugger used for testing
   */
  HttpClientMock(String defaultHost, Debugger debugger) {
    this.defaultHost = defaultHost;
    this.debugger = debugger;
  }

  /**
   * Resets mock to initial state where there are no rules and no previous requests.
   */
  public void reset() {
    this.rulesUnderConstruction.clear();
    this.rules.clear();
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
    return newRule(HttpPost.METHOD_NAME);
  }

  /**
   * Starts defining new rule which requires HTTP GET method.
   *
   * @return HttpClientMockBuilder which allows  to define new rule
   */
  public HttpClientMockBuilder onGet() {
    return newRule(HttpGet.METHOD_NAME);
  }

  /**
   * Starts defining new rule which requires HTTP DELETE method.
   *
   * @return HttpClientMockBuilder which allows  to define new rule
   */
  public HttpClientMockBuilder onDelete() {
    return newRule(HttpDelete.METHOD_NAME);
  }

  /**
   * Starts defining new rule which requires HTTP HEAD method.
   *
   * @return HttpClientMockBuilder which allows  to define new rule
   */
  public HttpClientMockBuilder onHead() {
    return newRule(HttpHead.METHOD_NAME);
  }

  /**
   * Starts defining new rule which requires HTTP OPTIONS method.
   *
   * @return HttpClientMockBuilder which allows  to define new rule
   * @deprecated Method name contains misspelling, use {@link #onOptions}
   */
  @Deprecated
  public HttpClientMockBuilder onOption() {
    return onOptions();
  }

  /**
   * Starts defining new rule which requires HTTP OPTIONS method.
   *
   * @return HttpClientMockBuilder which allows  to define new rule
   */
  public HttpClientMockBuilder onOptions() {
    return newRule(HttpOptions.METHOD_NAME);
  }

  /**
   * Starts defining new rule which requires HTTP PUT method.
   *
   * @return HttpClientMockBuilder which allows  to define new rule
   */
  public HttpClientMockBuilder onPut() {
    return newRule(HttpPut.METHOD_NAME);
  }

  /**
   * Starts defining new rule which requires HTTP PATCH method.
   *
   * @return HttpClientMockBuilder which allows  to define new rule
   */
  public HttpClientMockBuilder onPatch() {
    return newRule(HttpPatch.METHOD_NAME);
  }

  /**
   * Starts defining new rule which requires HTTP GET method and url. If provided url starts with "/" request url must be equal to concatenation of default host
   * and url. Otherwise request url must equal to provided url. If provided url contains query parameters they are parsed and added as a
   * separate conditions. <p> For example:<br> <code> httpClientMock.onGet("http://localhost/login?user=Ben"); </code> <br>is equal to<br> <code>
   * httpClientMock.onGet("http://localhost/login").withParameter("user","Ben"); </code>
   *
   * @param url required url
   * @return HttpClientMockBuilder which allows to define new rule
   */
  public HttpClientMockBuilder onGet(String url) {
    return newRule(HttpGet.METHOD_NAME, url);
  }

  /**
   * Starts defining new rule which requires HTTP POST method and url. URL works the same way as in {@link #onGet(String) onGet}
   *
   * @param url required url
   * @return HttpClientMockBuilder which allows to define new rule
   */
  public HttpClientMockBuilder onPost(String url) {
    return newRule(HttpPost.METHOD_NAME, url);
  }

  /**
   * Starts defining new rule which requires HTTP PUT method and url. URL works the same way as in {@link #onGet(String) onGet}
   *
   * @param url required url
   * @return HttpClientMockBuilder which allows to define new rule
   */
  public HttpClientMockBuilder onPut(String url) {
    return newRule(HttpPut.METHOD_NAME, url);
  }

  /**
   * Starts defining new rule which requires HTTP DELETE method and url. URL works the same way as in {@link #onGet(String) onGet}
   *
   * @param url required url
   * @return HttpClientMockBuilder which allows to define new rule
   */
  public HttpClientMockBuilder onDelete(String url) {
    return newRule(HttpDelete.METHOD_NAME, url);
  }

  /**
   * Starts defining new rule which requires HTTP HEAD method and url. URL works the same way as in {@link #onGet(String) onGet}
   *
   * @param url required url
   * @return HttpClientMockBuilder which allows to define new rule
   */
  public HttpClientMockBuilder onHead(String url) {
    return newRule(HttpHead.METHOD_NAME, url);
  }

  /**
   * Starts defining new rule which requires HTTP OPTIONS method and url. URL works the same way as in {@link #onGet(String) onGet}
   *
   * @param url required url
   * @return HttpClientMockBuilder which allows to define new rule
   */
  public HttpClientMockBuilder onOptions(String url) {
    return newRule(HttpOptions.METHOD_NAME, url);
  }

  /**
   * Starts defining new rule which requires HTTP PATCH method and url. URL works the same way as in {@link #onGet(String) onGet}
   *
   * @param url required url
   * @return HttpClientMockBuilder which allows to define new rule
   */
  public HttpClientMockBuilder onPatch(String url) {
    return newRule(HttpPatch.METHOD_NAME, url);
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
  protected CloseableHttpResponse doExecute(HttpHost httpHost, ClassicHttpRequest httpRequest, HttpContext httpContext) throws IOException {
    finishBuildingRules();
    executeRequestInterceptors(httpRequest, getEntityDetails(httpRequest), httpContext);
    HttpResponse response = getHttpResponse(httpHost, httpRequest, httpContext);
    executeResponseInterceptors(httpContext, getEntityDetails(httpRequest), response);

    try {
      Constructor<CloseableHttpResponse> constructor = CloseableHttpResponse.class.getDeclaredConstructor(ClassicHttpResponse.class, ExecRuntime.class);
      constructor.setAccessible(true);
      return constructor.newInstance(response, null);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  private EntityDetails getEntityDetails(Object entityContainer) {
    if (entityContainer instanceof HttpEntityContainer) {
      return ((HttpEntityContainer) entityContainer).getEntity();
    }
    return null;
  }

  private void executeResponseInterceptors(HttpContext httpContext, EntityDetails entityDetails, HttpResponse response) throws IOException {
    try {
      for (HttpResponseInterceptor responseInterceptor : responseInterceptors) {
        responseInterceptor.process(response, entityDetails, httpContext);
      }
    } catch (HttpException e) {
      throw new IOException(e);
    }
  }

  private ClassicHttpResponse getHttpResponse(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) throws IOException {
    Request request = new Request(httpHost, httpRequest, httpContext);
    requests.add(request);
    Rule rule = rules.stream()
        .filter(r -> r.matches(httpHost, httpRequest, httpContext))
        .reduce((a, b) -> b)
        .orElse(Rule.NOT_FOUND);
    if (isDebuggingTurnOn || rule == Rule.NOT_FOUND) {
      debugger.debug(rules, request);
    }
    return rule.nextResponse(request);
  }

  private void executeRequestInterceptors(HttpRequest httpRequest, EntityDetails entityDetails, HttpContext httpContext) throws IOException {
    try {
      for (HttpRequestInterceptor requestInterceptor : requestInterceptors) {
        requestInterceptor.process(httpRequest, entityDetails, httpContext);
      }
    } catch (HttpException e) {
      throw new IOException(e);
    }
  }

  private void finishBuildingRules() {
    synchronized (rulesUnderConstruction) {
      for (RuleBuilder ruleBuilder : rulesUnderConstruction) {
        rules.add(ruleBuilder.toRule());
      }
      rulesUnderConstruction.clear();
    }
  }

  public void debugOn() {
    isDebuggingTurnOn = true;
  }

  public void debugOff() {
    isDebuggingTurnOn = false;
  }

  public void addRequestInterceptor(HttpRequestInterceptor requestInterceptor) {
    this.requestInterceptors.add(requestInterceptor);
  }

  public void addResponseInterceptor(HttpResponseInterceptor responseInterceptor) {
    this.responseInterceptors.add(responseInterceptor);
  }

  @Override
  public void close(CloseMode closeMode) {
  }

  @Override
  public void close() throws IOException {
  }
}
