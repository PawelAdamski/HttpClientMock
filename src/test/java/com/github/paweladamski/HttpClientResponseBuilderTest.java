package com.github.paweladamski;

import com.github.paweladamski.httpclientmock.HttpClientMock;
import com.github.paweladamski.httpclientmock.action.Action;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicHttpResponse;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;

import static com.github.paweladamski.Requests.httpGet;
import static com.github.paweladamski.Requests.httpPost;
import static com.github.paweladamski.httpclientmock.matchers.HttpResponseMatchers.hasContent;
import static com.github.paweladamski.httpclientmock.matchers.HttpResponseMatchers.hasStatus;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class HttpClientResponseBuilderTest {

    @Test
    public void should_return_staus_404_when_no_rule_matches() throws IOException {
        HttpClientMock httpClientMock = new HttpClientMock();
        HttpResponse notFound = httpClientMock.execute(new HttpGet("http://localhost/foo"));
        assertThat(notFound, hasStatus(404));
    }

    @Test
    public void should_use_next_action_after_every_call() throws IOException {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onGet("/foo")
                .doReturn("first")
                .doReturn("second")
                .doReturn("third");

        HttpResponse response1 = httpClientMock.execute(new HttpGet("http://localhost/foo"));
        HttpResponse response2 = httpClientMock.execute(new HttpGet("http://localhost/foo"));
        HttpResponse response3 = httpClientMock.execute(new HttpGet("http://localhost/foo"));
        HttpResponse response4 = httpClientMock.execute(new HttpGet("http://localhost/foo"));
        HttpResponse response5 = httpClientMock.execute(new HttpGet("http://localhost/foo"));

        assertThat(response1, hasContent("first"));
        assertThat(response2, hasContent("second"));
        assertThat(response3, hasContent("third"));
        assertThat(response4, hasContent("third"));
        assertThat(response5, hasContent("third"));

    }

    @Test
    public void should_support_response_in_different_charsets() throws IOException {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onGet("/foo")
                .doReturn("first")
                .doReturn("second", Charset.forName("UTF-16"))
                .doReturn("third", Charset.forName("ASCII"));

        HttpResponse response1 = httpClientMock.execute(new HttpGet("http://localhost/foo"));
        HttpResponse response2 = httpClientMock.execute(new HttpGet("http://localhost/foo"));
        HttpResponse response3 = httpClientMock.execute(new HttpGet("http://localhost/foo"));

        assertThat(response1, hasContent("first", "UTF-8"));
        assertThat(response2, hasContent("second", "UTF-16"));
        assertThat(response3, hasContent("third", "ASCII"));
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

        HttpResponse ok = httpClientMock.execute(new HttpGet("http://localhost:8080/login"));
        HttpResponse notFound = httpClientMock.execute(new HttpGet("http://localhost:8080/abc"));
        HttpResponse error = httpClientMock.execute(new HttpGet("http://localhost:8080/error"));

        assertThat(ok, hasStatus(200));
        assertThat(notFound, hasStatus(404));
        assertThat(error, hasStatus(500));

    }

    @Test
    public void should_do_custom_action() throws IOException {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
        httpClientMock.onPost("/login").doAction(echo());
        HttpResponse response = httpClientMock.execute(httpPost("http://localhost:8080/login", "foo bar"));

        assertThat(response, hasContent("foo bar"));

    }

    @Test
    public void should_add_header_to_response() throws IOException {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
        httpClientMock.onPost("/login")
                .doReturn("foo").withHeader("tracking", "123")
                .doReturn("foo").withHeader("tracking", "456");

        HttpResponse first = httpClientMock.execute(httpPost("http://localhost:8080/login"));
        HttpResponse second = httpClientMock.execute(httpPost("http://localhost:8080/login"));

        assertThat(first.getFirstHeader("tracking").getValue(), equalTo("123"));
        assertThat(second.getFirstHeader("tracking").getValue(), equalTo("456"));
    }

    @Test
    public void should_add_status_to_response() throws IOException {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");
        httpClientMock.onGet("/login")
                .doReturn("foo").withStatus(300);
        HttpResponse login = httpClientMock.execute(httpGet("http://localhost:8080/login"));

        assertThat(login, hasContent("foo"));
        assertThat(login, hasStatus(300));

    }

    private Action echo() {
        return r -> {
            HttpEntity entity = ((HttpEntityEnclosingRequestBase) r.getHttpRequest()).getEntity();
            BasicHttpResponse response = new BasicHttpResponse(new ProtocolVersion("http", 1, 1), 200, "ok");
            response.setEntity(entity);
            return response;
        };
    }
}


