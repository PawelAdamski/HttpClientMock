package com.github.paweladamski.httpclientmock;

import static com.github.paweladamski.httpclientmock.matchers.HttpResponseMatchers.hasStatus;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.junit.jupiter.api.Test;

public class HttpClientMockTest {

  @Test
  public void should_run_requestInterceptors() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock();
    httpClientMock.addRequestInterceptor((request, context, entity) -> request.addHeader("foo", "bar"));
    httpClientMock.onGet().withHeader("foo", "bar").doReturn("ok");

    HttpResponse ok = httpClientMock.execute(new HttpGet("http://localhost"));
    assertThat(ok, hasStatus(200));
  }

  @Test
  public void should_run_responseInterceptors() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock();
    httpClientMock.addResponseInterceptor((request, context, entity) -> request.addHeader("foo", "bar"));
    httpClientMock.onGet().doReturn("ok");

    HttpResponse ok = httpClientMock.execute(new HttpGet("http://localhost"));
    assertThat(ok.getFirstHeader("foo").getValue(), equalTo("bar"));
  }

  @Test
  public void should_work_when_empty_content_type() throws IOException
  {
    HttpClientMock httpClientMock = new HttpClientMock();
    httpClientMock.onPost("http://localhost/Orders(Id=1)/Cancel").doReturn(200, "");

    HttpPost postReq = new HttpPost("http://localhost/Orders(Id=1)/Cancel");
    postReq.setEntity(new ByteArrayEntity(new byte[0], null));
    HttpResponse ok = httpClientMock.execute(postReq);

    assertThat(ok, hasStatus(200));
    httpClientMock.verify().post("http://localhost/Orders(Id=1)/Cancel").called();
  }
}
