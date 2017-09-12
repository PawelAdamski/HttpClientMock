package com.github.paweladamski.action;

import com.github.paweladamski.action.Action;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;

import java.io.UnsupportedEncodingException;

public class StringResponse implements Action {
    String response;

    public StringResponse(String response) {
        this.response = response;
    }

    @Override
    public HttpResponse getResponse() {
        BasicHttpResponse response = new BasicHttpResponse(new ProtocolVersion("http",1,1),200,"ok");
        try {
            response.setEntity(new StringEntity(this.response));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return response ;
    }
}
