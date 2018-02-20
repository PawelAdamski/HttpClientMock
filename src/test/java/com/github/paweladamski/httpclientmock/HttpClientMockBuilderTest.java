package com.github.paweladamski.httpclientmock;

import com.github.paweladamski.httpclientmock.condition.Condition;
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

import static com.github.paweladamski.httpclientmock.Requests.httpPost;
import static com.github.paweladamski.httpclientmock.matchers.HttpResponseMatchers.hasContent;
import static com.github.paweladamski.httpclientmock.matchers.HttpResponseMatchers.hasStatus;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class HttpClientMockBuilderTest {

    @Test
    public void shouldMatchSeparateHostAndPath() throws IOException {
        HttpClientMock httpClientMock = new HttpClientMock();

        httpClientMock.onPost()
                .withHost("http://localhost")
                .withPath("/login")
                .doReturnStatus(200);

        HttpResponse ok = httpClientMock.execute(new HttpPost("http://localhost/login"));
        assertThat(ok, hasStatus(200));
    }

    @Test
    public void shouldMatchSeparatePathAndParameter() throws IOException {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onPost()
                .withPath("/login")
                .withParameter("a", "1")
                .doReturn("one");
        httpClientMock.onPost()
                .withPath("/login")
                .withParameter("b", "2")
                .doReturn("two");

        HttpResponse one = httpClientMock.execute(new HttpPost("http://localhost/login?a=1"));
        HttpResponse two = httpClientMock.execute(new HttpPost("http://localhost/login?b=2"));
        HttpResponse wrong = httpClientMock.execute(new HttpPost("http://localhost/login?a=1&b=2"));
        assertThat(one, hasContent("one"));
        assertThat(two, hasContent("two"));
        assertThat(wrong, hasStatus(404));
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
    public void should_check_custom_rule() throws IOException {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        Condition fooCondition = request -> request.getUri().contains("foo");

        httpClientMock.onGet("http://localhost/foo/bar")
                .with(fooCondition)
                .doReturn("yes");

        HttpResponse first = httpClientMock.execute(new HttpGet("http://localhost/foo/bar"));

        assertThat(first, hasContent("yes"));
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
                .onGet("/login").withHeader("User-Agent", "Mozilla")
                .doReturn("mozilla");
        httpClientMock
                .onGet("/login").withHeader("User-Agent", "Chrome")
                .doReturn("chrome");

        HttpGet getMozilla = new HttpGet("http://localhost:8080/login");
        HttpGet getChrome = new HttpGet("http://localhost:8080/login");
        HttpGet getSafari = new HttpGet("http://localhost:8080/login");
        getMozilla.addHeader("User-Agent", "Mozilla");
        getChrome.addHeader("User-Agent", "Chrome");
        getSafari.addHeader("User-Agent", "Safari");

        assertThat(httpClientMock.execute(getMozilla), hasContent("mozilla"));
        assertThat(httpClientMock.execute(getChrome), hasContent("chrome"));
        assertThat(httpClientMock.execute(getSafari), hasStatus(404));
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

    @Test
    public void when_url_contains_parameter_it_should_be_added_us_a_separate_condition() throws IOException {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onPost("/login?user=john")
                .doReturnStatus(400);
        httpClientMock.onPost("/login?user=john&pass=abc")
                .doReturnStatus(200);

        HttpResponse notFound = httpClientMock.execute(new HttpPost("http://localhost/login"));
        HttpResponse wrong = httpClientMock.execute(new HttpPost("http://localhost/login?user=john"));
        HttpResponse ok = httpClientMock.execute(new HttpPost("http://localhost/login?user=john&pass=abc"));
        HttpResponse notFound_2 = httpClientMock.execute(new HttpPost("http://localhost/login?user=john&pass=abc&foo=bar"));

        assertThat(notFound, hasStatus(404));
        assertThat(wrong, hasStatus(400));
        assertThat(ok, hasStatus(200));
        assertThat(notFound_2, hasStatus(404));
    }

    @Test
    public void when_url_contains_reference_it_should_be_added_us_a_separate_condition() throws IOException {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onPost("/login")
                .doReturnStatus(400);
        httpClientMock.onPost("/login#abc")
                .doReturnStatus(200);

        HttpResponse wrong = httpClientMock.execute(new HttpPost("http://localhost/login"));
        HttpResponse ok = httpClientMock.execute(new HttpPost("http://localhost/login#abc"));

        assertThat(wrong, hasStatus(400));
        assertThat(ok, hasStatus(200));
    }

    @Test
    public void should_handle_path_with_parameters_and_reference() throws IOException {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onPost("/login?p=1#abc")
                .doReturnStatus(200);

        HttpResponse wrong1 = httpClientMock.execute(new HttpPost("http://localhost/login"));
        HttpResponse wrong2 = httpClientMock.execute(new HttpPost("http://localhost/login?p=1"));
        HttpResponse wrong3 = httpClientMock.execute(new HttpPost("http://localhost/login#abc"));
        HttpResponse ok = httpClientMock.execute(new HttpPost("http://localhost/login?p=1#abc"));

        assertThat(wrong1, hasStatus(404));
        assertThat(wrong2, hasStatus(404));
        assertThat(wrong3, hasStatus(404));
        assertThat(ok, hasStatus(200));
    }

    @Test
    public void should_check_reference_value() throws IOException {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onPost("/login")
                .doReturnStatus(400);
        httpClientMock.onPost("/login")
                .withReference("ref")
                .doReturnStatus(200);

        HttpResponse wrong = httpClientMock.execute(new HttpPost("http://localhost/login"));
        HttpResponse ok = httpClientMock.execute(new HttpPost("http://localhost/login#ref"));

        assertThat(wrong, hasStatus(400));
        assertThat(ok, hasStatus(200));
    }

    @Test
    public void after_reset_every_call_should_result_in_status_404() throws IOException {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onPost("/login").doReturnStatus(200);
        httpClientMock.reset();

        HttpResponse login = httpClientMock.execute(new HttpPost("http://localhost/login"));

        assertThat(login, hasStatus(404));
    }

    @Test
    public void after_reset_number_of_calls_should_be_zero() throws IOException {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onPost("/login").doReturnStatus(200);
        httpClientMock.execute(new HttpPost("http://localhost/login"));
        httpClientMock.execute(new HttpPost("http://localhost/login"));
        httpClientMock.reset();
        httpClientMock.verify().post("/login").notCalled();

        httpClientMock.onPost("/login").doReturnStatus(200);
        httpClientMock.execute(new HttpPost("http://localhost/login"));
        httpClientMock.execute(new HttpPost("http://localhost/login"));
        httpClientMock.verify().post("/login").called(2);

    }

    @Test
    public void not_all_parameters_occurred() throws IOException {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onPost("/login")
                .withParameter("foo", "bar")
                .doReturnStatus(200);

        HttpResponse response = httpClientMock.execute(new HttpPost("http://localhost/login"));
        assertThat(response, hasStatus(404));
    }

    @Test
    public void should_allow_different_host_then_default() throws IOException {
        HttpClientMock httpClientMock = new HttpClientMock("http://localhost");

        httpClientMock.onGet("/login").doReturn("login");
        httpClientMock.onGet("http://www.google.com").doReturn("google");

        HttpResponse login = httpClientMock.execute(new HttpGet("http://localhost/login"));
        HttpResponse google = httpClientMock.execute(new HttpGet("http://www.google.com"));
        assertThat(login, hasContent("login"));
        assertThat(google, hasContent("google"));
    }

}


