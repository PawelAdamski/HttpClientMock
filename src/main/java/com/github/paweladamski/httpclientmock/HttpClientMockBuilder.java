package com.github.paweladamski.httpclientmock;

import com.github.paweladamski.httpclientmock.action.Action;
import com.github.paweladamski.httpclientmock.condition.BodyMatcher;
import com.github.paweladamski.httpclientmock.condition.Condition;
import com.github.paweladamski.httpclientmock.condition.HeaderCondition;
import org.hamcrest.Matcher;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.hamcrest.Matchers.equalTo;

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
     * @param value  expected value
     * @return condition builder
     */
    public HttpClientMockBuilder withHeader(String header, String value) {
        return withHeader(header, equalTo(value));
    }

    /**
     * Adds header condition. Header must be equal to provided value.
     *
     * @param header  header name
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
     * @param name  parameter name
     * @param value expected parameter value
     * @return condition builder
     */
    public HttpClientMockBuilder withParameter(String name, String value) {
        return withParameter(name, equalTo(value));
    }

    /**
     * Adds parameter condition. Parameter value must match.
     *
     * @param name    parameter name
     * @param matcher paramter value matcher
     * @return condition builder
     */
    public HttpClientMockBuilder withParameter(String name, Matcher<String> matcher) {
        ruleBuilder.addParameterCondition(name, matcher);
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
     * Adds action which returns provided response in provided charset and status 200.
     *
     * @param response response to return
     * @return response builder
     */
    public HttpClientResponseBuilder doReturn(String response, Charset charset) {
        return responseBuilder.doReturn(response, charset);
    }

    /**
     * Adds action which returns empty message and provided status.
     *
     * @param statusCode status to return
     * @return response builder
     */
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
     * Adds action which returns provided JSON in provided encoding and status 200. Additionally it sets "Content-type" header to "application/json".
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
     * Adds action which returns provided XML in UTF-8 and status 200. Additionally it sets "Content-type" header to "application/xml".
     *
     * @param response JSON to return
     * @return response builder
     */
    public HttpClientResponseBuilder doReturnXML(String response, Charset charset) {
        return responseBuilder.doReturnXML(response, charset);
    }

}
