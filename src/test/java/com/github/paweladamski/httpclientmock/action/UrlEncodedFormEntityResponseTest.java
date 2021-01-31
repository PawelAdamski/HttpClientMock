package com.github.paweladamski.httpclientmock.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;
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
    ClassicHttpResponse response = action.getResponse(null);

    String entityContent = EntityUtils.toString(response.getEntity());
    List<NameValuePair> actualPairs = URLEncodedUtils.parse(entityContent, StandardCharsets.UTF_8);
    assertEquals(expectedPairs, actualPairs);

    assertEquals(expectedStatus, response.getCode());
  }
}
