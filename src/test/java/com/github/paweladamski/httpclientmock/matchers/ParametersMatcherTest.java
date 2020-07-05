package com.github.paweladamski.httpclientmock.matchers;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class ParametersMatcherTest {

  ParametersMatcher matcher;
  ArrayList<NameValuePair> actualParameters;

  @Before
  public void setUp() {
    matcher = new ParametersMatcher();
    actualParameters = new ArrayList<>();
  }

  @Test
  public void match_shouldReturnTrue_When_NoParamsWereExpectedAndNoParamsInActualList() {
    assertTrue(matcher.matches(Collections.EMPTY_LIST));
  }

  @Test
  public void matchesAndAllowExtraParameters_shouldReturnTrue_When_NoParamsWereExpectedAndNoParamsInActualList() {
    assertTrue(matcher.matchesAndAllowExtraParameters(Collections.EMPTY_LIST));
  }

  @Test
  public void match_shouldReturnTrue_When_OneParamIsExpectedAndTheSameParamsInActualList() {
    matcher.put("foo", equalTo("bar"));
    actualParameters.add(new BasicNameValuePair("foo", "bar"));

    assertTrue(matcher.matches(actualParameters));
  }

  @Test
  public void matchesAndAllowExtraParameters_shouldReturnTrue_When_OneParamIsExpectedAndTheSameParamsInActualList() {
    matcher.put("foo", equalTo("bar"));
    actualParameters.add(new BasicNameValuePair("foo", "bar"));

    assertTrue(matcher.matches(actualParameters));
  }

  @Test
  public void match_shouldReturnFalse_When_ActualParametersHaveOneExtraParameter() {
    matcher.put("foo", equalTo("bar"));
    actualParameters.add(new BasicNameValuePair("foo", "bar"));
    actualParameters.add(new BasicNameValuePair("abc", "123"));

    assertFalse(matcher.matches(actualParameters));
  }

  @Test
  public void matchesAndAllowExtraParameters_shouldReturnTrue_When_ActualParametersHaveOneExtraParameter() {
    matcher.put("foo", equalTo("bar"));
    actualParameters.add(new BasicNameValuePair("foo", "bar"));
    actualParameters.add(new BasicNameValuePair("abc", "123"));

    assertTrue(matcher.matchesAndAllowExtraParameters(actualParameters));
  }

  @Test
  public void match_shouldReturnFalse_When_ActualContainNotMatchingParameter() {
    matcher.put("foo", equalTo("bar"));
    actualParameters.add(new BasicNameValuePair("foo", "123"));

    assertFalse(matcher.matches(actualParameters));
  }

  @Test
  public void matchesAndAllowExtraParameters_shouldReturnFalse_When_ActualContainNotMatchingParameter() {
    matcher.put("foo", equalTo("bar"));
    actualParameters.add(new BasicNameValuePair("foo", "123"));

    assertFalse(matcher.matchesAndAllowExtraParameters(actualParameters));
  }

  @Test
  public void match_shouldReturnFalse_When_ActualMissesOneOfTheExpectedParameter() {
    matcher.put("foo", equalTo("bar"));
    matcher.put("abc", equalTo("123"));
    actualParameters.add(new BasicNameValuePair("foo", "bar"));

    assertFalse(matcher.matches(actualParameters));
  }

  @Test
  public void matchesAndAllowExtraParameters_shouldReturnFalse_When_ActualMissesOneOfTheExpectedParameter() {
    matcher.put("foo", equalTo("bar"));
    matcher.put("abc", equalTo("123"));
    actualParameters.add(new BasicNameValuePair("foo", "bar"));

    assertFalse(matcher.matchesAndAllowExtraParameters(actualParameters));
  }

  @Test
  public void put_should_addNewMatchers_when_addingMatcherForExistingParameter() {
    matcher.put("foo", Matchers.containsString("a"));
    matcher.put("foo", Matchers.containsString("b"));

    actualParameters.add(new BasicNameValuePair("foo", "a"));
    assertFalse(matcher.matches(actualParameters));

    actualParameters.clear();
    actualParameters.add(new BasicNameValuePair("foo", "b"));
    assertFalse(matcher.matches(actualParameters));

    actualParameters.clear();
    actualParameters.add(new BasicNameValuePair("foo", "bar"));
    assertTrue(matcher.matches(actualParameters));
  }

}
