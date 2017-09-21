package com.github.paweladamski;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.junit.Test;

import java.io.IOException;

import static com.github.paweladamski.Requests.httpPost;
import static com.github.paweladamski.matchers.HttpResponseMatchers.hasContent;
import static com.github.paweladamski.matchers.HttpResponseMatchers.hasStatus;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class HttpClientMock_RulesTest {

    @Test
    public void shouldUseRightMethod() throws IOException {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onGet("/foo").doReturn("get");
        httpClientMock.onPost("/foo").doReturn("post");
        httpClientMock.onPut("/foo").doReturn("put");
        httpClientMock.onDelete("/foo").doReturn("delete");
        httpClientMock.onHead("/foo").doReturn("head");
        httpClientMock.onOptions("/foo").doReturn("options");
        httpClientMock.onPatch("/foo").doReturn("patch");

        HttpResponse getResponse = httpClientMock.execute(new HttpGet("http://localhost/foo"));
        HttpResponse postResponse = httpClientMock.execute(new HttpPost("http://localhost/foo"));
        HttpResponse putResponse = httpClientMock.execute(new HttpPut("http://localhost/foo"));
        HttpResponse deleteResponse = httpClientMock.execute(new HttpDelete("http://localhost/foo"));
        HttpResponse headResponse = httpClientMock.execute(new HttpHead("http://localhost/foo"));
        HttpResponse optionsResponse = httpClientMock.execute(new HttpOptions("http://localhost/foo"));
        HttpResponse patchResponse = httpClientMock.execute(new HttpPatch("http://localhost/foo"));

        assertThat(getResponse, hasContent("get"));
        assertThat(postResponse, hasContent("post"));
        assertThat(putResponse, hasContent("put"));
        assertThat(deleteResponse, hasContent("delete"));
        assertThat(headResponse, hasContent("head"));
        assertThat(optionsResponse, hasContent("options"));
        assertThat(patchResponse, hasContent("patch"));
    }

    @Test
    public void should_use_right_host_and_path() throws IOException {
        HttpClientMock httpClientMock = new HttpClientMock();

        httpClientMock.onGet("http://localhost:8080/foo").doReturn("localhost");
        httpClientMock.onGet("http://www.google.com").doReturn("google");
        httpClientMock.onGet("https://www.google.com").doReturn("https");

        HttpResponse localhost = httpClientMock.execute(new HttpGet("http://localhost:8080/foo"));
        HttpResponse google = httpClientMock.execute(new HttpGet("http://www.google.com"));
        HttpResponse https = httpClientMock.execute(new HttpGet("https://www.google.com"));

        assertThat(localhost, hasContent("localhost"));
        assertThat(google, hasContent("google"));
        assertThat(https, hasContent("https"));
    }

    @Test
    public void should_match_right_header_value() throws IOException {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");

        httpClientMock
                .onGet("/mozilla").withHeader("User-Agent", "Mozilla")
                .doReturn("mozilla");
        httpClientMock
                .onGet("/chrome").withHeader("User-Agent", "Chrome")
                .doReturn("chrome");

        HttpGet getMozilla = new HttpGet("http://localhost:8080/mozilla");
        HttpGet getChrome = new HttpGet("http://localhost:8080/chrome");
        getMozilla.addHeader("User-Agent", "Mozilla");
        getChrome.addHeader("User-Agent", "Chrome");

        assertThat(httpClientMock.execute(getMozilla), hasContent("mozilla"));
        assertThat(httpClientMock.execute(getChrome), hasContent("chrome"));
    }

    @Test
    public void should_match_right_parameter_value() throws IOException {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");

        httpClientMock
                .onGet("/foo").withParameter("id", "1").withParameter("name", "abc")
                .doReturn("one");
        httpClientMock
                .onGet("/foo").withParameter("id", "2")
                .doReturn("two");

        HttpResponse one = httpClientMock.execute(new HttpGet("http://localhost:8080/foo?id=1&name=abc"));
        HttpResponse two = httpClientMock.execute(new HttpGet("http://localhost:8080/foo?id=2"));

        assertThat(one, hasContent("one"));
        assertThat(two, hasContent("two"));
    }

    @Test
    public void should_add_default_host_to_every_relative_path() throws IOException {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");

        httpClientMock.onGet("/login").doReturn("login");
        httpClientMock.onGet("/product/search").doReturn("search");
        httpClientMock.onGet("/logout").doReturn("logout");

        HttpResponse login = httpClientMock.execute(new HttpGet("http://localhost:8080/login"));
        HttpResponse search = httpClientMock.execute(new HttpGet("http://localhost:8080/product/search"));
        HttpResponse logout = httpClientMock.execute(new HttpGet("http://localhost:8080/logout"));

        assertThat(login, hasContent("login"));
        assertThat(search, hasContent("search"));
        assertThat(logout, hasContent("logout"));

    }

    @Test
    public void checkBody() throws IOException {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");

        httpClientMock.onPost("/login")
                .doReturnStatus(500);
        httpClientMock.onPost("/login").withBody(containsString("foo"))
                .doReturnStatus(200);

        HttpResponse badLogin = httpClientMock.execute(new HttpPost("http://localhost:8080/login"));
        HttpResponse correctLogin = httpClientMock.execute(httpPost("http://localhost:8080/login", "foo"));

        assertThat(correctLogin, hasStatus(200));
        assertThat(badLogin, hasStatus(500));
    }

}


