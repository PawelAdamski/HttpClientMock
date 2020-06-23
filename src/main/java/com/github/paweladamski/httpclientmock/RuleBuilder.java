package com.github.paweladamski.httpclientmock;

import com.github.paweladamski.httpclientmock.action.Action;
import com.github.paweladamski.httpclientmock.condition.Condition;
import com.github.paweladamski.httpclientmock.condition.HttpMethodCondition;
import com.github.paweladamski.httpclientmock.condition.UrlEncodedFormCondition;
import com.github.paweladamski.httpclientmock.matchers.ParametersMatcher;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.Matcher;

class RuleBuilder {

  private final List<Action> actions = new ArrayList<>();
  private final List<Condition> conditions = new ArrayList<>();
  private final UrlEncodedFormCondition formParametersCondition = new UrlEncodedFormCondition();
  private final UrlConditions urlConditions;

  RuleBuilder(String method, String defaultHost, String url) {
    this.urlConditions = new UrlParser().parse(buildFinalUrl(defaultHost, url));
    addCondition(new HttpMethodCondition(method));
    addCondition(formParametersCondition);
  }

  RuleBuilder(String method) {
    this.urlConditions = new UrlConditions();
    addCondition(new HttpMethodCondition(method));
    addCondition(formParametersCondition);
  }

  private String buildFinalUrl(String defaultHost, String url) {
    if (url.startsWith("/")) {
      return defaultHost + url;
    } else {
      return url;
    }
  }



  void addAction(Action o) {
    actions.add(o);
  }

  void addCondition(Condition o) {
    conditions.add(o);
  }

  void addParameterCondition(String name, Matcher<String> matcher) {
    urlConditions.getUrlQueryConditions().put(name, matcher);
  }

  void addFormParameterCondition(String name, Matcher<String> matcher) {
    formParametersCondition.addExpectedParameter(name, matcher);
  }

  void addFormParameterConditions(ParametersMatcher parameters) {
    formParametersCondition.addExpectedParameters(parameters);
  }

  void addReferenceCondition(Matcher<String> matcher) {
    urlConditions.setReferenceConditions(matcher);
  }

  void addHostCondition(String host) {
    UrlParser urlParser = new UrlParser();
    urlConditions.setHostConditions(urlParser.parse(host).getHostConditions());
  }

  void addPathCondition(Matcher<String> matcher) {
    urlConditions.getPathConditions().add(matcher);
  }

  Action getLastAction() {
    return actions.get(actions.size() - 1);
  }

  void overrideLastAction(Action lastAction) {
    actions.set(actions.size() - 1, lastAction);
  }

  Rule toRule() {
    return new Rule(urlConditions, conditions, actions);
  }

}
