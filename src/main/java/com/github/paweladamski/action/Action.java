package com.github.paweladamski.action;

import com.github.paweladamski.Request;
import org.apache.http.HttpResponse;

import java.io.IOException;

public interface Action {
    HttpResponse getResponse(Request r) throws IOException;
}
