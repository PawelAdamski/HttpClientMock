package com.github.paweladamski.action;

import com.github.paweladamski.action.Action;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpResponse;

public class StatusResponse implements Action {
    int status;

    public StatusResponse(int status) {
        this.status = status;
    }

    @Override
    public HttpResponse getResponse() {
        return new BasicHttpResponse(new ProtocolVersion("http",1,1),status,"ok");

    }
}
