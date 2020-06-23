package com.github.paweladamski.httpclientmock;

import static org.hamcrest.Matchers.isEmptyOrNullString;

import com.github.paweladamski.httpclientmock.matchers.MatchersList;
import com.github.paweladamski.httpclientmock.matchers.UrlQueryMatcher;
import java.net.MalformedURLException;
import java.net.URL;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;

public class UrlConditions {

  private static final int EMPTY_PORT = -1;
  private UrlQueryMatcher urlQueryConditions = new UrlQueryMatcher();
  private Matcher<String> referenceConditions = Matchers.isEmptyOrNullString();
  private MatchersList<String> hostConditions = new MatchersList<>();
  private MatchersList<String> pathConditions = new MatchersList<>();
  private MatchersList<Integer> portConditions = new MatchersList<>();
  private Matcher<String> schemaConditions = Matchers.any(String.class);

  public UrlQueryMatcher getUrlQueryConditions() {
    return urlQueryConditions;
  }

  public Matcher<String> getReferenceConditions() {
    return referenceConditions;
  }

  public void setReferenceConditions(Matcher<String> referenceConditions) {
    this.referenceConditions = referenceConditions;
  }

  public MatchersList<String> getHostConditions() {
    return hostConditions;
  }

  public void setHostConditions(MatchersList<String> hostConditions) {
    this.hostConditions = hostConditions;
  }

  public MatchersList<String> getPathConditions() {
    return pathConditions;
  }

  public MatchersList<Integer> getPortConditions() {
    return portConditions;
  }

  public void setSchemaConditions(Matcher<String> schemaConditions) {
    this.schemaConditions = schemaConditions;
  }

  boolean matches(String urlText) {
    try {
      URL url = new URL(urlText);

      return hostConditions.allMatches(url.getHost())
          && pathConditions.allMatches(url.getPath())
          && portConditions.allMatches(url.getPort())
          && referenceConditions.matches(url.getRef())
          && schemaConditions.matches(url.getProtocol())
          && urlQueryConditions.matches(url.getQuery());

    } catch (MalformedURLException e) {
      return false;
    }
  }


  void debug(Request request, Debugger debugger) {
    try {
      URL url = new URL(request.getUri());
      debugger.message(hostConditions.allMatches(url.getHost()), "schema is " + describe(schemaConditions));
      debugger.message(hostConditions.allMatches(url.getHost()), "host is " + hostConditions.describe());
      debugger.message(pathConditions.allMatches(url.getPath()), "path is " + pathConditions.describe());
      debugger.message(portConditions.allMatches(url.getPort()), "port is " + portDebugDescription());
      if (referenceConditions != isEmptyOrNullString() || !referenceConditions.matches(url.getRef())) {
        debugger.message(referenceConditions.matches(url.getRef()), "reference is " + describe(referenceConditions));
      }
      urlQueryConditions.describe(url.getQuery(), debugger);
    } catch (MalformedURLException e) {
      System.out.println("Can't parse URL: " + request.getUri());
    }
  }

  private String describe(Matcher<String> matcher) {
    return StringDescription.toString(matcher);
  }

  private String portDebugDescription() {
    return portConditions.allMatches(EMPTY_PORT) ? "empty" : portConditions.describe();
  }
}
