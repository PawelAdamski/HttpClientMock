package com.github.paweladamski;

import com.github.paweladamski.httpclientmock.UrlConditions;
import com.github.paweladamski.httpclientmock.UrlParser;
import org.junit.Before;
import org.junit.Test;

import static com.github.paweladamski.httpclientmock.UrlParser.EMPTY_PORT_NUMBER;
import static org.junit.Assert.assertTrue;

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
        assertTrue(urlConditions.getReferenceConditions().matches(""));
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
        assertTrue(urlConditions.getParameterConditions().get("a").get(0).matches("1"));
        assertTrue(urlConditions.getParameterConditions().get("b").get(0).matches("2"));
    }

    @Test
    public void parseHostReference() {
        UrlConditions urlConditions = urlParser.parse("http://localhost#abc");
        assertTrue(urlConditions.getHostConditions().get(0).matches("localhost"));
        assertTrue(urlConditions.getReferenceConditions().matches("abc"));
    }

}


