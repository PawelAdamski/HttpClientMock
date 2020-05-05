package com.github.paweladamski.httpclientmock;

import static com.github.paweladamski.httpclientmock.Requests.httpPost;
import static com.github.paweladamski.httpclientmock.Requests.httpPut;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import com.github.paweladamski.httpclientmock.matchers.MatchersMap;
import java.io.IOException;
import java.util.Arrays;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.message.BasicNameValuePair;
import org.hamcrest.Matchers;
import org.junit.Test;

public class HttpClientVerifyTest {

  @Test
  public void shouldHandleAllHttpMethods() throws IOException {

    HttpClientMock httpClientMock = new HttpClientMock();

    httpClientMock.execute(new HttpGet("http://localhost"));
    httpClientMock.execute(new HttpPost("http://localhost"));
    httpClientMock.execute(new HttpDelete("http://localhost"));
    httpClientMock.execute(new HttpPut("http://localhost"));
    httpClientMock.execute(new HttpHead("http://localhost"));
    httpClientMock.execute(new HttpOptions("http://localhost"));
    httpClientMock.execute(new HttpPatch("http://localhost"));

    httpClientMock.verify()
        .get("http://localhost")
        .called();
    httpClientMock.verify()
        .post("http://localhost")
        .called();
    httpClientMock.verify()
        .delete("http://localhost")
        .called();
    httpClientMock.verify()
        .put("http://localhost")
        .called();
    httpClientMock.verify()
        .options("http://localhost")
        .called();
    httpClientMock.verify()
        .head("http://localhost")
        .called();
    httpClientMock.verify()
        .patch("http://localhost")
        .called();
  }

  @Test
  public void shouldCountNumberOfHttpMethodCalls() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock();

    httpClientMock.execute(new HttpGet("http://localhost"));

    httpClientMock.execute(new HttpPost("http://localhost"));
    httpClientMock.execute(new HttpPost("http://localhost"));

    httpClientMock.execute(new HttpDelete("http://localhost"));
    httpClientMock.execute(new HttpDelete("http://localhost"));
    httpClientMock.execute(new HttpDelete("http://localhost"));

    httpClientMock.verify()
        .get("http://localhost")
        .called();
    httpClientMock.verify()
        .post("http://localhost")
        .called(2);
    httpClientMock.verify()
        .delete("http://localhost")
        .called(3);

