package com.github.paweladamski.httpclientmock;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.protocol.HttpContext;

public class Request {

  private final HttpHost httpHost;
  private final HttpRequest httpRequest;
  private final HttpContext httpContext;

  public Request(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) {

    this.httpHost = httpHost;
    this.httpRequest = httpRequest;
    this.httpContext = httpContext;
  }

  public HttpHost getHttpHost() {
    return httpHost;
  }

  public HttpRequest getHttpRequest() {
    return httpRequest;
  }

  public HttpContext getHttpContext() {
    return httpContext;
  }

  public URI getUri() {
    try {
      return getHttpRequest().getUri();
    } catch (URISyntaxException e) {
      throw new IllegalStateException(e);
    }
  }

}
