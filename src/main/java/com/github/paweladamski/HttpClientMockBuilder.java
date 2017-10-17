package com.github.paweladamski;

import com.github.paweladamski.action.Action;
import com.github.paweladamski.action.ExceptionAction;
import com.github.paweladamski.action.StatusResponse;
import com.github.paweladamski.action.StringResponse;
import com.github.paweladamski.condition.BodyMatcher;
import com.github.paweladamski.condition.Condition;
import com.github.paweladamski.condition.HeaderCondition;
import com.github.paweladamski.condition.HostCondition;
import com.github.paweladamski.condition.ParameterCondition;
import com.github.paweladamski.condition.ReferenceCondition;
import org.hamcrest.Matcher;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.hamcrest.Matchers.equalTo;

public class HttpClientMockBuilder extends HttpClientMock {

    private final Rule newRule;

     HttpClientMockBuilder(Rule rule) {
        this.newRule = rule;
    }

    public HttpClientMockBuilder withHeader(String header, String value) {
        newRule.addCondition(new HeaderCondition(header, equalTo(value)));
        return this;
    }

    HttpClientMockBuilder withReference(String ref) {
        newRule.addReferenceCondition(equalTo(ref));
        return this;
    }

    public HttpClientMockBuilder withParameter(String parameter, String value) {
        newRule.addParameterCondition(parameter, equalTo(value));
        return this;
    }

    public HttpClientMockBuilder with(Condition condition) {
        newRule.addCondition(condition);
        return this;
    }

    @Override
    public HttpClientMockBuilder onDelete(String url) {
        return super.onDelete(url);
    }

    public HttpClientMockBuilder doAction(Action action) {
        newRule.addAction(action);
        return this;
    }

    public HttpClientMockBuilder doReturn(String response) {
        newRule.addAction(new StringResponse(response, Charset.forName("UTF-8")));
        return this;
    }

    public HttpClientMockBuilder doReturn(String response, Charset charset) {
        newRule.addAction(new StringResponse(response, charset));
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
