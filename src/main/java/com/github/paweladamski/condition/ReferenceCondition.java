package com.github.paweladamski.condition;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;
import org.hamcrest.Matcher;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class ReferenceCondition implements Condition {

    Matcher<String> matcher;

    public ReferenceCondition(Matcher<String> matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean matches(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) {
        try {
            String reference = new URL(httpRequest.getRequestLine().getUri()).getRef();
            return matcher.matches(reference);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
