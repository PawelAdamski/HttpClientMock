package com.github.paweladamski.httpclientmock.action;

import com.github.paweladamski.httpclientmock.Request;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;

import java.io.IOException;
import java.util.Optional;

public class StatusResponse implements Action {

    private final Optional<Action> parentAction;
    private final int status;

    public StatusResponse(int status) {
        this.status = status;
        this.parentAction = Optional.empty();
    }

    public StatusResponse(Action parentAction, int status) {
        this.status = status;
        this.parentAction = Optional.of(parentAction);
    }

    @Override
    public HttpResponse getResponse(Request request) throws IOException {
        HttpResponse response;
        if (parentAction.isPresent()) {
            response = parentAction.get().getResponse(request);
        } else {
            response = new BasicHttpResponse(new ProtocolVersion("http", 1, 1), status, "");
            response.setEntity(new StringEntity(""));
        }
        response.setStatusCode(status);
        return response;
    }
}
