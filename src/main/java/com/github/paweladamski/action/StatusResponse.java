package com.github.paweladamski.action;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;

import java.io.UnsupportedEncodingException;

public class StatusResponse implements Action {
    private final int status;

    public StatusResponse(int status) {
        this.status = status;
    }

    @Override
    public HttpResponse getResponse() {
        BasicHttpResponse response = new BasicHttpResponse(new ProtocolVersion("http", 1, 1), status, "");
        try {
            response.setEntity(new StringEntity(""));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }
}
