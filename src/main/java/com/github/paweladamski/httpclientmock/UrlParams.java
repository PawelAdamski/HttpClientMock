package com.github.paweladamski.httpclientmock;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public class UrlParams extends ArrayList<NameValuePair> {

  public static UrlParams parse(String query) {
    return parse(query, StandardCharsets.UTF_8);
  }

  public static UrlParams parse(String query, Charset charset) {
    if (query == null) {
      return new UrlParams();
    } else {
      UrlParams urlParams = new UrlParams();
      urlParams.addAll(URLEncodedUtils.parse(query, charset));
      return urlParams;
    }
  }

  boolean contain(String name) {
    return stream().anyMatch(p -> p.getName().equals(name));
  }
}
