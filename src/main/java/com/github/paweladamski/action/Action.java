package com.github.paweladamski.action;

import org.apache.http.HttpResponse;

import java.io.IOException;

public interface Action {
    HttpResponse getResponse() throws IOException;
}
