package com.github.paweladamski.httpclientmock.action;

import static org.apache.http.HttpStatus.SC_NO_CONTENT;

import com.github.paweladamski.httpclientmock.Request;
import java.io.IOException;
import java.util.Optional;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;

public class StatusWithEmptyEntityResponse implements Action {

  private final Optional<Action> parentAction;
  private final int status;
  private final String reason;

  public StatusWithEmptyEntityResponse(int status) {
    this.status = status;
    this.reason = "";
    this.parentAction = Optional.empty();
  }

  public StatusWithEmptyEntityResponse(Action parentAction, int status) {
    this.status = status;
    this.reason = "";
    this.parentAction = Optional.of(parentAction);
  }

  public StatusWithEmptyEntityResponse(Action parentAction, int status, String reason) {
    this.status = status;
    this.reason = reason;
    this.parentAction = Optional.of(parentAction);
  }

  @Override
  public HttpResponse getResponse(Request request) throws IOException {
    HttpResponse response;
    if (parentAction.isPresent()) {
      response = parentAction.get().getResponse(request);
    } else {
      response = new BasicHttpResponse(new ProtocolVersion("http", 1, 1), status, "");
      if (status != SC_NO_CONTENT) {
        response.setEntity(new StringEntity(""));
      }
    }
    response.setStatusCode(status);
    response.setReasonPhrase(reason);
    return response;
  }
}
