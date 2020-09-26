package com.github.paweladamski.httpclientmock.action;

import com.github.paweladamski.httpclientmock.Request;
import java.io.IOException;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;

public class CookieAction implements Action {

  private final Action parentAction;
  private final String cookieName;
  private final String cookieValue;

  public CookieAction(Action parentAction, String cookieName, String cookieValue) {
    this.parentAction = parentAction;
    this.cookieName = cookieName;
    this.cookieValue = cookieValue;
  }

  @Override
  public ClassicHttpResponse getResponse(Request request) throws IOException {
    ClassicHttpResponse response = parentAction.getResponse(request);

    if (request.getHttpContext() == null) {
      throw new RuntimeException("No Http context");
    }
    if (!(request.getHttpContext() instanceof HttpClientContext)) {
      throw new RuntimeException("Http context is not a HttpClientContext instance.");
    }
    HttpClientContext httpClientContext = (HttpClientContext) request.getHttpContext();
    if (httpClientContext.getCookieStore() == null) {
      httpClientContext.setCookieStore(new BasicCookieStore());
    }
    httpClientContext.getCookieStore().addCookie(new BasicClientCookie(cookieName, cookieValue));

    return response;
  }

}
