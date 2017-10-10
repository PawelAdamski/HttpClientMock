package com.github.paweladamski;

import com.github.paweladamski.condition.BodyMatcher;
import com.github.paweladamski.condition.Condition;
import com.github.paweladamski.condition.HostCondition;
import com.github.paweladamski.condition.HttpMethodCondition;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.List;

public class HttpClientVerifyBuilder {

    private final List<Condition> conditions = new ArrayList<>();
    private final List<Request> requests;
    private final String host;

    public HttpClientVerifyBuilder(String host, List<Request> requests) {
        this.requests = requests;
        this.host = host;
    }

    private HttpClientVerifyBuilder newRule(String method, String url) {
        conditions.add(new HttpMethodCondition(method));
        List<Condition> urlConditions = new UrlParser().parse(host+url);
        conditions.addAll(urlConditions);
        return this;
    }

    public HttpClientVerifyBuilder post(String url) {
        return newRule("POST", url);
    }

    HttpClientVerifyBuilder get(String url) {
        return newRule("GET", url);
    }

    HttpClientVerifyBuilder put(String url) {
        return newRule("PUT", url);
    }

    HttpClientVerifyBuilder delete(String url) {
        return newRule("DELETE", url);
    }

    public HttpClientVerifyBuilder withBody(Matcher<String> matcher) {
        conditions.add(new BodyMatcher(matcher));
        return this;
    }

    void notCalled() {
        called(0);
    }

    public void called() {
        called(1);
    }

    public void called(int numberOfCalls) {
        int matchingCalls = 0;
        for (Request request : requests) {
            boolean matches = conditions.stream()
                    .allMatch(condition -> condition.matches(request));
            if (matches) {
                matchingCalls++;
            }
        }
        if (matchingCalls != numberOfCalls) {
            throw new IllegalStateException(String.format("Expected %s calls, but found %s.",numberOfCalls,matchingCalls));
        }
    }
}
