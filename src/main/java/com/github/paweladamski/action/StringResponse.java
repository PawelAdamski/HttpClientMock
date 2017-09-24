package com.github.paweladamski.action;

import com.github.paweladamski.Request;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;

import java.nio.charset.Charset;

public class StringResponse implements Action {

    private final String response;
    private final Charset charset;

    public StringResponse(String response, Charset charset) {
        this.response = response;
        this.charset = charset;
    }

    @Override
    public HttpResponse getResponse(Request request) {
        BasicHttpResponse response = new BasicHttpResponse(new ProtocolVersion("http", 1, 1), 200, "ok");
        response.setEntity(new StringEntity(this.response, this.charset));
        return response;
    }
}
