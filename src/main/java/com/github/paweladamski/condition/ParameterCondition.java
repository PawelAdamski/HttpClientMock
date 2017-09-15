package com.github.paweladamski.condition;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.protocol.HttpContext;
import org.hamcrest.Matcher;

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
        String query = httpRequest.getRequestLine().getUri().replaceAll(".*\\?", "");
        List<NameValuePair> params = URLEncodedUtils.parse(query, Charset.defaultCharset());
        return params.stream()
                .anyMatch(p -> p.getName().equals(paramName) && value.matches(p.getValue()));
    }
}