    httpClientMock.verify().get().called(greaterThanOrEqualTo(1));
    httpClientMock.verify().post().called(greaterThanOrEqualTo(1));
    httpClientMock.verify().delete().called(greaterThanOrEqualTo(1));
  }

  @Test
  public void shouldCountNumberOfUrlCalls() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock();

    httpClientMock.execute(new HttpGet("http://localhost"));

    httpClientMock.execute(new HttpGet("http://www.google.com"));
    httpClientMock.execute(new HttpGet("http://www.google.com"));

    httpClientMock.execute(new HttpGet("http://example.com"));
    httpClientMock.execute(new HttpGet("http://example.com"));
    httpClientMock.execute(new HttpGet("http://example.com"));

    httpClientMock.verify()
        .get("http://localhost")
        .called();
    httpClientMock.verify()
        .get("http://www.google.com")
        .called(2);
    httpClientMock.verify()
        .get("http://example.com")
        .called(3);
  }

  @Test
  public void shouldVerifyBodyContent() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock();

    httpClientMock.execute(httpPost("http://localhost", "foo"));
    httpClientMock.execute(httpPost("http://localhost", "foo"));

    httpClientMock.execute(httpPut("http://localhost", "bar"));
    httpClientMock.execute(httpPut("http://localhost", "foo"));

    httpClientMock.verify()
        .post("http://localhost")
        .withBody(containsString("foo"))
        .called(2);
    httpClientMock.verify()
        .put("http://localhost")
        .withBody(containsString("bar"))
        .called();
    httpClientMock.verify()
        .get("http://localhost")
        .withBody(containsString("foo bar"))
        .notCalled();
  }

  @Test
  public void should_handle_path_with_query_parameter() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock();

    httpClientMock.execute(httpPost("http://localhost?a=1&b=2&c=3"));
    httpClientMock.execute(httpPost("http://localhost?a=1&b=2"));
    httpClientMock.execute(httpPost("http://localhost?a=1"));

    httpClientMock.verify()
        .post("http://localhost?d=3")
        .notCalled();
    httpClientMock.verify()
        .post("http://localhost?a=3")
        .notCalled();
    httpClientMock.verify()
        .post("http://localhost?a=1&b=2&c=3")
        .called(1);
    httpClientMock.verify()
        .post("http://localhost?a=1&b=2")
        .called(1);
    httpClientMock.verify()
        .post("http://localhost?a=1")
        .called(1);
    httpClientMock.verify()
        .post("http://localhost")
        .withParameter("a", "1")
        .called(1);

    httpClientMock.verify()
        .post("http://localhost")
        .notCalled();
  }

  @Test
  public void should_handle_path_with_reference() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock();

    httpClientMock.execute(httpPost("http://localhost?a=1#abc"));
    httpClientMock.execute(httpPost("http://localhost#xyz"));

    httpClientMock.verify()
        .post("http://localhost?a=1#abc")
        .called(1);
    httpClientMock.verify()
        .post("http://localhost#abc")
        .notCalled();
    httpClientMock.verify()
        .post("http://localhost#xyz")
        .called(1);
    httpClientMock.verify()
        .post("http://localhost")
        .notCalled();
  }

  @Test(expected = AssertionError.class)
  public void should_throw_exception_when_number_of_calls_is_wrong() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock();

    httpClientMock.execute(httpPost("http://localhost?a=1"));

    httpClientMock.verify()
        .post("http://localhost?a=1#abc")
        .called(2);
  }

  @Test
  public void should_allow_different_host_then_default() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

    httpClientMock.onGet("/login").doReturn("login");
    httpClientMock.onGet("http://www.google.com").doReturn("google");

    httpClientMock.execute(new HttpGet("http://localhost/login"));
    httpClientMock.execute(new HttpGet("http://www.google.com"));

    httpClientMock.verify().get("/login").called();
    httpClientMock.verify().get("http://www.google.com").called();
  }

  @Test
  public void should_check_header() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");

    httpClientMock.onGet("/login").doReturn("OK");

    HttpGet getMozilla = new HttpGet("http://localhost:8080/login");
    HttpGet getChrome = new HttpGet("http://localhost:8080/login");
    getMozilla.addHeader("User-Agent", "Mozilla");
    getChrome.addHeader("User-Agent", "Chrome");
    httpClientMock.execute(getChrome);
    httpClientMock.execute(getMozilla);

    httpClientMock.verify().get("/login").withHeader("User-Agent", "Mozilla").called();
    httpClientMock.verify().get("/login").withHeader("User-Agent", "Chrome").called();
    httpClientMock.verify().get("/login").withHeader("User-Agent", "IE").notCalled();
  }

  @Test
  public void withFormParameter_should_match_when_allParametersHaveMatchingValue() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock("http://localhost");
    
    HttpPost request = new HttpPost("http://localhost/login");
    request.setEntity(new UrlEncodedFormEntity(Arrays.asList(
      new BasicNameValuePair("username", "John"),
      new BasicNameValuePair("password", "secret!")
    )));
    httpClientMock.execute(request);
    
    httpClientMock.verify()
        .post("/login")
        .withFormParameter("username", "John")
        .withFormParameter("password", Matchers.containsString("secret"))
        .called();
  }

  @Test
  public void withFormParameter_should_notMatch_when_parameterHaveNotMatchingValue() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

    HttpPost request = new HttpPost("http://localhost/login");
    request.setEntity(new UrlEncodedFormEntity(Arrays.asList(
        new BasicNameValuePair("username", "John"),
        new BasicNameValuePair("password", "secret!")
    )));
    httpClientMock.execute(request);

    httpClientMock.verify()
        .post("/login")
        .withFormParameter("username", "John")
        .withFormParameter("password", Matchers.containsString("abc"))
        .notCalled();
  }

  @Test
  public void withFormParametersshould_match_when_allParametersHaveMatchingValue() throws IOException {
    HttpClientMock httpClientMock = new HttpClientMock("http://localhost");
    
    HttpPost request = new HttpPost("http://localhost/login");
    request.setEntity(new UrlEncodedFormEntity(Arrays.asList(
      new BasicNameValuePair("username", "John"),
      new BasicNameValuePair("password", "secret!")
    )));
    httpClientMock.execute(request);
    
    MatchersMap<String, String> parameters = new MatchersMap<>();
    parameters.put("username", Matchers.equalTo("John"));
    parameters.put("password", Matchers.containsString("secret"));
    httpClientMock.verify().post("/login")
        .withFormParameters(parameters)
        .called();
  }
}
