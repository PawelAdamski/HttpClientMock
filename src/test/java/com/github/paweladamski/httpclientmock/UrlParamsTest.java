package com.github.paweladamski.httpclientmock;

import org.apache.http.NameValuePair;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class UrlParamsTest {

    @Test
    public void shouldParseQueryString() {
        List<NameValuePair> params = UrlParams.parse("a=1&b=2");
        assertEquals("a", params.get(0).getName());
        assertEquals("1", params.get(0).getValue());
        assertEquals("b", params.get(1).getName());
        assertEquals("2", params.get(1).getValue());
    }

    @Test
    public void shouldReturnEmptyListForNull() {
        assertEquals(0, UrlParams.parse(null).size());
    }

}
