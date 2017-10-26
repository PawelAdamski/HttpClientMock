package com.github.paweladamski.httpclientmock.action;

import com.github.paweladamski.httpclientmock.Request;
import org.apache.http.HttpResponse;

import java.io.IOException;

public interface Action {
    HttpResponse getResponse(Request r) throws IOException;
}
