package com.github.paweladamski.action;

import org.apache.http.HttpResponse;

import java.io.IOException;


public class ExceptionAction implements Action {
    private final IOException exception;

    public ExceptionAction(IOException e) {
        this.exception = e;
    }

    @Override
    public HttpResponse getResponse() throws IOException{
        throw exception;
    }
}
