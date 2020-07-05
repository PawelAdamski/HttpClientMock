package com.github.paweladamski.httpclientmock.matchers;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.http.NameValuePair;
import org.hamcrest.Matcher;

public class ParametersMatcher {

  private HashMap<String, MatchersList<String>> matchers = new HashMap<>();

  public void put(String name, Matcher<String> value) {
    matchers.computeIfAbsent(name, n -> new MatchersList<>()).add(value);
  }

  public void putAll(String name, MatchersList<String> value) {
    matchers.computeIfAbsent(name, n -> new MatchersList<>()).addAll(value);
  }

  public MatchersList<String> get(String name) {
    return matchers.get(name);
  }

  public boolean containsParameter(String name) {
    return matchers.containsKey(name);
  }

  public void putAll(ParametersMatcher parametersMatcher) {
    for (String paramName : parametersMatcher.matchers.keySet()) {
      putAll(paramName, parametersMatcher.matchers.get(paramName));
    }
  }

  public boolean matchesAndAllowExtraParameters(List<NameValuePair> actual) {
    return findMissingParameters(actual).isEmpty()
        && allParametersHaveMatchingValue(actual);
  }

  public boolean matches(List<NameValuePair> actual) {
    return findRedundantParams(actual).isEmpty()
        && findMissingParameters(actual).isEmpty()
        && allParametersHaveMatchingValue(actual);
  }

  public boolean matches(String name, String value) {
    return matchers.containsKey(name) && matchers.get(name).allMatches(value);
  }

  private boolean allParametersHaveMatchingValue(List<NameValuePair> actual) {
    return actual.stream().allMatch(param -> matchers.getOrDefault(param.getName(), new MatchersList<>()).allMatches(param.getValue()));
  }

  public Set<String> findRedundantParams(List<NameValuePair> actualParameters) {
    return actualParameters.stream()
        .map(NameValuePair::getName)
        .filter(n -> !matchers.containsKey(n))
        .collect(Collectors.toSet());
  }

  public Set<String> findMissingParameters(List<NameValuePair> actualParameters) {
    List<String> actualParametersName = actualParameters.stream().map(NameValuePair::getName).collect(Collectors.toList());
    return matchers.keySet().stream()
        .filter(((Predicate<String>) actualParametersName::contains).negate())
        .collect(Collectors.toSet());
  }

}
