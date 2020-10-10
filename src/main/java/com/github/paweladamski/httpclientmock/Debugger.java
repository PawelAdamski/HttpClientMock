package com.github.paweladamski.httpclientmock;

import java.util.List;
import org.apache.hc.core5.http.HttpRequest;

public class Debugger {

  public Rule debug(List<Rule> rules, Request request) {
    logRequest(request);
    logRules(rules, request);
    return Rule.NOT_FOUND;
  }

  private void logRules(List<Rule> rules, Request request) {
    if (rules.size() == 0) {
      System.out.println("No rules were defined.");
    }
    for (int i = 0; i < rules.size(); i++) {
      System.out.println("Rule " + (i + 1) + ":");
      System.out.println("\tMATCHES\t\tEXPECTED");
      rules.get(i).debug(request, this);
    }
    System.out.println();
    System.out.println("----------------");

  }

  private void logRequest(Request request) {
    HttpRequest httpRequest = request.getHttpRequest();
    System.out.println("Host: " + request.getHttpHost() + " ,Request: " + httpRequest);
  }

  public void message(boolean matches, String expected) {
    String debugMessage = String.format("\t%s\t\t%s", matches, expected);
    System.out.println(debugMessage);
  }
}
