package com.github.paweladamski.httpclientmock.condition;

import com.github.paweladamski.httpclientmock.Debugger;
import com.github.paweladamski.httpclientmock.Request;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.protocol.HttpContext;

public interface Condition {

  default boolean matches(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) {
    return matches(new Request(httpHost, httpRequest, httpContext));
  }

  boolean matches(Request request);

  default void debug(Request request, Debugger debugger) {
    debugger.message(matches(request), getClass().getSimpleName());
  }

}
