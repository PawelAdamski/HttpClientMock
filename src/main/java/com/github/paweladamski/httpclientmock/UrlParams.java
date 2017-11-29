package com.github.paweladamski.httpclientmock;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class UrlParams {

    public static List<NameValuePair> parse(String query) {
        return parse(query, Charset.forName("UTF-8"));
    }

    public static List<NameValuePair> parse(String query, Charset charset) {
        if (query == null) {
            return new ArrayList<>();
        } else {
            return URLEncodedUtils.parse(query, charset);
        }
    }
}
