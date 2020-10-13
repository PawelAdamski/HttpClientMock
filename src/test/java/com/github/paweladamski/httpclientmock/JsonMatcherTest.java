package com.github.paweladamski.httpclientmock;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.Before;
import org.junit.Test;

import static com.spotify.hamcrest.jackson.IsJsonStringMatching.isJsonStringMatching;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonObject;
import static com.spotify.hamcrest.jackson.JsonMatchers.jsonText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class JsonMatcherTest {

    private final HttpClientMock mock = new HttpClientMock();

    @Before
    public void setUp() {
        mock.reset();
    }

    @Test
    public void testMatchingJsonBody() throws Exception {
        mock.onPost().withBody(isJsonStringMatching(
            jsonObject().where("foo", is(jsonText("bar")))
        )).doReturn("OK");

        final HttpPost post = new HttpPost("http://localhost");
        post.setEntity(new StringEntity("{\"foo\" : \"bar\" }"));
        final HttpResponse response = mock.execute(post);

        assertThat(response.getStatusLine().getStatusCode(), is(HttpStatus.SC_OK));
    }

    @Test
    public void testNonMatchingJsonBody() throws Exception {
        mock.onPost().withBody(isJsonStringMatching(
                jsonObject().where("foo", is(jsonText("bar")))
        )).doReturn("OK");

        final HttpPost post = new HttpPost("http://localhost");
        post.setEntity(new StringEntity("{\"foo\" : \"quax\" }"));
        final HttpResponse response = mock.execute(post);

        assertThat(response.getStatusLine().getStatusCode(), is(HttpStatus.SC_NOT_FOUND));
    }
}
