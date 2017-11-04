package com.github.paweladamski.httpclientmock.condition;

import com.github.paweladamski.httpclientmock.Request;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;

public interface Condition {
    default boolean matches(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) {
        return matches(new Request(httpHost, httpRequest, httpContext));
    }

    boolean matches(Request request);
}
