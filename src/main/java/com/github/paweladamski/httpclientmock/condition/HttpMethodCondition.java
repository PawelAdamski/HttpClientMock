package com.github.paweladamski.httpclientmock.condition;

import com.github.paweladamski.httpclientmock.Debugger;
import com.github.paweladamski.httpclientmock.Request;

public class HttpMethodCondition implements Condition {

  private final String method;

  public HttpMethodCondition(String method) {
    this.method = method;
  }

  @Override
  public boolean matches(Request request) {
    return request.getHttpRequest().getRequestLine().getMethod().equals(method);
  }

  @Override
  public void debug(Request request, Debugger debugger) {
    debugger.message(matches(request), "HTTP method is " + method);
  }
}
