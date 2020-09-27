package com.github.paweladamski.httpclientmock;

import com.github.paweladamski.httpclientmock.matchers.MatchersList;
import com.github.paweladamski.httpclientmock.matchers.UrlQueryMatcher;
import java.net.URI;
import java.net.URISyntaxException;
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
    try {
      URI url = request.getUri();
      debugger.message(hostConditions.allMatches(url.getHost()), "schema is " + describe(schemaConditions));
      debugger.message(hostConditions.allMatches(url.getHost()), "host is " + hostConditions.describe());
      debugger.message(pathConditions.allMatches(url.getPath()), "path is " + pathConditions.describe());
      debugger.message(portConditions.allMatches(url.getPort()), "port is " + portDebugDescription());
      urlQueryConditions.describe(url.getQuery(), debugger);
    } catch (URISyntaxException e) {
      System.out.println("Can't parse URL");
    }
  }

  private String describe(Matcher<String> matcher) {
    return StringDescription.toString(matcher);
  }

  private String portDebugDescription() {
    return portConditions.allMatches(EMPTY_PORT) ? "empty" : portConditions.describe();
  }
}
