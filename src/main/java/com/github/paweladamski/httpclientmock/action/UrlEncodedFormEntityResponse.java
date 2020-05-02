package com.github.paweladamski.httpclientmock.action;

import java.nio.charset.Charset;
import java.util.Collection;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicHttpResponse;

import com.github.paweladamski.httpclientmock.Request;

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
  public HttpResponse getResponse(Request request) {
    BasicHttpResponse response = new BasicHttpResponse(new ProtocolVersion("http", 1, 1), statusCode, "ok");

    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, charset);
    response.setEntity(entity);

    return response;
  }
}
