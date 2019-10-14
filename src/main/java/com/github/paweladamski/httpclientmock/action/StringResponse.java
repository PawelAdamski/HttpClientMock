package com.github.paweladamski.httpclientmock.action;

import com.github.paweladamski.httpclientmock.Request;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;

import java.nio.charset.Charset;

public class StringResponse implements Action {

    private final int statusCode;
    private final String response;
    private final Charset charset;
    private final ContentType contentType;

    public StringResponse(String response, Charset charset) {
        this(200, response, charset);
    }

    public StringResponse(int statusCode, String response, Charset charset) {
        this(statusCode, response, charset, ContentType.TEXT_PLAIN);
    }

    public StringResponse(String response, Charset charset, ContentType contentType) {
        this(200, response, charset, contentType);
    }

    public StringResponse(int statusCode, String response, Charset charset, ContentType contentType) {
        this.statusCode = statusCode;
        this.response = response;
        this.charset = charset;
        this.contentType = contentType;
    }

    @Override
    public HttpResponse getResponse(Request request) {
        BasicHttpResponse response = new BasicHttpResponse(new ProtocolVersion("http", 1, 1), statusCode, "ok");
        StringEntity entity = new StringEntity(this.response, this.charset);
        entity.setContentType(contentType.toString());
        response.setEntity(entity);
        return response;
    }
}
