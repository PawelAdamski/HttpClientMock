package com.github.paweladamski.httpclientmock.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.Test;

/**
 * @author Michael Angstadt
 */
public class UrlEncodedFormEntityResponseTest {

  @Test
  public void test() throws Exception {
    List<NameValuePair> expectedPairs = Arrays.asList(new BasicNameValuePair("one", "1"), new BasicNameValuePair("two", "2"));
    int expectedStatus = 500;

    UrlEncodedFormEntityResponse action = new UrlEncodedFormEntityResponse(expectedStatus, expectedPairs, StandardCharsets.UTF_8);
    HttpResponse response = action.getResponse(null);

    List<NameValuePair> actualPairs = URLEncodedUtils.parse(response.getEntity());
    assertEquals(expectedPairs, actualPairs);

    assertEquals(expectedStatus, response.getStatusLine().getStatusCode());
  }
}
