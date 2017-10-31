package com.github.paweladamski.httpclientmock;

import com.github.paweladamski.httpclientmock.action.Action;
import com.github.paweladamski.httpclientmock.action.ExceptionAction;
import com.github.paweladamski.httpclientmock.action.StatusResponse;
import com.github.paweladamski.httpclientmock.action.StringResponse;
import com.github.paweladamski.httpclientmock.condition.BodyMatcher;
import com.github.paweladamski.httpclientmock.condition.Condition;
import com.github.paweladamski.httpclientmock.condition.HeaderCondition;
import org.hamcrest.Matcher;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.apache.http.entity.ContentType.APPLICATION_XML;
import static org.hamcrest.Matchers.equalTo;

public class HttpClientMockBuilder extends HttpClientMock {

    private final RuleBuilder newRule;

    HttpClientMockBuilder(RuleBuilder rule) {
        this.newRule = rule;
    }

    public HttpClientMockBuilder withHeader(String header, String value) {
        newRule.addCondition(new HeaderCondition(header, equalTo(value)));
        return this;
    }

    public HttpClientMockBuilder withReference(String ref) {
        UrlConditions urlConditions = new UrlConditions();
        urlConditions.setReferenceConditions(equalTo(ref));
        newRule.addUrlConditions(urlConditions);
        return this;
    }

    public HttpClientMockBuilder withParameter(String parameter, String value) {
        UrlConditions urlConditions = new UrlConditions();
        urlConditions.getParameterConditions().put(parameter, equalTo(value));
        newRule.addUrlConditions(urlConditions);
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

    public HttpClientResponseBuilder doReturn(String response) {
        return doReturn(response, Charset.forName("UTF-8"));
    }

    public HttpClientResponseBuilder doReturn(String response, Charset charset) {
        newRule.addAction(new StringResponse(response, charset));
        return new HttpClientResponseBuilder(newRule);
    }

    public HttpClientResponseBuilder doReturnStatus(int statusCode) {
        newRule.addAction(new StatusResponse(statusCode));
        return new HttpClientResponseBuilder(newRule);
    }

    public HttpClientResponseBuilder doThrowException(IOException e) {
        newRule.addAction(new ExceptionAction(e));
        return new HttpClientResponseBuilder(newRule);
    }

    public HttpClientMockBuilder withBody(Matcher<String> foo) {
        newRule.addCondition(new BodyMatcher(foo));
        return this;
    }

    public HttpClientResponseBuilder doReturnJSON(String response) {
        return doReturnJSON(response, Charset.forName("UTF-8"));
    }

    public HttpClientResponseBuilder doReturnJSON(String response, Charset charset) {
        return doReturn(response, charset).withHeader("Content-type", APPLICATION_JSON.toString());
    }

    public HttpClientResponseBuilder doReturnXML(String response) {
        return doReturnXML(response, Charset.forName("UTF-8"));
    }

    public HttpClientResponseBuilder doReturnXML(String response, Charset charset) {
        return doReturn(response, charset).withHeader("Content-type", APPLICATION_XML.toString());
    }

    public HttpClientMockBuilder withHost(String host) {
        UrlParser urlParser = new UrlParser();
        UrlConditions urlConditions = new UrlConditions();
        urlConditions.setHostConditions(urlParser.parse(host).getHostConditions());
        newRule.addUrlConditions(urlConditions);
        return this;
    }

    public HttpClientMockBuilder withPath(String path) {
        UrlConditions urlConditions = new UrlConditions();
        urlConditions.getPathConditions().add(equalTo(path));
        newRule.addUrlConditions(urlConditions);
        return this;
    }
}
