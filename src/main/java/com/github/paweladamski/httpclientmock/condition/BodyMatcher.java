package com.github.paweladamski.httpclientmock.condition;

import com.github.paweladamski.httpclientmock.Debugger;
import com.github.paweladamski.httpclientmock.Request;
import java.io.IOException;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpEntityContainer;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.hamcrest.Matcher;

public class BodyMatcher implements Condition {

  private final Matcher<String> matcher;

  public BodyMatcher(Matcher<String> matcher) {
    this.matcher = matcher;
  }

  @Override
  public boolean matches(Request request) {
    HttpRequest httpRequest = request.getHttpRequest();
    if (!(httpRequest instanceof HttpEntityContainer)) {
      return false;
    }

    HttpEntity entity = ((HttpEntityContainer) httpRequest).getEntity();
    if (entity == null) {
      return false;
    }

    String message;
    try {
      message = EntityUtils.toString(entity);
    } catch (IOException | ParseException e) {
      return false;
    }
    return matcher.matches(message);
  }

  @Override
  public void debug(Request request, Debugger debugger) {
    debugger.message(matches(request), "body matches");
  }
}
