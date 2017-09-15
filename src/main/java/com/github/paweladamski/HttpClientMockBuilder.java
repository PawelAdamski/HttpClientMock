package com.github.paweladamski;

import com.github.paweladamski.action.ExceptionAction;
import com.github.paweladamski.action.StatusResponse;
import com.github.paweladamski.action.StringResponse;
import com.github.paweladamski.condition.BodyMatcher;
import com.github.paweladamski.condition.HeaderCondition;
import com.github.paweladamski.condition.ParameterCondition;
import org.hamcrest.Matcher;

import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;

public class HttpClientMockBuilder extends HttpClientMock {

    private final Rule newRule;

    public HttpClientMockBuilder(Rule rule) {
        this.newRule = rule;
    }

    public HttpClientMockBuilder withHeader(String header, String value) {
        newRule.addCondition(new HeaderCondition(header, equalTo(value)));
        return this;
    }

    public HttpClientMockBuilder withParameter(String parameter, String value) {
        newRule.addCondition(new ParameterCondition(parameter, equalTo(value)));
        return this;
    }

    public HttpClientMockBuilder doReturn(String response) {
        newRule.addAction(new StringResponse(response));
        return this;
    }

    public void doReturnStatus(int statusCode) {
        newRule.addAction(new StatusResponse(statusCode));
    }

    public HttpClientMockBuilder doThrowException(IOException e) {
        newRule.addAction(new ExceptionAction(e));
        return this;
    }

    public HttpClientMockBuilder withBody(Matcher<String> foo) {
        newRule.addCondition(new BodyMatcher(foo));
        return this;
    }
}
