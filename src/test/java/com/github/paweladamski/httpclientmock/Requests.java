package com.github.paweladamski.httpclientmock;

import java.io.UnsupportedEncodingException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;

class Requests {

  public static HttpUriRequest httpGet(String host) {
    return new HttpGet(host);
  }

  public static HttpUriRequest httpPost(String host) throws UnsupportedEncodingException {
    return httpPost(host, "");
  }

  public static HttpUriRequest httpPost(String host, String content) {
    HttpPost post = new HttpPost(host);
    HttpEntity entity = new StringEntity(content);
    post.setEntity(entity);
    return post;
  }

  public static HttpUriRequest httpPut(String host, String content) {
    HttpPut post = new HttpPut(host);
    HttpEntity entity = new StringEntity(content);
    post.setEntity(entity);
    return post;
  }
}
