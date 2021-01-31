package com.github.paweladamski.httpclientmock.action;

import com.github.paweladamski.httpclientmock.Request;
import java.util.Optional;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;

public class StatusWithNullEntityResponse implements Action {

  private final Optional<String> reason;
  private final int status;

  public StatusWithNullEntityResponse(int status) {
    this.status = status;
    this.reason = Optional.empty();
  }

  public StatusWithNullEntityResponse(int status, String reason) {
    this.status = status;
    this.reason = Optional.of(reason);
  }

  @Override
  public ClassicHttpResponse getResponse(Request request) {
    ClassicHttpResponse response = new BasicClassicHttpResponse(status, "");
    response.setCode(status);
    reason.ifPresent(response::setReasonPhrase);
    return response;
  }
}
