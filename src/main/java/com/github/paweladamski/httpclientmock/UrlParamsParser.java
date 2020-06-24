package com.github.paweladamski.httpclientmock;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public class UrlParamsParser {

  public List<NameValuePair> parse(String query) {
    return parse(query, StandardCharsets.UTF_8);
  }

  public List<NameValuePair> parse(String query, Charset charset) {
    if (query != null) {
      return URLEncodedUtils.parse(query, charset);
    } else {
      return Collections.emptyList();
    }
  }

}
