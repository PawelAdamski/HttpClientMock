package com.github.paweladamski.httpclientmock;

import static com.github.paweladamski.httpclientmock.Requests.httpGet;
import static com.github.paweladamski.httpclientmock.matchers.HttpResponseMatchers.hasNoEntity;
import static com.github.paweladamski.httpclientmock.matchers.HttpResponseMatchers.hasStatus;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class HttpClientMockBuilder_doReturnWithStatusTest {

  @ParameterizedTest
  @ValueSource(ints = {200, 300, 400, 500})
  public void doReturn_should_set_response_status_empty_reason_and_null_entity_when_only_statusCode_is_provided(int statusCode) throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock();
    httpClientMock.onGet().doReturnWithStatus(statusCode);

    CloseableHttpResponse response = httpClientMock.execute(httpGet("http://localhost"));
    assertThat(response, hasStatus(statusCode));
    assertThat(response, hasNoEntity());
  }

  @ParameterizedTest
  @ValueSource(ints = {200, 300, 400, 500})
  public void doReturn_should_set_response_status_and_reason_phrase(int statusCode) throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock();
    httpClientMock.onGet().doReturnWithStatus(statusCode, "reason");

    HttpResponse response = httpClientMock.execute(httpGet("http://localhost"));
    assertThat(response, hasStatus(statusCode));
    assertThat(response.getStatusLine().getReasonPhrase(), equalTo("reason"));
  }

}