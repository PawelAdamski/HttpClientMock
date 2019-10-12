package com.github.paweladamski.httpclientmock;

import com.github.paweladamski.httpclientmock.action.*;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.apache.http.entity.ContentType.APPLICATION_XML;

public class HttpClientResponseBuilder {

    private final RuleBuilder newRule;

    HttpClientResponseBuilder(RuleBuilder rule) {
        this.newRule = rule;
    }

    /**
     * Sets response header.
     *
     * @param name  header name
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
     * @param cookieName  cookie name
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
        return doReturn(response, Charset.forName("UTF-8"));
    }

    /**
     * Adds action which returns provided response in UTF-8 with status code.
     *
     * @param statusCode status to return
     * @param response   response to return
     * @return response builder
     */
    public HttpClientResponseBuilder doReturn(int statusCode, String response) {
        return doReturn(statusCode, response, Charset.forName("UTF-8"));
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

    public HttpClientResponseBuilder doReturn(String response, Charset charset, ContentType contentType) {
        newRule.addAction(new StringResponse(response, charset, contentType));
        return new HttpClientResponseBuilder(newRule);
    }


    /**
     * Adds action which returns provided response in provided charset and status code.
     *
     * @param statusCode status to return
     * @param response   response to return
     * @param charset    the charset
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
        return doReturnJSON(response, Charset.forName("UTF-8"));
    }

    /**
     * Adds action which returns provided JSON in provided encoding and status 200. Additionally it sets "Content-type" header to "application/json".
     *
     * @param response JSON to return
     * @return response builder
     */
    public HttpClientResponseBuilder doReturnJSON(String response, Charset charset) {
        return doReturn(response, charset, APPLICATION_JSON).withHeader("Content-type", APPLICATION_JSON.toString());
    }

    /**
     * Adds action which returns provided XML in UTF-8 and status 200. Additionally it sets "Content-type" header to "application/xml".
     *
     * @param response JSON to return
     * @return response builder
     */
    public HttpClientResponseBuilder doReturnXML(String response) {
        return doReturnXML(response, Charset.forName("UTF-8"));
    }

    /**
     * Adds action which returns provided XML in UTF-8 and status 200. Additionally it sets "Content-type" header to "application/xml".
     *
     * @param response JSON to return
     * @return response builder
     */
    public HttpClientResponseBuilder doReturnXML(String response, Charset charset) {
        return doReturn(response, charset, APPLICATION_XML).withHeader("Content-type", APPLICATION_XML.toString());
    }

}
