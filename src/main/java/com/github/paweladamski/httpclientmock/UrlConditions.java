package com.github.paweladamski.httpclientmock;

import com.github.paweladamski.httpclientmock.matchers.MatchersList;
import com.github.paweladamski.httpclientmock.matchers.UrlQueryMatcher;
import java.net.URI;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;

public class UrlConditions {

  private static final int EMPTY_PORT = -1;
  private UrlQueryMatcher urlQueryConditions = new UrlQueryMatcher();
  private MatchersList<String> hostConditions = new MatchersList<>();
  private MatchersList<String> pathConditions = new MatchersList<>();
  private MatchersList<Integer> portConditions = new MatchersList<>();
  private Matcher<String> schemaConditions = Matchers.any(String.class);

  public UrlQueryMatcher getUrlQueryConditions() {
    return urlQueryConditions;
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

  boolean matches(URI uri) {
    return hostConditions.allMatches(uri.getHost())
        && pathConditions.allMatches(uri.getPath())
        && portConditions.allMatches(uri.getPort())
        && schemaConditions.matches(uri.getScheme())
        && urlQueryConditions.matches(uri.getQuery());
  }

  void debug(Request request, Debugger debugger) {
    URI uri = request.getUri();
    debugger.message(schemaConditions.matches(uri.getScheme()), "schema is " + describe(schemaConditions));
    debugger.message(hostConditions.allMatches(uri.getHost()), "host is " + hostConditions.describe());
    debugger.message(pathConditions.allMatches(uri.getPath()), "path is " + pathConditions.describe());
    debugger.message(portConditions.allMatches(uri.getPort()), "port is " + portDebugDescription());
    urlQueryConditions.describe(uri.getQuery(), debugger);
  }

  private String describe(Matcher<String> matcher) {
    return StringDescription.toString(matcher);
  }

  private String portDebugDescription() {
    return portConditions.allMatches(EMPTY_PORT) ? "empty" : portConditions.describe();
  }
}
