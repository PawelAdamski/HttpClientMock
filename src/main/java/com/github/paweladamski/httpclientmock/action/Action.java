package com.github.paweladamski.httpclientmock.action;

import com.github.paweladamski.httpclientmock.Request;
import java.io.IOException;
import org.apache.hc.core5.http.ClassicHttpResponse;

public interface Action {

  ClassicHttpResponse getResponse(Request r) throws IOException;
}
