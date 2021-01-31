package com.github.paweladamski.httpclientmock.matchers;

import java.io.IOException;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpEntityContainer;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public final class HttpResponseMatchers {

  public static Matcher<? super HttpResponse> hasStatus(int expectedStatus) {
    return new BaseMatcher<HttpResponse>() {
      public boolean matches(Object o) {
        HttpResponse response = (HttpResponse) o;
        return response.getCode() == expectedStatus;
      }

      public void describeTo(Description description) {
        description.appendValue(expectedStatus);
      }
    };
  }

  public static Matcher<? super HttpResponse> hasNoEntity() {
    return new BaseMatcher<HttpResponse>() {
      public boolean matches(Object o) {
        HttpEntityContainer response = (HttpEntityContainer) o;
        return response.getEntity() == null;
      }

      public void describeTo(Description description) {
        description.appendValue(null);
      }
    };
  }



  public static Matcher<? super HttpResponse> hasReason(String expectedReason) {
    return new BaseMatcher<HttpResponse>() {
      public boolean matches(Object o) {
        HttpResponse response = (HttpResponse) o;
        return response.getReasonPhrase().equals(expectedReason);
      }

      public void describeTo(Description description) {
        description.appendValue(expectedReason);
      }
    };
  }



  public static Matcher<? super HttpEntityContainer> hasContent(final String content) {
    return hasContent(content, "UTF-8");
  }

  public static Matcher<? super HttpEntityContainer> hasContent(final String content, final String charset) {
    return new BaseMatcher<HttpEntityContainer>() {
      public boolean matches(Object o) {
        HttpEntityContainer response = (HttpEntityContainer) o;

        String targetString;
        try {
          byte[] bytes = EntityUtils.toByteArray(response.getEntity());
          targetString = new String(bytes, charset);
        } catch (IOException e) {
          e.printStackTrace();
          return false;
        }
        return targetString.equals(content);
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
        if (cookieStore == null) {
          return null;
        }

        return cookieStore.getCookies().stream()
            .filter(c -> c.getName().equalsIgnoreCase(cookieName))
            .findFirst().map(c -> c.getValue()).orElse(null);
      }
    };
  }
}
