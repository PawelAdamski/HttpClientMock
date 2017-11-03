package com.github.paweladamski.httpclientmock;

import java.util.List;

public class HttpClientVerify {

    private final String defaultHost;
    private List<Request> requests;

    public HttpClientVerify(String defaultHost, List<Request> requests) {
        this.requests = requests;
        this.defaultHost = defaultHost;
    }

    private HttpClientVerifyBuilder newRule(String method) {
        RuleBuilder r = new RuleBuilder(method);
        return new HttpClientVerifyBuilder(r, requests);
    }

    private HttpClientVerifyBuilder newRule(String method, String url) {
        RuleBuilder r = new RuleBuilder(method, defaultHost, url);
        return new HttpClientVerifyBuilder(r, requests);
    }

    public HttpClientVerifyBuilder post(String url) {
        return newRule("POST", url);
    }

    public HttpClientVerifyBuilder get(String url) {
        return newRule("GET", url);
    }

    public HttpClientVerifyBuilder put(String url) {
        return newRule("PUT", url);
    }

    public HttpClientVerifyBuilder delete(String url) {
        return newRule("DELETE", url);
    }

    public HttpClientVerifyBuilder head(String url) {
        return newRule("HEAD", url);
    }

    public HttpClientVerifyBuilder options(String url) {
        return newRule("OPTIONS", url);
    }

    public HttpClientVerifyBuilder patch(String url) {
        return newRule("PATCH", url);
    }

    public HttpClientVerifyBuilder post() {
        return newRule("POST");
    }

    public HttpClientVerifyBuilder get() {
        return newRule("GET");
    }

    public HttpClientVerifyBuilder put() {
        return newRule("PUT");
    }

    public HttpClientVerifyBuilder delete() {
        return newRule("DELETE");
    }

    public HttpClientVerifyBuilder head() {
        return newRule("HEAD");
    }

    public HttpClientVerifyBuilder options() {
        return newRule("OPTIONS");
    }

    public HttpClientVerifyBuilder patch() {
        return newRule("PATCH");
    }

}
