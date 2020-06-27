package com.github.paweladamski.httpclientmock;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.apache.http.entity.ContentType.APPLICATION_XML;

import com.github.paweladamski.httpclientmock.action.Action;
import com.github.paweladamski.httpclientmock.action.CookieAction;
import com.github.paweladamski.httpclientmock.action.ExceptionAction;
import com.github.paweladamski.httpclientmock.action.HeaderAction;
import com.github.paweladamski.httpclientmock.action.StatusResponse;
import com.github.paweladamski.httpclientmock.action.StringResponse;
import com.github.paweladamski.httpclientmock.action.UrlEncodedFormEntityResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;

public class HttpClientResponseBuilder {

  private final RuleBuilder newRule;

  HttpClientResponseBuilder(RuleBuilder rule) {
    this.newRule = rule;
  }

  /**
   * Sets response header.
   *
   * @param name header name
   * @param value header value
   * @return response builder
   */
  public HttpClientResponseBuilder withHeader(String name, String value) {
    Action lastAction = newRule.getLastAction();
    HeaderAction headerAction = new HeaderAction(lastAction, name, value);
    newRule.overrideLastAction(headerAction);
    return this;
  }

  /**
   * Sets response status code.
   *
   * @param statusCode response status code
   * @return response builder
   */
  public HttpClientResponseBuilder withStatus(int statusCode) {
    Action lastAction = newRule.getLastAction();
    StatusResponse statusAction = new StatusResponse(lastAction, statusCode);
    newRule.overrideLastAction(statusAction);
    return this;
  }

  /**
   * Sets response cookie
   *
   * @param cookieName cookie name
   * @param cookieValue cookie value
   * @return response builder
   */
  public HttpClientResponseBuilder withCookie(String cookieName, String cookieValue) {
    Action lastAction = newRule.getLastAction();
    CookieAction cookieAction = new CookieAction(lastAction, cookieName, cookieValue);
    newRule.overrideLastAction(cookieAction);
    return this;
  }

  /**
   * Adds custom action.
   *
   * @param action custom action
   * @return response builder
   */
  public HttpClientResponseBuilder doAction(Action action) {
    newRule.addAction(action);
    return new HttpClientResponseBuilder(newRule);
  }

  /**
   * Adds action which returns provided response in UTF-8 and status 200.
   *
   * @param response response to return
   * @return response builder
   */
  public HttpClientResponseBuilder doReturn(String response) {
    return doReturn(response, StandardCharsets.UTF_8);
  }

  /**
   * Adds action which returns provided response in UTF-8 with status code.
   *
   * @param statusCode status to return
   * @param response response to return
   * @return response builder
   */
  public HttpClientResponseBuilder doReturn(int statusCode, String response) {
    return doReturn(statusCode, response, StandardCharsets.UTF_8);
  }

  /**
   * Adds action which returns provided response in provided charset and status 200.
   *
   * @param response response to return
   * @return response builder
   */
  public HttpClientResponseBuilder doReturn(String response, Charset charset) {
    newRule.addAction(new StringResponse(response, charset));
    return new HttpClientResponseBuilder(newRule);
  }

  /**
   * Adds action which returns provided response in provided charset, content type and status 200.
   *
   * @param response response to return
   * @return response builder
   */
  public HttpClientResponseBuilder doReturn(String response, Charset charset, ContentType contentType) {
    newRule.addAction(new StringResponse(response, charset, contentType));
    return new HttpClientResponseBuilder(newRule);
  }

  /**
   * Adds action which returns provided response in provided charset and status code.
   *
   * @param statusCode status to return
   * @param response response to return
   * @param charset the charset
   * @return response builder
   */
  public HttpClientResponseBuilder doReturn(int statusCode, String response, Charset charset) {
    newRule.addAction(new StringResponse(statusCode, response, charset));
    return new HttpClientResponseBuilder(newRule);
  }

  /**
   * Adds action which returns empty message and provided status.
   *
   * @param statusCode status to return
   * @return response builder
   */
  public HttpClientResponseBuilder doReturnStatus(int statusCode) {
    newRule.addAction(new StatusResponse(statusCode));
    return new HttpClientResponseBuilder(newRule);
  }

  /**
   * Adds action which throws provided exception.
   *
   * @param exception exception to be thrown
   * @return response builder
   */
  public HttpClientResponseBuilder doThrowException(IOException exception) {
    newRule.addAction(new ExceptionAction(exception));
    return new HttpClientResponseBuilder(newRule);
  }

  /**
   * Adds action which returns provided JSON in UTF-8 and status 200. Additionally it sets "Content-type" header to "application/json".
   *
   * @param response JSON to return
   * @return response builder
   */
  public HttpClientResponseBuilder doReturnJSON(String response) {
    return doReturnJSON(response, StandardCharsets.UTF_8);
  }

  /**
   * Adds action which returns provided JSON in provided encoding and status 200. Additionally it sets "Content-type" header to "application/json".
   *
   * @param response JSON to return
   * @return response builder
   */
  public HttpClientResponseBuilder doReturnJSON(String response, Charset charset) {
    return doReturn(response, charset, APPLICATION_JSON);
  }

  /**
   * Adds action which returns provided XML in UTF-8 and status 200. Additionally it sets "Content-type" header to "application/xml".
   *
   * @param response JSON to return
   * @return response builder
   */
  public HttpClientResponseBuilder doReturnXML(String response) {
    return doReturnXML(response, StandardCharsets.UTF_8);
  }

  /**
   * Adds action which returns provided XML in UTF-8 and status 200. Additionally it sets "Content-type" header to "application/xml".
   *
   * @param response JSON to return
   * @return response builder
   */
  public HttpClientResponseBuilder doReturnXML(String response, Charset charset) {
    return doReturn(response, charset, APPLICATION_XML);
  }

  /**
   * Adds action which returns provided name/value pairs as URL-encoded form response in UTF-8 and status 200. Additionally it sets "Content-type" header to
   * "application/x-www-form-urlencoded".
   *
   * @param formParameters the parameters to include in the response
   * @return response builder
   */
  public HttpClientResponseBuilder doReturnFormParams(Collection<NameValuePair> formParameters) {
    return doReturnFormParams(formParameters, StandardCharsets.UTF_8);
  }

  /**
   * Adds action which returns provided name/value pairs as URL-encoded form response and status 200. Additionally it sets "Content-type" header to
   * "application/x-www-form-urlencoded".
   *
   * @param formParameters the parameters to include in the response
   * @return response builder
   */
  public HttpClientResponseBuilder doReturnFormParams(Collection<NameValuePair> formParameters, Charset charset) {
    newRule.addAction(new UrlEncodedFormEntityResponse(formParameters, charset));
    return new HttpClientResponseBuilder(newRule);
  }

}
