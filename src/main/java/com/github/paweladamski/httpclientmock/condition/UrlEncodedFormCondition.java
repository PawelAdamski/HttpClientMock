package com.github.paweladamski.httpclientmock.condition;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    if (!requestHasBody(r)) {
      //body-less requests only match if no parameters are expected
      return expected.isEmpty();
    }

    List<NameValuePair> actual = parseFormParameters(r);
    return allDefinedParamsOccurredInRequest(actual) && allParamsHaveMatchingValue(actual);
  }
  
  private boolean requestHasBody(Request r) {
    return (r.getHttpRequest() instanceof HttpEntityEnclosingRequest);
  }
  
  private boolean allDefinedParamsOccurredInRequest(List<NameValuePair> actual) {
    Set<String> actualNames = actual.stream()
      .map(p -> p.getName())
    .collect(Collectors.toSet());
    
    Set<String> expectedNames = expected.keySet();
    
    return expectedNames.equals(actualNames);
  }
  
  private boolean allParamsHaveMatchingValue(List<NameValuePair> actual) {
    for (NameValuePair actualPair : actual) {
      String actualName = actualPair.getName();
      String actualValue = actualPair.getValue();
      
      if (!expected.get(actualName).matches(actualValue)) {
        return false;
      }
    }
    
    return true;
  }
  
  private List<NameValuePair> parseFormParameters(Request r) {
    HttpEntityEnclosingRequest request = (HttpEntityEnclosingRequest) r.getHttpRequest();
    try {
      return URLEncodedUtils.parse(request.getEntity());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
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
