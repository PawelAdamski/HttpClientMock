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
    URI url = URI.create(urlText);

    String ref = url.getFragment();
    conditions.setReferenceConditions((ref == null) ? isEmptyOrNullString() : equalTo(ref));

    conditions.setSchemaConditions(Matchers.equalTo(url.getScheme()));
    conditions.getHostConditions().add(equalTo(url.getHost()));
    conditions.getPortConditions().add(equalTo(url.getPort()));
    String path = url.getPath();
    if (path.isEmpty()) {
      conditions.getPathConditions().add(equalTo("/"));
    } else {
      conditions.getPathConditions().add(equalTo(url.getPath()));
    }
    List<NameValuePair> params = new UrlParamsParser().parse(url.getQuery(), StandardCharsets.UTF_8);
    for (NameValuePair param : params) {
      conditions.getUrlQueryConditions().put(param.getName(), equalTo(param.getValue()));
    }
    return conditions;

  }

}
