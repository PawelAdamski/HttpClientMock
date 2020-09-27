package com.github.paweladamski.httpclientmock.action;

import com.github.paweladamski.httpclientmock.Request;
import java.nio.charset.Charset;
import java.util.Collection;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;

/**
 * @author Michael Angstadt
 */
public class UrlEncodedFormEntityResponse implements Action {

  private final int statusCode;
  private final Collection<NameValuePair> pairs;
  private final Charset charset;

  public UrlEncodedFormEntityResponse(Collection<NameValuePair> pairs, Charset charset) {
    this(200, pairs, charset);
  }

  public UrlEncodedFormEntityResponse(int statusCode, Collection<NameValuePair> pairs, Charset charset) {
    this.statusCode = statusCode;
    this.pairs = pairs;
    this.charset = charset;
  }

  @Override
  public ClassicHttpResponse getResponse(Request request) {
    BasicClassicHttpResponse response = new BasicClassicHttpResponse(statusCode, "ok");
    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, charset);
    response.setEntity(entity);
    return response;
  }
}
