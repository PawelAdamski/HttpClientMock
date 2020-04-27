package com.github.paweladamski.httpclientmock.condition;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.hamcrest.Matcher;

import com.github.paweladamski.httpclientmock.Request;

/**
 * Tests the request body for URL-encoded parameters.
 * @author Michael Angstadt
 */
public class UrlEncodedFormCondition implements Condition {
  private final Map<String, Matcher<String>> expected = new HashMap<>();
  private boolean enabled = false;

  @Override
  public boolean matches(Request r) {
    if (!enabled) {
      return true;
    }

    boolean requestHasBody = (r.getHttpRequest() instanceof HttpEntityEnclosingRequest);
    if (!requestHasBody) {
      //body-less requests only match if no parameters are expected
      return expected.isEmpty();
    }

    HttpEntityEnclosingRequest request = (HttpEntityEnclosingRequest) r.getHttpRequest();

    List<NameValuePair> actual;
    try {
      actual = URLEncodedUtils.parse(request.getEntity());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    if (expected.size() != actual.size()) {
      return false;
    }

    Map<String, Matcher<String>> expectedCopy = new HashMap<>(expected);
    for (NameValuePair actualPair : actual) {
      String actualName = actualPair.getName();

      Matcher<String> expectedValue = expectedCopy.remove(actualName);
      if (expectedValue == null) {
        return false;
      }

      String actualValue = actualPair.getValue();
      if (!expectedValue.matches(actualValue)) {
        return false;
      }
    }

    return true;
  }

  /**
   * Adds an expected form parameter.
   * @param name the parameter name
   * @param matcher the expected value
   */
  public void addExpectedParameter(String name, Matcher<String> matcher) {
    expected.put(name, matcher);
    enabled = true;
  }

  /**
   * Adds expected form parameters.
   * @param parameters the expected parameters
   */
  public void addExpectedParameters(Map<String, Matcher<String>> parameters) {
    expected.putAll(parameters);
    enabled = true;
  }
}
