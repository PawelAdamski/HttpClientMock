package com.github.paweladamski.httpclientmock.condition;

import com.github.paweladamski.httpclientmock.Debugger;
import com.github.paweladamski.httpclientmock.Request;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.util.EntityUtils;
import org.hamcrest.Matcher;

public class BodyMatcher implements Condition {

  private final Matcher<String> matcher;

  public BodyMatcher(Matcher<String> matcher) {
    this.matcher = matcher;
  }

  @Override
  public boolean matches(Request request) {
    HttpRequest httpRequest = request.getHttpRequest();
    if (!(httpRequest instanceof HttpEntityEnclosingRequest)) {
      return false;
    }
    
    HttpEntity entity = ((HttpEntityEnclosingRequest) httpRequest).getEntity();
    if (entity == null) {
      return false;
    }
    
    String message;
    try {
      message = EntityUtils.toString(entity);
    } catch (IOException e) {
      return false;
    }
    return matcher.matches(message);
  }

  @Override
  public void debug(Request request, Debugger debugger) {
    debugger.message(matches(request), "body matches");
  }
}
