package com.github.paweladamski.httpclientmock.action;

import com.github.paweladamski.httpclientmock.Request;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;

public class StringResponse implements Action {

  private final int statusCode;
  private final String response;
  private final ContentType contentType;

  public StringResponse(int statusCode, String response, ContentType contentType) {
    this.statusCode = statusCode;
    this.response = response;
    this.contentType = contentType;
  }

  @Override
  public ClassicHttpResponse getResponse(Request request) {
    BasicClassicHttpResponse response = new BasicClassicHttpResponse(statusCode, "ok");
    StringEntity entity = new StringEntity(this.response, contentType, this.contentType.getCharset().toString(), false);
    response.addHeader("Content-type", contentType.toString());
    response.setEntity(entity);
    return response;
  }
}
