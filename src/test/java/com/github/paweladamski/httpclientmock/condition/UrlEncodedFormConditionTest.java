package com.github.paweladamski.httpclientmock.condition;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.paweladamski.httpclientmock.Debugger;
import com.github.paweladamski.httpclientmock.Request;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.Test;

/**
 * @author Michael Angstadt
 */
public class UrlEncodedFormConditionTest {

  @Test
  public void valid_match() throws Exception {
    UrlEncodedFormCondition condition = new UrlEncodedFormCondition();
    condition.addExpectedParameter("one", equalTo("1"));
    condition.addExpectedParameter("two", equalTo("2"));

    HttpPost request = new HttpPost();
    request.setEntity(new UrlEncodedFormEntity(Arrays.asList(
        new BasicNameValuePair("one", "1"),
        new BasicNameValuePair("two", "2")
    )));

    Request r = new Request(null, request, null);
    assertTrue(condition.matches(r));

    TestDebugger debugger = new TestDebugger();
    condition.debug(r, debugger);
    assertThat(debugger.matching, contains(
        "form parameter one is \"1\"",
        "form parameter two is \"2\""
    ));
    assertThat(debugger.notMatching, empty());
  }

  @Test
  public void case_sensitive_names() throws Exception {
    UrlEncodedFormCondition condition = new UrlEncodedFormCondition();
    condition.addExpectedParameter("ONE", equalTo("1"));

    HttpPost request = new HttpPost();
    request.setEntity(new UrlEncodedFormEntity(Arrays.asList(
        new BasicNameValuePair("one", "1")
    )));

    Request r = new Request(null, request, null);
    assertFalse(condition.matches(r));

    TestDebugger debugger = new TestDebugger();
    condition.debug(r, debugger);
    assertThat(debugger.matching, empty());
    assertThat(debugger.notMatching, contains(
        "form parameter ONE is missing from the request",
        "form parameter one was not expected to be in the request"
    ));
  }

  @Test
  public void no_match() throws Exception {
    UrlEncodedFormCondition condition = new UrlEncodedFormCondition();
    condition.addExpectedParameter("one", equalTo("1"));
    condition.addExpectedParameter("two", equalTo("2"));

    HttpPost request = new HttpPost();
    request.setEntity(new UrlEncodedFormEntity(Arrays.asList(
        new BasicNameValuePair("one", "1"),
        new BasicNameValuePair("two", "not 2")
    )));

    Request r = new Request(null, request, null);
    assertFalse(condition.matches(r));

    TestDebugger debugger = new TestDebugger();
    condition.debug(r, debugger);
    assertThat(debugger.matching, contains(
        "form parameter one is \"1\""
    ));
    assertThat(debugger.notMatching, contains(
        "form parameter two is \"2\""
    ));
  }

  /**
   * Parameters with the same name are not supported because there's no way of telling which Matcher to assign to which parameter.
   */
  @Test
  public void duplicate_names() throws Exception {
    UrlEncodedFormCondition condition = new UrlEncodedFormCondition();
    condition.addExpectedParameter("one", equalTo("1"));
    condition.addExpectedParameter("one", equalTo(
        "3")); //MatchersMap requires that the parameter value match BOTH conditions--so the value must equal "1" and must also equal "3", which is impossible

    HttpPost request = new HttpPost();
    request.setEntity(new UrlEncodedFormEntity(Arrays.asList(
        new BasicNameValuePair("one", "1"),
        new BasicNameValuePair("one", "3")
    )));

    Request r = new Request(null, request, null);
    assertFalse(condition.matches(r));

    TestDebugger debugger = new TestDebugger();
    condition.debug(r, debugger);
    assertThat(debugger.matching, empty());
    assertThat(debugger.notMatching, contains(
        "form parameter one is \"1\" and \"3\"",
        "form parameter one is \"1\" and \"3\""
    ));
  }

  @Test
  public void different_number_of_parameters() throws Exception {
    UrlEncodedFormCondition condition = new UrlEncodedFormCondition();
    condition.addExpectedParameter("one", equalTo("1"));

    HttpPost request = new HttpPost();
    request.setEntity(new UrlEncodedFormEntity(Arrays.asList(
        new BasicNameValuePair("one", "1"),
        new BasicNameValuePair("two", "2")
    )));

    Request r = new Request(null, request, null);
    assertFalse(condition.matches(r));

    TestDebugger debugger = new TestDebugger();
    condition.debug(r, debugger);
    assertThat(debugger.matching, contains(
        "form parameter one is \"1\""
    ));
    assertThat(debugger.notMatching, contains(
        "form parameter two was not expected to be in the request"
    ));
  }

  /**
   * The method that is used to extract the form parameters out of the request body ({@link URLEncodedUtils#parse(HttpEntity)}) also takes the Content-Type of
   * the request into consideration. If the Content-Type is not "application/x-www-form-urlencoded", then it will not attempt to parse the body and it will act
   * as if the body has zero form parameters in it.
   */
  @Test
  public void body_contains_form_parameters_but_content_type_is_different() throws Exception {
    {
      UrlEncodedFormCondition condition = new UrlEncodedFormCondition();

      HttpPost request = new HttpPost();
      request.setEntity(new StringEntity("one=1"));

      Request r = new Request(null, request, null);
      assertTrue(condition.matches(r));

      TestDebugger debugger = new TestDebugger();
      condition.debug(r, debugger);
      assertThat(debugger.matching, empty());
      assertThat(debugger.notMatching, empty());
    }

    {
      UrlEncodedFormCondition condition = new UrlEncodedFormCondition();
      condition.addExpectedParameter("one", equalTo("1"));

      HttpPost request = new HttpPost();
      request.setEntity(new StringEntity("one=1"));

      Request r = new Request(null, request, null);
      assertFalse(condition.matches(r)); //request does not use "application/x-www-form-urlencoded" content type

      TestDebugger debugger = new TestDebugger();
      condition.debug(r, debugger);
      assertThat(debugger.matching, empty());
      assertThat(debugger.notMatching, contains(
          "form parameter one is missing from the request"
      ));
    }
  }

  @Test
  public void bodyless_request() {
    //without expected params
    {
      UrlEncodedFormCondition condition = new UrlEncodedFormCondition();
      HttpGet request = new HttpGet();
      Request r = new Request(null, request, null);
      assertTrue(condition.matches(r));

      TestDebugger debugger = new TestDebugger();
      condition.debug(r, debugger);
      assertThat(debugger.matching, empty());
      assertThat(debugger.notMatching, empty());
    }

    //with expected params
    {
      UrlEncodedFormCondition condition = new UrlEncodedFormCondition();
      condition.addExpectedParameter("foo", equalTo("bar"));
      HttpGet request = new HttpGet();
      Request r = new Request(null, request, null);
      assertFalse(condition.matches(r));

      TestDebugger debugger = new TestDebugger();
      condition.debug(r, debugger);
      assertThat(debugger.matching, empty());
      assertThat(debugger.notMatching, contains(
          "form parameter foo is missing from the request"
      ));
    }
  }

  private static class TestDebugger extends Debugger {

    public final List<String> matching = new ArrayList<>();
    public final List<String> notMatching = new ArrayList<>();

    @Override
    public void message(boolean matches, String message) {
      List<String> list = matches ? matching : notMatching;
      list.add(message);
    }
  }
}
