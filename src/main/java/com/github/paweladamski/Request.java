package com.github.paweladamski;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;

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

}
