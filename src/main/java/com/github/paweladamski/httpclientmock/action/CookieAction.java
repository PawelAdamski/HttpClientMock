package com.github.paweladamski.httpclientmock.action;

import com.github.paweladamski.httpclientmock.Request;
import org.apache.http.HttpResponse;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.IOException;

public class CookieAction implements Action {

    private final Action parentAction;
    private final String cookieName;
    private final String cookieValue;

    public CookieAction(Action parentAction, String cookieName, String cookieValue) {
        this.parentAction = parentAction;
        this.cookieName = cookieName;
        this.cookieValue = cookieValue;
    }

    @Override
    public HttpResponse getResponse(Request request) throws IOException {
        HttpResponse response = parentAction.getResponse(request);

        if(request.getHttpContext()==null) {
            throw new RuntimeException("No Http context");
        }
        if(!(request.getHttpContext() instanceof HttpClientContext)) {
            throw new RuntimeException("Http context is not a HttpClientContext instance.");
        }
        HttpClientContext httpClientContext = (HttpClientContext) request.getHttpContext();
        if (httpClientContext.getCookieStore() == null) {
            httpClientContext.setCookieStore(new BasicCookieStore());
        }
        httpClientContext.getCookieStore().addCookie(new BasicClientCookie(cookieName, cookieValue));

        return response;
    }

}
