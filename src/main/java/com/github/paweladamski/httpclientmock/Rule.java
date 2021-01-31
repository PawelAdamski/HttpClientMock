package com.github.paweladamski.httpclientmock;

import static java.util.Collections.emptyList;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;

import com.github.paweladamski.httpclientmock.action.Action;
import com.github.paweladamski.httpclientmock.action.StatusWithEmptyEntityResponse;
import com.github.paweladamski.httpclientmock.condition.Condition;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

public class Rule {

  public static final Rule NOT_FOUND = new Rule(new UrlConditions(), emptyList(), notFoundAction());
  private final LinkedList<Action> actions;
  private final List<Condition> conditions;
  private final UrlConditions urlConditions;

  public Rule(UrlConditions urlConditions, List<Condition> conditions, List<Action> actions) {
    this.urlConditions = urlConditions;
    this.conditions = conditions;
    this.actions = new LinkedList<>(actions);
  }

  boolean matches(Request request) {
    return urlConditions.matches(request.getUri())
        && conditions.stream()
        .allMatch(c -> c.matches(request));
  }

  boolean matches(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) {
    return matches(new Request(httpHost, httpRequest, httpContext));
  }

  HttpResponse nextResponse(Request request) throws IOException {
    Action action = (actions.size() > 1) ? actions.poll() : actions.peek();
    return action.getResponse(request);
  }

  public void debug(Request request, Debugger debugger) {
    for (Condition condition : conditions) {
      condition.debug(request, debugger);
    }
    urlConditions.debug(request, debugger);
  }

  private static List<Action> notFoundAction() {
    ArrayList<Action> actions = new ArrayList<>();
    actions.add(new StatusWithEmptyEntityResponse(SC_NOT_FOUND));
    return actions;
  }

}
