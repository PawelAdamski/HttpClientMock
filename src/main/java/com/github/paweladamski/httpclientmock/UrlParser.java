package com.github.paweladamski.httpclientmock;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.http.NameValuePair;
import org.hamcrest.Matchers;

public class UrlParser {

  public static final int EMPTY_PORT_NUMBER = -1;

  public UrlConditions parse(String urlText) {
    try {
      UrlConditions conditions = new UrlConditions();
      URL url = new URL(urlText);

      String ref = url.getRef();
      conditions.setReferenceConditions((ref == null) ? isEmptyOrNullString() : equalTo(ref));

      conditions.setSchemaConditions(Matchers.equalTo(url.getProtocol()));
      conditions.getHostConditions().add(equalTo(url.getHost()));
      conditions.getPortConditions().add(equalTo(url.getPort()));
      conditions.getPathConditions().add(equalTo(url.getPath()));
      List<NameValuePair> params = new UrlParamsParser().parse(url.getQuery(), StandardCharsets.UTF_8);
      for (NameValuePair param : params) {
        conditions.getUrlQueryConditions().put(param.getName(), equalTo(param.getValue()));
      }
      return conditions;
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(e);
    }

  }

}
