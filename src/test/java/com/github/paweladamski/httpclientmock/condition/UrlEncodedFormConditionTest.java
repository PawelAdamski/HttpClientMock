package com.github.paweladamski.httpclientmock.condition;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

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
  }
  
  /**
   * Parameters with the same name are not supported because there's no way of
   * telling which Matcher to assign to which parameter.
   */
  @Test
  public void duplicate_names() throws Exception {
    UrlEncodedFormCondition condition = new UrlEncodedFormCondition();
    condition.addExpectedParameter("one", equalTo("1"));
    condition.addExpectedParameter("one", equalTo("3")); //overwrites the above parameter
    
    HttpPost request = new HttpPost();
    request.setEntity(new UrlEncodedFormEntity(Arrays.asList(
      new BasicNameValuePair("one", "1"),
      new BasicNameValuePair("one", "3")
    )));
    
    Request r = new Request(null, request, null);
    assertFalse(condition.matches(r));
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
  }

  @Test
  public void bodyless_request() {
    //without expected params
    {
      UrlEncodedFormCondition condition = new UrlEncodedFormCondition();
      HttpGet request = new HttpGet();
      Request r = new Request(null, request, null);
      assertTrue(condition.matches(r));
    }

    //with expected params
    {
      UrlEncodedFormCondition condition = new UrlEncodedFormCondition();
      condition.addExpectedParameter("foo", equalTo("bar"));
      HttpGet request = new HttpGet();
      Request r = new Request(null, request, null);
      assertFalse(condition.matches(r));
    }
  }
}
