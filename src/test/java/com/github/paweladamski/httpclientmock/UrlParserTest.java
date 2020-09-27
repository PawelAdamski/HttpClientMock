package com.github.paweladamski.httpclientmock;

import static com.github.paweladamski.httpclientmock.UrlParser.EMPTY_PORT_NUMBER;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class UrlParserTest {

  private UrlParser urlParser;

  @Before
  public void setUp() {
    urlParser = new UrlParser();
  }

  @Test
  public void parseHost() {
    UrlConditions urlConditions = urlParser.parse("http://localhost");
    assertTrue(urlConditions.getHostConditions().get(0).matches("localhost"));
    assertTrue(urlConditions.getPortConditions().get(0).matches(EMPTY_PORT_NUMBER));
  }

  @Test
  public void parseHostWithPort() {
    UrlConditions urlConditions = urlParser.parse("http://localhost:8080");
    assertTrue(urlConditions.getHostConditions().get(0).matches("localhost"));
    assertTrue(urlConditions.getPortConditions().get(0).matches(8080));
  }

  @Test
  public void parseHostAndPath() {
    UrlConditions urlConditions = urlParser.parse("http://localhost/foo/bar");
    assertTrue(urlConditions.getHostConditions().get(0).matches("localhost"));
    assertTrue(urlConditions.getPathConditions().get(0).matches("/foo/bar"));
  }

  @Test
  public void parseHostAndPathAndParameters() {
    UrlConditions urlConditions = urlParser.parse("http://localhost/foo/bar?a=1&b=2");
    assertTrue(urlConditions.getHostConditions().get(0).matches("localhost"));
    assertTrue(urlConditions.getPathConditions().get(0).matches("/foo/bar"));
    assertTrue(urlConditions.getUrlQueryConditions().matches("a=1&b=2"));
  }

}


