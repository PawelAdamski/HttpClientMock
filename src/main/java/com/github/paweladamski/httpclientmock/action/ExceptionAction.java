package com.github.paweladamski.httpclientmock.action;

import com.github.paweladamski.httpclientmock.Request;
import org.apache.http.HttpResponse;

import java.io.IOException;

public class ExceptionAction implements Action {
    private final IOException exception;

    public ExceptionAction(IOException e) {
        this.exception = e;
    }

    @Override
    public HttpResponse getResponse(Request request) throws IOException {
        throw exception;
    }
}
