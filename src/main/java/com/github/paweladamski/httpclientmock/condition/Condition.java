package com.github.paweladamski.httpclientmock.condition;

import com.github.paweladamski.httpclientmock.Request;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;

public interface Condition {
    boolean matches(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext);

    default boolean matches(Request request) {
        return matches(request.getHttpHost(), request.getHttpRequest(), request.getHttpContext());
    }
}
