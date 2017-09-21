package com.github.paweladamski.matchers;

import org.apache.http.HttpResponse;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class HttpResponseMatchers {

    public static Matcher<? super HttpResponse> hasStatus(int expectedStatus) {
        return new BaseMatcher<HttpResponse>() {
            public boolean matches(Object o) {
                HttpResponse response = (HttpResponse) o;
                return response.getStatusLine().getStatusCode() == expectedStatus;
            }

            public void describeTo(Description description) {

            }
        };
    }

    public static Matcher<? super HttpResponse> hasContent(final String content) {
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
}
