package com.github.paweladamski.condition;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.protocol.HttpContext;
import org.hamcrest.Matcher;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

public class ParameterCondition implements Condition {
    private final String paramName;
    private final Matcher<String> value;

    public ParameterCondition(String paramName, Matcher<String> value) {
        this.paramName = paramName;
        this.value = value;
    }

    @Override
    public boolean matches(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) {
        try {
            String query = new URL(httpRequest.getRequestLine().getUri()).getQuery();
            List<NameValuePair> params = URLEncodedUtils.parse(query, Charset.defaultCharset());
            return params.stream()
                    .anyMatch(p -> p.getName().equals(paramName) && value.matches(p.getValue()));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
