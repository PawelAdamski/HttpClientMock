package com.github.paweladamski;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.hamcrest.Matchers;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;

public class UrlParser {

    public static int EMPTY_PORT_NUMBER = -1;

    public UrlConditions parse(String urlText) {
        try {
            UrlConditions conditions = new UrlConditions();
            URL url = new URL(urlText);
            if (url.getRef() != null) {
                conditions.referenceConditions = equalTo(url.getRef());
            } else {
                conditions.referenceConditions = isEmptyOrNullString();
            }
            conditions.schemaConditons = Matchers.equalTo(url.getProtocol());
            conditions.hostConditions.add(equalTo(url.getHost()));
            conditions.portConditions.add(equalTo(url.getPort()));
            conditions.pathConditions.add(equalTo(url.getPath()));
            List<NameValuePair> params = URLEncodedUtils.parse(url.getQuery(), Charset.forName("UTF-8"));
            for (NameValuePair param : params) {
                conditions.addParameterCondition(param.getName(), equalTo(param.getValue()));
            }
            return conditions;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }

    }

}
