package com.github.paweladamski.httpclientmock;

import com.github.paweladamski.httpclientmock.action.Action;
import com.github.paweladamski.httpclientmock.action.ExceptionAction;
import com.github.paweladamski.httpclientmock.action.HeaderAction;
import com.github.paweladamski.httpclientmock.action.StatusResponse;
import com.github.paweladamski.httpclientmock.action.StringResponse;

import java.io.IOException;
import java.nio.charset.Charset;

public class HttpClientResponseBuilder {

    private final RuleBuilder newRule;

    HttpClientResponseBuilder(RuleBuilder rule) {
        this.newRule = rule;
    }

    public HttpClientResponseBuilder doReturn(String response) {
        newRule.addAction(new StringResponse(response, Charset.forName("UTF-8")));
        return this;
    }

    public HttpClientResponseBuilder doReturn(String response, Charset charset) {
        newRule.addAction(new StringResponse(response, charset));
        return this;
    }

    public HttpClientResponseBuilder doReturnStatus(int statusCode) {
        newRule.addAction(new StatusResponse(statusCode));
        return this;
    }

    public HttpClientResponseBuilder doThrowException(IOException e) {
        newRule.addAction(new ExceptionAction(e));
        return this;
    }

    public HttpClientResponseBuilder withHeader(String name, String value) {
        Action lastAction = newRule.getLastAction();
        HeaderAction headerAction = new HeaderAction(lastAction, name, value);
        newRule.overrideLastAction(headerAction);
        return this;
    }

    public HttpClientResponseBuilder withStatus(int statusCode) {
        Action lastAction = newRule.getLastAction();
        StatusResponse statusAction = new StatusResponse(lastAction, statusCode);
        newRule.overrideLastAction(statusAction);
        return this;
    }
}
