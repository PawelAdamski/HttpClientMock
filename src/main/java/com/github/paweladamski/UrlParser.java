package com.github.paweladamski;


import com.github.paweladamski.condition.*;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;

public class UrlParser {

    public List<Condition> parse(String urlText){
        try {
            List<Condition> conditions = new ArrayList<>();
            URL url = new URL(urlText);
            int pathEnd = urlText.indexOf('?');
            if (pathEnd==-1) {
                pathEnd = urlText.indexOf('#');
            }
            String urlPath;
            if (pathEnd!=-1) {
                urlPath = urlText.substring(0,pathEnd);
            }
            else {
                urlPath = urlText;
            }

            conditions.add(new HostCondition(urlPath));

            if (url.getQuery()!=null) {
                List<NameValuePair> params = URLEncodedUtils.parse(url.getQuery(), Charset.forName("UTF-8"));
                for (NameValuePair param:params) {
                    conditions.add(new ParameterCondition(param.getName(),equalTo(param.getValue())));
                }
            }
            if (url.getRef()!=null ) {
                conditions.add(new ReferenceCondition(equalTo(url.getRef())));
            }
            return conditions;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
