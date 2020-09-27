package com.github.paweladamski.httpclientmock;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.hc.core5.http.NameValuePair;
import org.hamcrest.Matchers;

public class UrlParser {

  public static final int EMPTY_PORT_NUMBER = -1;

  public UrlConditions parse(String urlText) {

    UrlConditions conditions = new UrlConditions();
    URI uri = URI.create(urlText);

    conditions.setSchemaConditions(Matchers.equalTo(uri.getScheme()));
    conditions.getHostConditions().add(equalTo(uri.getHost()));
    conditions.getPortConditions().add(equalTo(uri.getPort()));
    String path = uri.getPath();
    if (path.isEmpty()) {
      conditions.getPathConditions().add(equalTo("/"));
    } else {
      conditions.getPathConditions().add(equalTo(uri.getPath()));
    }
    List<NameValuePair> params = new UrlParamsParser().parse(uri.getQuery(), StandardCharsets.UTF_8);
    for (NameValuePair param : params) {
      conditions.getUrlQueryConditions().put(param.getName(), equalTo(param.getValue()));
    }
    return conditions;

  }

}
