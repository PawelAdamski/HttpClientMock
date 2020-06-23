package com.github.paweladamski.httpclientmock.condition;

import com.github.paweladamski.httpclientmock.Request;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public class FormParametersParser {

  List<NameValuePair> parse(Request request) {
    if (!requestHasBody(request)) {
      return Collections.emptyList();
    }

    HttpEntityEnclosingRequest httpRequest = (HttpEntityEnclosingRequest) request.getHttpRequest();
    HttpEntity entity = httpRequest.getEntity();
    if (entity == null) {
      return Collections.emptyList();
    }

    try {
      /*
       * The method below returns an empty list if the Content-Type of the
       * request is not "application/x-www-form-urlencoded". So, requests with
       * other kinds of data in the body will correctly be ignored here.
       */
      return URLEncodedUtils.parse(entity);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean requestHasBody(Request r) {
    return (r.getHttpRequest() instanceof HttpEntityEnclosingRequest);
  }
}
