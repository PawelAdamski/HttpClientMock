package com.github.paweladamski.httpclientmock.condition;

import com.github.paweladamski.httpclientmock.Debugger;
import com.github.paweladamski.httpclientmock.Request;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

public class HeaderCondition implements Condition {
    private final String header;
    private final Matcher<String> value;

    public HeaderCondition(String header, Matcher<String> value) {
        this.header = header;
        this.value = value;
    }

    @Override
    public boolean matches(Request request) {
        return request.getHttpRequest().getFirstHeader(header) != null &&
                value.matches(request.getHttpRequest().getFirstHeader(header).getValue());
    }

    @Override
    public void debug(Request request, Debugger debugger) {
        String matcherDesc = StringDescription.toString(value);
        debugger.message(matches(request), "header " + header + " is " + matcherDesc);
    }
}
