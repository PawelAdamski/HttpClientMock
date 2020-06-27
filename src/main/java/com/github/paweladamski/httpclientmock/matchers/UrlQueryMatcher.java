package com.github.paweladamski.httpclientmock.matchers;

import com.github.paweladamski.httpclientmock.Debugger;
import com.github.paweladamski.httpclientmock.UrlParamsParser;
import java.util.List;
import java.util.Set;
import org.apache.http.NameValuePair;
import org.hamcrest.Matcher;

public class UrlQueryMatcher {

  private ParametersMatcher expected = new ParametersMatcher();
  private boolean allowExtraParameters;

  public boolean matches(String query) {
    List<NameValuePair> actualParameters = new UrlParamsParser().parse(query);
    if (allowExtraParameters) {
      return expected.matchesAndAllowRedundantParameters(actualParameters);
    } else {
      return expected.matches(actualParameters);
    }
  }

  public void put(String name, Matcher<String> matcher) {
    expected.put(name, matcher);
  }

  public void describe(String query, Debugger debugger) {
    List<NameValuePair> actualParameters = new UrlParamsParser().parse(query);
    Set<String> missingParams = expected.findMissingParameters(actualParameters);
    for (String param : missingParams) {
      debugger.message(false, "query parameter " + param + " is missing from the request");
    }

    for (NameValuePair param : actualParameters) {
      if (expected.containsParameter(param.getName())) {
        boolean matches = expected.matches(param.getName(), param.getValue());
        String message = "query parameter " + param.getName() + " is " + expected.get(param.getName()).describe();
        debugger.message(matches, message);
      } else if (!allowExtraParameters){
        String message = "query parameter " + param.getName() + " was not expected to be in the request";
        debugger.message(false, message);
      }
    }
  }

  public void setAllowExtraParameters(boolean allowExtraParameters) {
    this.allowExtraParameters = allowExtraParameters;
  }
}
