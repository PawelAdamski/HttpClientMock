package com.github.paweladamski.httpclientmock;

import java.io.UnsupportedEncodingException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;

class Requests {

  public static HttpUriRequest httpGet(String host) {
    return new HttpGet(host);
  }

  public static HttpUriRequest httpPost(String host) throws UnsupportedEncodingException {
    return httpPost(host, "");
  }

  public static HttpUriRequest httpPost(String host, String content) throws UnsupportedEncodingException {
    HttpPost post = new HttpPost(host);
    HttpEntity entity = new StringEntity(content);
    post.setEntity(entity);
    return post;
  }

  public static HttpUriRequest httpPut(String host, String content) throws UnsupportedEncodingException {
    HttpPut post = new HttpPut(host);
    HttpEntity entity = new StringEntity(content);
    post.setEntity(entity);
    return post;
  }
}
