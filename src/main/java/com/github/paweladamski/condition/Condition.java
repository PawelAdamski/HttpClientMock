package com.github.paweladamski.condition;

import com.github.paweladamski.Call;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;

public interface Condition {
    boolean matches(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext);

    default boolean matches(Call call) {
        return matches(call.getHttpHost(), call.getHttpRequest(), call.getHttpContext());
    }
}
