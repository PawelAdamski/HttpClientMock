package com.github.paweladamski.httpclientmock;

import static com.github.paweladamski.httpclientmock.Requests.httpGet;
import static com.github.paweladamski.httpclientmock.matchers.HttpResponseMatchers.hasContent;
import static com.github.paweladamski.httpclientmock.matchers.HttpResponseMatchers.hasStatus;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class HttpClientMockBuilder_doReturnStatusTest {

  @ParameterizedTest
  @ValueSource(ints = {200, 300, 400, 500})
  public void doReturnStatus_should_set_response_status_and_empty_entity(int statusCode) throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock();
    httpClientMock.onGet().doReturnStatus(statusCode);

    CloseableHttpResponse response = httpClientMock.execute(httpGet("http://localhost"));
    assertThat(response, hasStatus(statusCode));
    assertThat(response, hasContent(""));
  }

}