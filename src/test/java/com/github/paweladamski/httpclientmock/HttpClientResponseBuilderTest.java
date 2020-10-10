package com.github.paweladamski.httpclientmock;

import static com.github.paweladamski.httpclientmock.Asserts.assertThrows;
import static com.github.paweladamski.httpclientmock.Requests.httpGet;
import static com.github.paweladamski.httpclientmock.Requests.httpPost;
import static com.github.paweladamski.httpclientmock.matchers.HttpResponseMatchers.hasContent;
import static com.github.paweladamski.httpclientmock.matchers.HttpResponseMatchers.hasCookie;
import static com.github.paweladamski.httpclientmock.matchers.HttpResponseMatchers.hasStatus;
import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static org.apache.hc.core5.http.ContentType.APPLICATION_XML;
import static org.apache.hc.core5.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.hc.core5.http.HttpStatus.SC_NO_CONTENT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNull;

import com.github.paweladamski.httpclientmock.action.Action;
import com.github.paweladamski.httpclientmock.condition.UrlEncodedFormParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpEntityContainer;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.junit.Assert;
import org.junit.Test;

public class HttpClientResponseBuilderTest {

  @Test
  public void should_return_status_404_when_no_rule_matches() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock();
    ClassicHttpResponse notFound = httpClientMock.execute(new HttpGet("http://localhost/foo"));
    assertThat(notFound, hasStatus(404));
  }

  @Test
  public void should_use_next_action_after_every_call() throws Exception {
    HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

    httpClientMock.onGet("/foo")
        .doReturn("first")
        .doReturn("second")
        .doReturn("third");

    httpClientMock.onGet("/bar")
        .doReturn("bar")
        .doReturnStatus(300)
        .doThrowException(new IOException());

    ClassicHttpResponse response1 = httpClientMock.execute(new HttpGet("http://localhost/foo"));
    ClassicHttpResponse response2 = httpClientMock.execute(new HttpGet("http://localhost/foo"));
    ClassicHttpResponse response3 = httpClientMock.execute(new HttpGet("http://localhost/foo"));
    ClassicHttpResponse response4 = httpClientMock.execute(new HttpGet("http://localhost/foo"));
    ClassicHttpResponse response5 = httpClientMock.execute(new HttpGet("http://localhost/foo"));

    assertThat(response1, hasContent("first"));
    assertThat(response2, hasContent("second"));
    assertThat(response3, hasContent("third"));
    assertThat(response4, hasContent("third"));
    assertThat(response5, hasContent("third"));

    ClassicHttpResponse bar1 = httpClientMock.execute(new HttpGet("http://localhost/bar"));
    ClassicHttpResponse bar2 = httpClientMock.execute(new HttpGet("http://localhost/bar"));
    assertThat(bar1, hasContent("bar"));
    assertThat(bar2, hasStatus(300));

    assertThrows(IOException.class, () -> httpClientMock.execute(new HttpGet("http://localhost/bar")));

  }

  @Test
  public void should_support_response_in_different_charsets() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

    httpClientMock.onGet("/foo")
        .doReturn("first")
        .doReturn("second", StandardCharsets.UTF_16)
        .doReturn("third", StandardCharsets.US_ASCII);

    ClassicHttpResponse response1 = httpClientMock.execute(new HttpGet("http://localhost/foo"));
    ClassicHttpResponse response2 = httpClientMock.execute(new HttpGet("http://localhost/foo"));
    ClassicHttpResponse response3 = httpClientMock.execute(new HttpGet("http://localhost/foo"));

    assertThat(response1, hasContent("first", "UTF-8"));
    assertThat(response2, hasContent("second", "UTF-16"));
    assertThat(response3, hasContent("third", "ASCII"));
  }

  @Test
  public void should_support_response_with_different_contentType() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
    httpClientMock
        .onGet("/json").doReturn("{\"a\":1}", APPLICATION_JSON.withCharset(StandardCharsets.UTF_8));
    httpClientMock
        .onGet("/xml").doReturn("<a>1</a>", APPLICATION_XML.withCharset(StandardCharsets.UTF_8));

    ClassicHttpResponse jsonResponse = httpClientMock.execute(httpGet("http://localhost:8080/json"));
    ClassicHttpResponse xmlResponse = httpClientMock.execute(httpGet("http://localhost:8080/xml"));

    assertThat(jsonResponse, hasContent("{\"a\":1}"));
    assertThat(jsonResponse.getFirstHeader("Content-type").getValue(), equalTo(APPLICATION_JSON.toString()));
    assertThat(jsonResponse.getEntity().getContentType(), equalTo(APPLICATION_JSON.toString()));

    assertThat(xmlResponse, hasContent("<a>1</a>"));
    assertThat(xmlResponse.getFirstHeader("Content-type").getValue(), equalTo(APPLICATION_XML.toString()));
    assertThat(xmlResponse.getEntity().getContentType(), equalTo(APPLICATION_XML.toString()));

  }

  @Test
  public void should_support_response_in_body_with_status() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

    httpClientMock.onGet("/foo")
        .doReturn("first")
        .doReturn(300, "second")
        .doReturn(400, "third");

    ClassicHttpResponse response1 = httpClientMock.execute(new HttpGet("http://localhost/foo"));
    ClassicHttpResponse response2 = httpClientMock.execute(new HttpGet("http://localhost/foo"));
    ClassicHttpResponse response3 = httpClientMock.execute(new HttpGet("http://localhost/foo"));
    ClassicHttpResponse response4 = httpClientMock.execute(new HttpGet("http://localhost/foo"));
    ClassicHttpResponse response5 = httpClientMock.execute(new HttpGet("http://localhost/foo"));

    assertThat(response1, hasContent("first"));
    assertThat(response1, hasStatus(200));
    assertThat(response2, hasContent("second"));
    assertThat(response2, hasStatus(300));
    assertThat(response3, hasContent("third"));
    assertThat(response3, hasStatus(400));
    assertThat(response4, hasContent("third"));
    assertThat(response4, hasStatus(400));
    assertThat(response5, hasContent("third"));
    assertThat(response5, hasStatus(400));
  }

  @Test(expected = IOException.class)
  public void should_throw_exception_when_throwing_action_matched() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
    httpClientMock.onGet("/foo").doThrowException(new IOException());
    httpClientMock.execute(new HttpGet("http://localhost:8080/foo"));
  }

  @Test
  public void should_return_status_corresponding_to_match() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");

    httpClientMock.onGet("/login").doReturnStatus(200);
    httpClientMock.onGet("/abc").doReturnStatus(404);
    httpClientMock.onGet("/error").doReturnStatus(500);

    ClassicHttpResponse ok = httpClientMock.execute(new HttpGet("http://localhost:8080/login"));
    ClassicHttpResponse notFound = httpClientMock.execute(new HttpGet("http://localhost:8080/abc"));
    ClassicHttpResponse error = httpClientMock.execute(new HttpGet("http://localhost:8080/error"));

    assertThat(ok, hasStatus(200));
    assertThat(notFound, hasStatus(404));
    assertThat(error, hasStatus(500));

  }

  @Test
  public void should_do_custom_action() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
    httpClientMock.onPost("/login").doAction(echo());
    ClassicHttpResponse response = httpClientMock.execute(httpPost("http://localhost:8080/login", "foo bar"));

    assertThat(response, hasContent("foo bar"));

  }

  @Test
  public void should_add_header_to_response() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
    httpClientMock.onPost("/login")
        .doReturn("foo").withHeader("tracking", "123")
        .doReturn("foo").withHeader("tracking", "456");

    ClassicHttpResponse first = httpClientMock.execute(httpPost("http://localhost:8080/login"));
    ClassicHttpResponse second = httpClientMock.execute(httpPost("http://localhost:8080/login"));

    assertThat(first.getFirstHeader("tracking").getValue(), equalTo("123"));
    assertThat(second.getFirstHeader("tracking").getValue(), equalTo("456"));
  }

  @Test
  public void should_add_cookie_to_context() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
    httpClientMock.onPost("/login")
        .doReturn("foo").withCookie("cookieName", "cookieValue")
        .doReturn("foo").withCookie("cookieName", "cookieValue2");

    HttpClientContext httpClientContext = new HttpClientContext();
    httpClientMock.execute(httpPost("http://localhost:8080/login"), httpClientContext);
    assertThat(httpClientContext, hasCookie("cookieName", "cookieValue"));

    httpClientMock.execute(httpPost("http://localhost:8080/login"), httpClientContext);
    assertThat(httpClientContext, hasCookie("cookieName", "cookieValue2"));
  }

  @Test
  public void should_add_status_to_response() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
    httpClientMock.onGet("/login")
        .doReturn("foo").withStatus(300);
    ClassicHttpResponse login = httpClientMock.execute(httpGet("http://localhost:8080/login"));

    assertThat(login, hasContent("foo"));
    assertThat(login, hasStatus(300));

  }

  @Test
  public void should_return_json_with_right_header() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
    httpClientMock.onGet("/login")
        .doReturnJSON("{foo:1}", StandardCharsets.UTF_8);
    ClassicHttpResponse login = httpClientMock.execute(httpGet("http://localhost:8080/login"));

    assertThat(login, hasContent("{foo:1}"));
    assertThat(login.getFirstHeader("Content-type").getValue(), equalTo(APPLICATION_JSON.toString()));
  }

  @Test
  public void should_return_json_with_right_content_type() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
    httpClientMock.onGet("/login")
        .doReturnJSON("{foo:1}", StandardCharsets.UTF_8);
    ClassicHttpResponse login = httpClientMock.execute(httpGet("http://localhost:8080/login"));

    assertThat(login, hasContent("{foo:1}"));
    assertThat(login.getEntity().getContentType(), equalTo(APPLICATION_JSON.toString()));
  }

  @Test
  public void should_return_xml_with_right_header() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
    httpClientMock.onGet("/login")
        .doReturnXML("<foo>bar</foo>", StandardCharsets.UTF_8);
    ClassicHttpResponse login = httpClientMock.execute(httpGet("http://localhost:8080/login"));

    assertThat(login, hasContent("<foo>bar</foo>"));
    assertThat(login.getFirstHeader("Content-type").getValue(), equalTo(APPLICATION_XML.toString()));
  }

  @Test
  public void should_return_xml_with_right_content_type() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
    httpClientMock.onGet("/login")
        .doReturnXML("<foo>bar</foo>", StandardCharsets.UTF_8);
    ClassicHttpResponse login = httpClientMock.execute(httpGet("http://localhost:8080/login"));

    assertThat(login, hasContent("<foo>bar</foo>"));
    assertThat(login.getEntity().getContentType(), equalTo(APPLICATION_XML.toString()));
  }

  @Test
  public void should_not_set_response_entity_when_status_is_no_content() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
    httpClientMock.onGet("/login")
        .doReturnStatus(SC_NO_CONTENT);

    ClassicHttpResponse login = httpClientMock.execute(httpGet("http://localhost:8080/login"));

    assertNull(login.getEntity());
  }

  @Test
  public void should_not_throw_exception_when_body_matcher_is_present_on_post_request() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
    httpClientMock.onPost("/path1")
        .withBody(equalTo("Body content"))
        .doReturnStatus(200);

    ClassicHttpResponse response = httpClientMock.execute(httpGet("http://localhost:8080/path2"));
    Assert.assertThat(response, hasStatus(SC_NOT_FOUND));
  }

  @Test
  public void doReturnFormParams_should_returnResponseEntityWithFormParameters() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");

    List<NameValuePair> expected = Arrays.asList(
        new BasicNameValuePair("one", "1"),
        new BasicNameValuePair("two", "2")
    );
    httpClientMock.onGet("/path1").doReturnFormParams(expected);

    ClassicHttpResponse response = httpClientMock.execute(httpGet("http://localhost:8080/path1"));
    List<NameValuePair> actual = new UrlEncodedFormParser().parse(response.getEntity());

    assertThat(response, hasStatus(200));
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void doReturnFormParams_empty() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");

    List<NameValuePair> expected = Collections.emptyList();
    httpClientMock.onGet("/path1").doReturnFormParams(expected);

    ClassicHttpResponse response = httpClientMock.execute(httpGet("http://localhost:8080/path1"));
    List<NameValuePair> actual = new UrlEncodedFormParser().parse(response.getEntity());

    assertThat(response, hasStatus(200));
    Assert.assertEquals(expected, actual);
  }

  private Action echo() {
    return r -> {
      HttpEntity entity = ((HttpEntityContainer) r.getHttpRequest()).getEntity();
      BasicClassicHttpResponse response = new BasicClassicHttpResponse(200, "ok");
      response.setEntity(entity);
      return response;
    };
  }
}


