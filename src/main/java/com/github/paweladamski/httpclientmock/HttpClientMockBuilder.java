package com.github.paweladamski.httpclientmock;

import static org.hamcrest.Matchers.equalTo;

import com.github.paweladamski.httpclientmock.action.Action;
import com.github.paweladamski.httpclientmock.condition.BodyMatcher;
import com.github.paweladamski.httpclientmock.condition.Condition;
import com.github.paweladamski.httpclientmock.condition.HeaderCondition;
import com.github.paweladamski.httpclientmock.matchers.ParametersMatcher;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;
import org.hamcrest.Matcher;

public class HttpClientMockBuilder {

  private final RuleBuilder ruleBuilder;
  private final HttpClientResponseBuilder responseBuilder;

  HttpClientMockBuilder(RuleBuilder rule) {
    this.ruleBuilder = rule;
    this.responseBuilder = new HttpClientResponseBuilder(rule);
  }

  /**
   * Adds header condition. Header must be equal to provided value.
   *
   * @param header header name
   * @param value expected value
   * @return condition builder
   */
  public HttpClientMockBuilder withHeader(String header, String value) {
    return withHeader(header, equalTo(value));
  }

  /**
   * Adds header condition. Header must be equal to provided value.
   *
   * @param header header name
   * @param matcher header value matcher
   * @return condition builder
   */
  public HttpClientMockBuilder withHeader(String header, Matcher<String> matcher) {
    ruleBuilder.addCondition(new HeaderCondition(header, matcher));
    return this;
  }

  /**
   * Adds reference condition. Reference must be equal to provided value.
   *
   * @param reference expected reference
   * @return conditions builder
   */
  public HttpClientMockBuilder withReference(String reference) {
    return withReference(equalTo(reference));
  }

  /**
   * Adds reference condition. Reference must match.
   *
   * @param matcher reference matcher
   * @return conditions builder
   */
  public HttpClientMockBuilder withReference(Matcher<String> matcher) {
    ruleBuilder.addReferenceCondition(matcher);
    return this;
  }

  /**
   * Adds parameter condition. Parameter must be equal to provided value.
   *
   * @param name parameter name
   * @param value expected parameter value
   * @return condition builder
   */
  public HttpClientMockBuilder withParameter(String name, String value) {
    return withParameter(name, equalTo(value));
  }

  /**
   * Adds parameter condition. Parameter value must match.
   *
   * @param name parameter name
   * @param matcher parameter value matcher
   * @return condition builder
   */
  public HttpClientMockBuilder withParameter(String name, Matcher<String> matcher) {
    ruleBuilder.addParameterCondition(name, matcher);
    return this;
  }

  /**
   * Request body must contain the given URL-encoded form parameter (typically found in POST requests). Alternatively, parameters may be specified all at once
   * using {@link #withFormParameters(ParametersMatcher)}.
   *
   * @param name parameter name
   * @param value expected parameter value
   * @return condition builder
   */
  public HttpClientMockBuilder withFormParameter(String name, String value) {
    return withFormParameter(name, equalTo(value));
  }

  /**
   * Request body must contain the given URL-encoded form parameter (typically found in POST requests). Alternatively, parameters may be specified all at once
   * using {@link #withFormParameters(ParametersMatcher)}.
   *
   * @param name parameter name
   * @param matcher parameter value matcher
   * @return condition builder
   */
  public HttpClientMockBuilder withFormParameter(String name, Matcher<String> matcher) {
    ruleBuilder.addFormParameterCondition(name, matcher);
    return this;
  }

  /**
   * Request body must contain the given URL-encoded form parameters (typically used in POST requests). Alternatively, parameters may be specified individually
   * using {@link #withFormParameter(String, Matcher)}.
   *
   * @param parameters the parameters
   * @return condition builder
   */
  public HttpClientMockBuilder withFormParameters(ParametersMatcher parameters) {
    ruleBuilder.addFormParameterConditions(parameters);
    return this;
  }

  /**
   * Adds custom conditions.
   *
   * @param condition custom condition
   * @return condition builder
   */
  public HttpClientMockBuilder with(Condition condition) {
    ruleBuilder.addCondition(condition);
    return this;
  }

  /**
   * Adds body condition. Request body must match provided matcher.
   *
   * @param matcher custom condition
   * @return condition builder
   */
  public HttpClientMockBuilder withBody(Matcher<String> matcher) {
    ruleBuilder.addCondition(new BodyMatcher(matcher));
    return this;
  }

  /**
   * Adds host condition. Request host must be equal to provided value.
   *
   * @param host expected host
   * @return condition builder
   */
  public HttpClientMockBuilder withHost(String host) {
    ruleBuilder.addHostCondition(host);
    return this;
  }

  /**
   * Adds path condition. Request path must be equal to provided value.
   *
   * @param path expected path
   * @return condition builder
   */
  public HttpClientMockBuilder withPath(String path) {
    return withPath(equalTo(path));
  }

  /**
   * Adds path condition. Request path must match.
   *
   * @param matcher path matcher
   * @return condition builder
   */
  public HttpClientMockBuilder withPath(Matcher<String> matcher) {
    ruleBuilder.addPathCondition(matcher);
    return this;
  }

  /**
   * Allows extra parameters (not defined in condition) in query.
   *
   * @return condition builder
   */
  public HttpClientMockBuilder withExtraParameters() {
    ruleBuilder.setAllowExtraParameters(true);
    return this;
  }

  /**
   * Disallows extra parameters (not defined in condition) in query.
   *
   * @return condition builder
   */
  public HttpClientMockBuilder withoutExtraParameters() {
    ruleBuilder.setAllowExtraParameters(false);
    return this;
  }

