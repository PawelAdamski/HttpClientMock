package com.github.paweladamski.httpclientmock;

import static org.hamcrest.Matchers.isEmptyOrNullString;

import com.github.paweladamski.httpclientmock.matchers.MatchersList;
import com.github.paweladamski.httpclientmock.matchers.MatchersMap;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.http.NameValuePair;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;

public class UrlConditions {

  private static final int EMPTY_PORT = -1;
  private MatchersMap<String, String> parameterConditions = new MatchersMap<>();
  private Matcher<String> referenceConditions = Matchers.isEmptyOrNullString();
  private MatchersList<String> hostConditions = new MatchersList<>();
  private MatchersList<String> pathConditions = new MatchersList<>();
  private MatchersList<Integer> portConditions = new MatchersList<>();
  private Matcher<String> schemaConditions = Matchers.any(String.class);

  public MatchersMap<String, String> getParameterConditions() {
    return parameterConditions;
  }

  public Matcher<String> getReferenceConditions() {
    return referenceConditions;
  }

  public void setReferenceConditions(Matcher<String> referenceConditions) {
    this.referenceConditions = referenceConditions;
  }

  public MatchersList<String> getHostConditions() {
    return hostConditions;
  }

  public void setHostConditions(MatchersList<String> hostConditions) {
    this.hostConditions = hostConditions;
  }

  public MatchersList<String> getPathConditions() {
    return pathConditions;
  }

  public MatchersList<Integer> getPortConditions() {
    return portConditions;
  }

  public void setSchemaConditions(Matcher<String> schemaConditions) {
    this.schemaConditions = schemaConditions;
  }

  boolean matches(String urlText) {
    try {
      URL url = new URL(urlText);

      return hostConditions.allMatches(url.getHost())
          && pathConditions.allMatches(url.getPath())
          && portConditions.allMatches(url.getPort())
          && referenceConditions.matches(url.getRef())
          && schemaConditions.matches(url.getProtocol())
          && allDefinedParamsOccurredInURL(url.getQuery())
          && allParamsHaveMatchingValue(url.getQuery());

    } catch (MalformedURLException e) {
      return false;
    }
  }

  private boolean allDefinedParamsOccurredInURL(String query) {
    return findMissingParameters(query).isEmpty();
  }

  private boolean allParamsHaveMatchingValue(String query) {
    UrlParams params = UrlParams.parse(query);
    return params.stream()
        .allMatch(param -> parameterConditions.matches(param.getName(), param.getValue()));
  }

  private Set<String> findMissingParameters(String query) {
    UrlParams params = UrlParams.parse(query);
    return parameterConditions.keySet().stream()
        .filter(((Predicate<String>) params::contain).negate())
        .collect(Collectors.toSet());
  }

  public void join(UrlConditions a) {
    this.referenceConditions = a.referenceConditions;
    this.schemaConditions = a.schemaConditions;
    this.portConditions.addAll(a.portConditions);
    this.pathConditions.addAll(a.pathConditions);
    this.hostConditions.addAll(a.hostConditions);
    for (String paramName : a.parameterConditions.keySet()) {
      for (Matcher<String> paramValue : a.parameterConditions.get(paramName)) {
        this.parameterConditions.put(paramName, paramValue);
      }
    }
  }

  void debug(Request request, Debugger debugger) {
    try {
      URL url = new URL(request.getUri());
      debugger.message(hostConditions.allMatches(url.getHost()), "schema is " + describe(schemaConditions));
      debugger.message(hostConditions.allMatches(url.getHost()), "host is " + hostConditions.describe());
      debugger.message(pathConditions.allMatches(url.getPath()), "path is " + pathConditions.describe());
      debugger.message(portConditions.allMatches(url.getPort()), "port is " + portDebugDescription());
      if (referenceConditions != isEmptyOrNullString() || !referenceConditions.matches(url.getRef())) {
        debugger.message(referenceConditions.matches(url.getRef()), "reference is " + describe(referenceConditions));
      }
      Set<String> missingParams = findMissingParameters(url.getQuery());
      for (String param : missingParams) {
        debugger.message(false, "parameter " + param + " occurs in request");
      }
      UrlParams params = UrlParams.parse(url.getQuery());
      for (NameValuePair param : params) {
        if (parameterConditions.containsKey(param.getName())) {
          boolean matches = parameterConditions.matches(param.getName(), param.getValue());
          String message = "parameter " + param.getName() + " is " + parameterConditions.describe(param.getName());
          debugger.message(matches, message);
        } else {
          String message = "parameter " + param.getName() + " is redundant";
          debugger.message(false, message);
        }
      }

    } catch (MalformedURLException e) {
      System.out.println("Can't parse URL: " + request.getUri());
    }

  }

  private String describe(Matcher<String> matcher) {
    return StringDescription.toString(matcher);
  }

  private String portDebugDescription() {
    if (portConditions.allMatches(EMPTY_PORT)) {
      return "empty";
    } else {
      return portConditions.describe();
    }
  }
}
