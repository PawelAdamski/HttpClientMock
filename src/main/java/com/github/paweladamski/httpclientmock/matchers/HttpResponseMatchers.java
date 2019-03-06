package com.github.paweladamski.httpclientmock.matchers;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public final class HttpResponseMatchers {

    public static Matcher<? super HttpResponse> hasStatus(int expectedStatus) {
        return new BaseMatcher<HttpResponse>() {
            public boolean matches(Object o) {
                HttpResponse response = (HttpResponse) o;
                return response.getStatusLine().getStatusCode() == expectedStatus;
            }

            public void describeTo(Description description) {
                description.appendValue(expectedStatus);
            }
        };
    }

    public static Matcher<? super HttpResponse> hasContent(final String content) {
        return hasContent(content, "UTF-8");
    }

    public static Matcher<? super HttpResponse> hasContent(final String content, final String charset) {
        return new BaseMatcher<HttpResponse>() {
            public boolean matches(Object o) {
                try {
                    HttpResponse response = (HttpResponse) o;
                    Reader reader = new InputStreamReader(response.getEntity().getContent(), charset);

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
                description.appendText(content);
            }
        };
    }

    public static Matcher<? super HttpClientContext> hasCookie(final String expectedCookieName, final String expectedCookieValue) {
        return new BaseMatcher<HttpClientContext>() {
            public boolean matches(Object o) {
                HttpClientContext httpClientContext = (HttpClientContext) o;
                String cookieValue = getCookieValue(httpClientContext.getCookieStore(), expectedCookieName);
                return expectedCookieValue.equals(cookieValue);
            }

            public void describeTo(Description description) {
                description.appendValue(expectedCookieValue);
            }

            private String getCookieValue(CookieStore cookieStore, String cookieName) {
                if(cookieStore!=null) {
                    for (Cookie cookie : cookieStore.getCookies()) {
                        if(cookie.getName().equalsIgnoreCase(cookieName)) {
                            return cookie.getValue();
                        }
                    }
                }
                return null;
            }
        };
    }
}
