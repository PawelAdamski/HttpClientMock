package com.github.paweladamski.httpclientmock.condition;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.hamcrest.Matcher;

import com.github.paweladamski.httpclientmock.Debugger;
import com.github.paweladamski.httpclientmock.Request;
import com.github.paweladamski.httpclientmock.matchers.MatchersMap;

/**
 * Tests the request body for URL-encoded parameters.
 * @author Michael Angstadt
 */
public class UrlEncodedFormCondition implements Condition {
  private final MatchersMap<String, String> expected = new MatchersMap<>();

  @Override
  public boolean matches(Request r) {
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
    return actual.stream().allMatch(actualPair -> expected.matches(actualPair.getName(), actualPair.getValue()));
  }
  
  private List<NameValuePair> parseFormParameters(Request r) {
    if (!requestHasBody(r)) {
      return Collections.emptyList();
    }
    
    HttpEntityEnclosingRequest request = (HttpEntityEnclosingRequest) r.getHttpRequest();
    HttpEntity entity = request.getEntity();
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

  /**
   * Adds an expected form parameter.
   * @param name the parameter name
   * @param matcher the expected value
   */
  public void addExpectedParameter(String name, Matcher<String> matcher) {
    expected.put(name, matcher);
  }

  /**
   * Adds expected form parameters.
   * @param parameters the expected parameters
   */
  public void addExpectedParameters(MatchersMap<String, String> parameters) {
    expected.putAll(parameters);
  }
  
  @Override
  public void debug(Request r, Debugger debugger) {
    List<NameValuePair> actual = parseFormParameters(r);

    for (String param : findExtraParamsInRequest(actual)) {
      debugger.message(false, "parameter " + param + " was not expected to be in the request");
    }
    
    for (String param : findParamsMissingFromRequest(actual)) {
      debugger.message(false, "parameter " + param + " is missing from the request");
    }
    
    for (NameValuePair actualPair : actual) {
      String actualName = actualPair.getName();
      if (!expected.containsKey(actualName)) {
        /*
         * Parameter was not expected to be found in the request.
         * This is checked for in the code above, so no need to output a message here.
         */
        continue;
      }

      String actualValue = actualPair.getValue();
      boolean matches = expected.matches(actualName, actualValue);
      String message = "parameter " + actualName + " is " + expected.describe(actualName);
      debugger.message(matches, message);
    }
  }
  
  private Set<String> findExtraParamsInRequest(List<NameValuePair> actual) {
    return actual.stream()
      .map(p -> p.getName())
      .filter(name -> !expected.containsKey(name))
    .collect(Collectors.toSet());
  }
  
  private Set<String> findParamsMissingFromRequest(List<NameValuePair> actual) {
    return expected.keySet().stream()
      .filter(name -> actual.stream().noneMatch(p -> p.getName().equals(name)))
    .collect(Collectors.toSet());
  }
}
