package com.github.paweladamski.httpclientmock.condition;

import com.github.paweladamski.httpclientmock.Debugger;
import com.github.paweladamski.httpclientmock.Request;
import com.github.paweladamski.httpclientmock.matchers.ParametersMatcher;
import java.util.List;
import java.util.Set;
import org.apache.http.NameValuePair;
import org.hamcrest.Matcher;

/**
 * Tests the request body for URL-encoded parameters.
 *
 * @author Michael Angstadt
 */
public class UrlEncodedFormCondition implements Condition {

  private final ParametersMatcher expectedParameters = new ParametersMatcher();

  @Override
  public boolean matches(Request r) {
    List<NameValuePair> actualParameters = new UrlEncodedFormParser().parse(r);
    return expectedParameters.matches(actualParameters);
  }

  /**
   * Adds an expected form parameter.
   *
   * @param name the parameter name
   * @param matcher the expected value
   */
  public void addExpectedParameter(String name, Matcher<String> matcher) {
    expectedParameters.put(name, matcher);
  }

  /**
   * Adds expected form parameters.
   *
   * @param parameters the expected parameters
   */
  public void addExpectedParameters(ParametersMatcher parameters) {
    expectedParameters.putAll(parameters);
  }

  @Override
  public void debug(Request r, Debugger debugger) {
    List<NameValuePair> actual = new UrlEncodedFormParser().parse(r);

    Set<String> missingParams = expectedParameters.findMissingParameters(actual);
    for (String param : missingParams) {
      debugger.message(false, "form parameter " + param + " is missing from the request");
    }

    for (NameValuePair param : actual) {
      if (expectedParameters.containsParameter(param.getName())) {
        boolean matches = expectedParameters.matches(param.getName(), param.getValue());
        String message = "form parameter " + param.getName() + " is " + expectedParameters.get(param.getName()).describe();
        debugger.message(matches, message);
      } else {
        String message = "form parameter " + param.getName() + " was not expected to be in the request";
        debugger.message(false, message);
      }
    }
  }

}
