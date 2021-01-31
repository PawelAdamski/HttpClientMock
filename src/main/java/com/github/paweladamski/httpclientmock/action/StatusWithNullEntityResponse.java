package com.github.paweladamski.httpclientmock.action;

import com.github.paweladamski.httpclientmock.Request;
import java.util.Optional;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpResponse;

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
  public BasicHttpResponse getResponse(Request request) {
    return new BasicHttpResponse(new ProtocolVersion("http", 1, 1), status, reason.orElse(null));
  }
}