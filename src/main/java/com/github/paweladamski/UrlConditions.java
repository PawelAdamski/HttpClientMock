package com.github.paweladamski;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.hamcrest.Matcher;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UrlConditions {

    Map<String, List<Matcher<String>>> parameterConditions = new HashMap<>();
    Matcher<String> referenceConditions;
    List<Matcher<String>> hostConditions = new ArrayList<>();
    List<Matcher<String>> pathConditions = new ArrayList<>();
    List<Matcher<Integer>> portConditions = new ArrayList<>();
    Matcher<String> schemaConditons;

    public void addParameterCondition(String name, Matcher<String> matcher) {
        parameterConditions.putIfAbsent(name, new ArrayList<>());
        parameterConditions.get(name).add(matcher);
    }

    boolean matches(String urlText) {
        try {
            URL url = new URL(urlText);

            if (!schemaConditons.matches(url.getProtocol())) {
                return false;
            }
            if (!referenceConditions.matches(url.getRef())) {
                return false;
            }
            for (Matcher<String> m : hostConditions) {
                if (!m.matches(url.getHost())) {
                    return false;
                }
            }
            for (Matcher<String> m : pathConditions) {
                if (!m.matches(url.getPath())) {
                    return false;
                }
            }

            for (Matcher<Integer> m : portConditions) {
                if (!m.matches(url.getPort())) {
                    return false;
                }
            }
            List<NameValuePair> params = URLEncodedUtils.parse(url.getQuery(), Charset.forName("UTF-8"));
            for (NameValuePair param : params) {
                if (!parameterConditions.containsKey(param.getName())) {
                    return false;
                }
                for (Matcher<String> m : parameterConditions.get(param.getName())) {
                    if (!m.matches(param.getValue())) {
                        return false;
                    }
                }
            }
            for (String param : parameterConditions.keySet()) {
                Optional<NameValuePair> paramValue = params.stream().filter(p -> p.getName().equals(param)).findAny();
                if (!paramValue.isPresent()) {
                    return false;
                }
            }
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }
}
