package com.github.paweladamski;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;

public class Call {

    HttpHost httpHost;
    HttpRequest httpRequest;
    HttpContext httpContext;

    public Call(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) {

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
