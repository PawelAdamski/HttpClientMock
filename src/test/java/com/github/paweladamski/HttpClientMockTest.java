package com.github.paweladamski;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import static org.hamcrest.MatcherAssert.assertThat;

public class HttpClientMockTest {


    @Test
    public void should_return_staus_404_when_no_rule_matches() throws IOException {
        HttpClientMock httpClientMock = new HttpClientMock();
        HttpResponse notFound = httpClientMock.execute(new HttpGet("http://localhost/foo"));
        assertThat(notFound, hasStatus(404));
    }


    @Test
    public void should_use_next_rule_after_every_call() throws IOException {
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
                .onGet("/mozilla") .withHeader("User-Agent","Mozilla")
                .doReturn("mozilla");
        httpClientMock
                .onGet("/chrome") .withHeader("User-Agent","Chrome")
                .doReturn("chrome");

        HttpGet getMozilla = new HttpGet("http://localhost:8080/mozilla");
        HttpGet getChrome = new HttpGet("http://localhost:8080/chrome");
        getMozilla.addHeader("User-Agent","Mozilla");
        getChrome.addHeader("User-Agent","Chrome");

        assertThat(httpClientMock.execute(getMozilla), hasContent("mozilla"));
        assertThat(httpClientMock.execute(getChrome), hasContent("chrome"));
    }


    @Test
    public void should_match_right_parameter_value() throws IOException {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost:8080");

        httpClientMock
                .onGet("/foo").withParameter("id","1").withParameter("name","abc")
                .doReturn("one");
        httpClientMock
                .onGet("/foo").withParameter("id","2")
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


    private Matcher<? super HttpResponse> hasContent(final String content) {
        return new BaseMatcher<HttpResponse>() {
            public boolean matches(Object o) {
                try {
                    HttpResponse response = (HttpResponse) o;
                    Reader reader = new InputStreamReader(response.getEntity().getContent());

                    int intValueOfChar;
                    String targetString = "";
                    while ((intValueOfChar = reader.read()) != -1) {
                        targetString += (char) intValueOfChar;
                    }
                    reader.close();

                    return targetString.equals(content);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            public void describeTo(Description description) {

            }
        };
    }

    private  Matcher<? super HttpResponse> hasStatus(int expectedStatus) {
        return new BaseMatcher<HttpResponse>() {
            public boolean matches(Object o) {
                    HttpResponse response = (HttpResponse) o;
                return response.getStatusLine().getStatusCode()==expectedStatus;
            }

            public void describeTo(Description description) {

            }
        };
    }

}


