package com.github.paweladamski.httpclientmock;

import com.github.paweladamski.httpclientmock.action.Action;
import com.github.paweladamski.httpclientmock.action.ExceptionAction;
import com.github.paweladamski.httpclientmock.action.HeaderAction;
import com.github.paweladamski.httpclientmock.action.StatusResponse;
import com.github.paweladamski.httpclientmock.action.StringResponse;

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
        return doReturn(response, charset).withHeader("Content-type", APPLICATION_JSON.toString());
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
        return doReturn(response, charset).withHeader("Content-type", APPLICATION_XML.toString());
    }

}
