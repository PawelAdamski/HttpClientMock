package com.github.paweladamski.httpclientmock.condition;

import com.github.paweladamski.httpclientmock.Debugger;
import com.github.paweladamski.httpclientmock.Request;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.util.EntityUtils;
import org.hamcrest.Matcher;

import java.io.IOException;

public class BodyMatcher implements Condition {

    private final Matcher<String> matcher;

    public BodyMatcher(Matcher<String> matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean matches(Request request) {
        try {
            HttpEntity entity = ((HttpEntityEnclosingRequestBase) request.getHttpRequest()).getEntity();
            if (entity == null) {
                return false;
            }
            String message = EntityUtils.toString(entity);
            return matcher.matches(message);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void debug(Request request, Debugger debugger) {
        debugger.message(matches(request), "body matches");
    }
}
