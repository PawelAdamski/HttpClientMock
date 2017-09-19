package com.github.paweladamski;

import com.github.paweladamski.condition.BodyMatcher;
import com.github.paweladamski.condition.Condition;
import com.github.paweladamski.condition.HostCondition;
import com.github.paweladamski.condition.HttpMethodCondition;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.List;

public class HttpClientVerifyBuilder {

    List<Condition> conditions = new ArrayList<>();
    List<Call> calls;
    private final String host;

    public HttpClientVerifyBuilder(String host, List<Call> calls) {
        this.calls = calls;
        this.host = host;
    }

    private HttpClientVerifyBuilder newRule(String method, String url) {
        conditions.add(new HttpMethodCondition(method));
        conditions.add(new HostCondition(host + url));
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
        for (Call call : calls) {
            boolean matches = conditions.stream()
                    .allMatch(condition -> condition.matches(call));
            if (matches) {
                matchingCalls++;
            }
        }
        if (matchingCalls != numberOfCalls) {
            throw new IllegalStateException("Expected different number of calls.");
        }
    }
}
