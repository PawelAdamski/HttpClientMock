package com.github.paweladamski.httpclientmock.action;

import com.github.paweladamski.httpclientmock.Request;
import java.io.IOException;
import org.apache.http.HttpResponse;

public interface Action {

  HttpResponse getResponse(Request r) throws IOException;
}
