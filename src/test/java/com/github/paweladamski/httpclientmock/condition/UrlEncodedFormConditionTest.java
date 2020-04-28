package com.github.paweladamski.httpclientmock.condition;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

import com.github.paweladamski.httpclientmock.Debugger;
import com.github.paweladamski.httpclientmock.Request;

/**
 * @author Michael Angstadt
 */
public class UrlEncodedFormConditionTest {
  /**
   * Condition should always match if no expected parameters were passed into it.
   */
  @Test
  public void no_expected_parameters_provided() throws Exception {
    UrlEncodedFormCondition condition = new UrlEncodedFormCondition();
    
    HttpPost request = new HttpPost();
    request.setEntity(new UrlEncodedFormEntity(Arrays.asList(
      new BasicNameValuePair("one", "1"),
      new BasicNameValuePair("two", "2")
    )));
    
    Request r = new Request(null, request, null);
    assertTrue(condition.matches(r));

    TestDebugger debugger = new TestDebugger();
    condition.debug(r, debugger);
    assertThat(debugger.matching, empty());
    assertThat(debugger.notMatching, empty());
  }
  
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
      "parameter one is \"1\"",
      "parameter two is \"2\""
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
      "parameter one was not expected to be in the request",
      "parameter ONE is missing from the request"
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
      "parameter one is \"1\""
    ));
    assertThat(debugger.notMatching, contains(
      "parameter two is \"2\""
    ));
  }
  
  /**
   * Parameters with the same name are not supported because there's no way of
   * telling which Matcher to assign to which parameter.
   */
  @Test
  public void duplicate_names() throws Exception {
    UrlEncodedFormCondition condition = new UrlEncodedFormCondition();
    condition.addExpectedParameter("one", equalTo("1"));
    condition.addExpectedParameter("one", equalTo("3")); //TODO MatchersMap requires that the parameter value match BOTH conditions--so the value must equal "1" and must also equal "3", which is impossible 
    
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
      "parameter one is \"1\" and \"3\"",
      "parameter one is \"1\" and \"3\""
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
      "parameter one is \"1\""
    ));
    assertThat(debugger.notMatching, contains(
      "parameter two was not expected to be in the request"
    ));
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
        "parameter foo is missing from the request"
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
