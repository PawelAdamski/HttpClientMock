package com.github.paweladamski.httpclientmock;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public class UrlParamsParser extends ArrayList<NameValuePair> {

  public List<NameValuePair> parse(String query) {
    return parse(query, StandardCharsets.UTF_8);
  }

  public List<NameValuePair> parse(String query, Charset charset) {
    List<NameValuePair> params = new UrlParamsParser();

    if (query != null) {
      params.addAll(URLEncodedUtils.parse(query, charset));
    }

    return params;
  }

  public boolean contain(String name) {
    return stream().anyMatch(p -> p.getName().equals(name));
  }

  public Optional<String> get(String name) {
    return stream()
        .filter(pair -> pair.getName().equals(name))
        .map(NameValuePair::getValue)
        .findFirst();
  }
}
