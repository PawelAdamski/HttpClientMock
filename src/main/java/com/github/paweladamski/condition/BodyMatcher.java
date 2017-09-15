package com.github.paweladamski.condition;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.hamcrest.Matcher;

import java.io.IOException;

public class BodyMatcher implements Condition {

    Matcher<String> matcher;

    public BodyMatcher(Matcher<String> matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean matches(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) {
        try {
            HttpEntity entity = ((HttpEntityEnclosingRequestBase) httpRequest).getEntity();
            if (entity == null) {
                return false;
            }
            String message = EntityUtils.toString(entity);
            return matcher.matches(message);
        } catch (IOException e) {
            return false;
        }
    }
}
