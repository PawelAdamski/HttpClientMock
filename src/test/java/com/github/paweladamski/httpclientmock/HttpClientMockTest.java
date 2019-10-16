package com.github.paweladamski.httpclientmock;

import static com.github.paweladamski.httpclientmock.matchers.HttpResponseMatchers.hasStatus;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.hamcrest.Matchers;
import org.junit.Test;

public class HttpClientMockTest {

  @Test
  public void should_run_requestInterceptors() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock();
    httpClientMock.addRequestInterceptor((request,context)->request.addHeader("foo","bar"));
    httpClientMock.onGet().withHeader("foo","bar").doReturn("ok");

    HttpResponse ok = httpClientMock.execute(new HttpGet("http://localhost"));
    assertThat(ok, hasStatus(200));
  }

  @Test
  public void should_run_responseInterceptors() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock();
    httpClientMock.addResponseInterceptor((request,context)->request.addHeader("foo","bar"));
    httpClientMock.onGet().doReturn("ok");

    HttpResponse ok = httpClientMock.execute(new HttpGet("http://localhost"));
    assertThat(ok.getFirstHeader("foo").getValue(), equalTo("bar2"));
  }


}
