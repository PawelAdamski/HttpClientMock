package com.github.paweladamski.httpclientmock;

import java.util.List;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

public class HttpClientVerify {

  private final String defaultHost;
  private final List<Request> requests;

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
    return newRule(HttpPost.METHOD_NAME, url);
  }

  public HttpClientVerifyBuilder get(String url) {
    return newRule(HttpGet.METHOD_NAME, url);
  }

  public HttpClientVerifyBuilder put(String url) {
    return newRule(HttpPut.METHOD_NAME, url);
  }

  public HttpClientVerifyBuilder delete(String url) {
    return newRule(HttpDelete.METHOD_NAME, url);
  }

  public HttpClientVerifyBuilder head(String url) {
    return newRule(HttpHead.METHOD_NAME, url);
  }

  public HttpClientVerifyBuilder options(String url) {
    return newRule(HttpOptions.METHOD_NAME, url);
  }

  public HttpClientVerifyBuilder patch(String url) {
    return newRule(HttpPatch.METHOD_NAME, url);
  }

  public HttpClientVerifyBuilder post() {
    return newRule(HttpPost.METHOD_NAME);
  }

  public HttpClientVerifyBuilder get() {
    return newRule(HttpGet.METHOD_NAME);
  }

  public HttpClientVerifyBuilder put() {
    return newRule(HttpPut.METHOD_NAME);
  }

  public HttpClientVerifyBuilder delete() {
    return newRule(HttpDelete.METHOD_NAME);
  }

  public HttpClientVerifyBuilder head() {
    return newRule(HttpHead.METHOD_NAME);
  }

  public HttpClientVerifyBuilder options() {
    return newRule(HttpOptions.METHOD_NAME);
  }

  public HttpClientVerifyBuilder patch() {
    return newRule(HttpPatch.METHOD_NAME);
  }

}
