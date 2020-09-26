package com.github.paweladamski.httpclientmock;

import static org.junit.Assert.assertEquals;

import java.util.List;
import org.apache.hc.core5.http.NameValuePair;
import org.junit.Test;

public class UrlParamsParserTest {

  @Test
  public void shouldParseQueryString() {
    List<NameValuePair> params = new UrlParamsParser().parse("a=1&b=2");
    assertEquals("a", params.get(0).getName());
    assertEquals("1", params.get(0).getValue());
    assertEquals("b", params.get(1).getName());
    assertEquals("2", params.get(1).getValue());
  }

  @Test
  public void shouldReturnEmptyListForNull() {
    assertEquals(0, new UrlParamsParser().parse(null).size());
  }

}