  /**
   * Allows extra parameters (not defined in condition) in  form.
   *
   * @return condition builder
   */
  public HttpClientMockBuilder withExtraFormParameters() {
    ruleBuilder.setAllowExtraFormParameters(true);
    return this;
  }

  /**
   * Disallows extra parameters (not defined in condition) in  form.
   *
   * @return condition builder
   */
  public HttpClientMockBuilder withoutExtraFormParameters() {
    ruleBuilder.setAllowExtraFormParameters(false);
    return this;
  }

  /**
   * Adds custom action.
   *
   * @param action custom action
   * @return response builder
   */
  public HttpClientResponseBuilder doAction(Action action) {
    return responseBuilder.doAction(action);
  }

  /**
   * Adds action which returns provided response in UTF-8 and status 200.
   *
   * @param response response to return
   * @return response builder
   */
  public HttpClientResponseBuilder doReturn(String response) {
    return responseBuilder.doReturn(response);
  }

  /**
   * Adds action which returns provided response and status in UTF-8.
   *
   * @param statusCode status to return
   * @param response response to return
   * @return response builder
   */
  public HttpClientResponseBuilder doReturn(int statusCode, String response) {
    return responseBuilder.doReturn(statusCode, response);
  }

  /**
   * Adds action which returns provided response in provided charset and status 200.
   *
   * @param response response to return
   * @param charset charset to return
   * @return response builder
   */
  public HttpClientResponseBuilder doReturn(String response, Charset charset) {
    return responseBuilder.doReturn(response, charset);
  }

  /**
   * Adds action which returns provided response in provided charset and status.
   *
   * @param statusCode status to return
   * @param response response to return
   * @param charset the charset
   * @return response builder
   */
  public HttpClientResponseBuilder doReturn(int statusCode, String response, Charset charset) {
    return responseBuilder.doReturn(statusCode, response, charset);
  }

  /**
   * Adds action which returns provided response in provided charset, content-type and status 200.
   *
   * @param response response to return
   * @return response builder
   */
  public HttpClientResponseBuilder doReturn(String response, Charset charset, ContentType contentType) {
    return responseBuilder.doReturn(response, charset, contentType);
  }

  /**
   * Adds action which returns provided status and null entity.
   *
   * @param statusCode status to return
   * @return response builder
   */
  public HttpClientResponseBuilder doReturnWithStatus(int statusCode) {
    return responseBuilder.doReturnWithStatus(statusCode);
  }

  /**
   * Adds action which returns provided status with reason and null entity.
   *
   * @param statusCode status to return
   * @param reason reason to return
   * @return response builder
   */
  public HttpClientResponseBuilder doReturnWithStatus(int statusCode, String reason) {
    return responseBuilder.doReturnWithStatus(statusCode, reason);
  }

  /**
   * Adds action which returns empty message and provided status.
   *
   * @param statusCode status to return
   * @return response builder
   * @deprecated use doReturnWithStatus instead
   */
  @Deprecated
  public HttpClientResponseBuilder doReturnStatus(int statusCode) {
    return responseBuilder.doReturnStatus(statusCode);
  }

  /**
   * Adds action which throws provided exception.
   *
   * @param exception exception to be thrown
   * @return response builder
   */
  public HttpClientResponseBuilder doThrowException(IOException exception) {
    return responseBuilder.doThrowException(exception);
  }

  /**
   * Adds action which returns provided JSON in UTF-8 and status 200. Additionally it sets "Content-type" header to "application/json".
   *
   * @param response JSON to return
   * @return response builder
   */
  public HttpClientResponseBuilder doReturnJSON(String response) {
    return responseBuilder.doReturnJSON(response);
  }

  /**
   * Adds action which returns provided JSON in provided charset and status 200. Additionally it sets "Content-type" header to "application/json".
   *
   * @param response JSON to return
   * @return response builder
   */
  public HttpClientResponseBuilder doReturnJSON(String response, Charset charset) {
    return responseBuilder.doReturnJSON(response, charset);
  }

  /**
   * Adds action which returns provided XML in UTF-8 and status 200. Additionally it sets "Content-type" header to "application/xml".
   *
   * @param response JSON to return
   * @return response builder
   */
  public HttpClientResponseBuilder doReturnXML(String response) {
    return responseBuilder.doReturnXML(response);
  }

  /**
   * Adds action which returns provided XML in provided charset and status 200. Additionally it sets "Content-type" header to "application/xml".
   *
   * @param response JSON to return
   * @return response builder
   */
  public HttpClientResponseBuilder doReturnXML(String response, Charset charset) {
    return responseBuilder.doReturnXML(response, charset);
  }

  /**
   * Adds action which returns provided URL-encoded parameter response in UTF-8 and status 200. Additionally it sets "Content-type" header to
   * "application/x-www-form-urlencoded".
   *
   * @param parameters parameters to return
   * @return response builder
   */
  public HttpClientResponseBuilder doReturnFormParams(Collection<NameValuePair> parameters) {
    return doReturnFormParams(parameters, StandardCharsets.UTF_8);
  }

  /**
   * Adds action which returns provided URL-encoded parameter response in provided charset and status 200. Additionally it sets "Content-type" header to
   * "application/x-www-form-urlencoded".
   *
   * @param parameters parameters to return
   * @return response builder
   */
  public HttpClientResponseBuilder doReturnFormParams(Collection<NameValuePair> parameters, Charset charset) {
    return responseBuilder.doReturnFormParams(parameters, charset);
  }


}
