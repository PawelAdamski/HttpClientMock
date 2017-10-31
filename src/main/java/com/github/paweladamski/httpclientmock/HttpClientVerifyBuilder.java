package com.github.paweladamski.httpclientmock;

import com.github.paweladamski.httpclientmock.condition.BodyMatcher;
import com.github.paweladamski.httpclientmock.condition.Condition;
import com.github.paweladamski.httpclientmock.condition.HttpMethodCondition;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.ArrayList;
import java.util.List;

public class HttpClientVerifyBuilder {

    private UrlConditions urlConditions;
    private final List<Condition> conditions = new ArrayList<>();
    private final List<Request> requests;
    private final String host;

    public HttpClientVerifyBuilder(String host, List<Request> requests) {
        this.requests = requests;
        this.host = host;
    }

    private HttpClientVerifyBuilder newRule(String method, String url) {
        conditions.add(new HttpMethodCondition(method));
        urlConditions = new UrlParser().parse(host + url);
        return this;
    }

    public HttpClientVerifyBuilder post(String url) {
        return newRule("POST", url);
    }

    public HttpClientVerifyBuilder get(String url) {
        return newRule("GET", url);
    }

    public HttpClientVerifyBuilder put(String url) {
        return newRule("PUT", url);
    }

    public HttpClientVerifyBuilder delete(String url) {
        return newRule("DELETE", url);
    }

    public HttpClientVerifyBuilder head(String url) {
        return newRule("HEAD", url);
    }

    public HttpClientVerifyBuilder options(String url) {
        return newRule("OPTIONS", url);
    }

    public HttpClientVerifyBuilder patch(String url) {
        return newRule("PATCH", url);
    }

    public HttpClientVerifyBuilder withParameter(String name, String value) {
        urlConditions.getParameterConditions().put(name, Matchers.equalTo(value));
        return this;
    }

    public HttpClientVerifyBuilder withBody(Matcher<String> matcher) {
        conditions.add(new BodyMatcher(matcher));
        return this;
    }

    public void notCalled() {
        called(0);
    }

    public void called() {
        called(1);
    }

    public void called(int numberOfCalls) {
        int matchingCalls = 0;
        for (Request request : requests) {
            boolean matches = urlConditions.matches(request.getHttpRequest().getRequestLine().getUri())
                    && conditions.stream()
                    .allMatch(condition -> condition.matches(request));
            if (matches) {
                matchingCalls++;
            }
        }
        if (matchingCalls != numberOfCalls) {
            throw new IllegalStateException(String.format("Expected %s calls, but found %s.", numberOfCalls, matchingCalls));
        }
    }
}
