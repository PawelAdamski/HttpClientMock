package com.github.paweladamski.httpclientmock;

import com.github.paweladamski.httpclientmock.action.Action;
import com.github.paweladamski.httpclientmock.condition.Condition;
import com.github.paweladamski.httpclientmock.condition.HttpMethodCondition;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.List;

public class RuleBuilder {

    private final List<Action> actions = new ArrayList<>();
    private final List<Condition> conditions = new ArrayList<>();
    private final UrlConditions urlConditions = new UrlConditions();

    public RuleBuilder(String method, String defaultHost, String url) {
        UrlParser urlParser = new UrlParser();
        if (url.startsWith("/")) {
            url = defaultHost + url;
        }
        addCondition(new HttpMethodCondition(method));
        addUrlConditions(urlParser.parse(url));
    }

    public RuleBuilder(String method) {
        addCondition(new HttpMethodCondition(method));
    }

    void addAction(Action o) {
        actions.add(o);
    }

    void addCondition(Condition o) {
        conditions.add(o);
    }

    void addUrlConditions(UrlConditions newUrlConditions) {
        this.urlConditions.join(newUrlConditions);
    }

    void addParameterCondition(String name, Matcher<String> matcher) {
        UrlConditions urlConditions = new UrlConditions();
        urlConditions.getParameterConditions().put(name, matcher);
        addUrlConditions(urlConditions);
    }

    void addReferenceCondition(Matcher<String> matcher) {
        UrlConditions urlConditions = new UrlConditions();
        urlConditions.setReferenceConditions(matcher);
        addUrlConditions(urlConditions);
    }

    void addHostCondition(String host) {
        UrlParser urlParser = new UrlParser();
        UrlConditions urlConditions = new UrlConditions();
        urlConditions.setHostConditions(urlParser.parse(host).getHostConditions());
        addUrlConditions(urlConditions);
    }

    void addPathCondition(Matcher<String> matcher) {
        UrlConditions urlConditions = new UrlConditions();
        urlConditions.getPathConditions().add(matcher);
        addUrlConditions(urlConditions);
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
