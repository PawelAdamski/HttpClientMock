package com.github.paweladamski.httpclientmock;

import com.github.paweladamski.httpclientmock.action.Action;
import com.github.paweladamski.httpclientmock.condition.Condition;

import java.util.ArrayList;
import java.util.List;

public class RuleBuilder {

    private final List<Action> actions = new ArrayList<>();
    private final List<Condition> conditions = new ArrayList<>();
    private final UrlConditions urlConditions = new UrlConditions();

    void addAction(Action o) {
        actions.add(o);
    }

    void addCondition(Condition o) {
        conditions.add(o);
    }

    void addUrlConditions(UrlConditions newUrlConditions) {
        this.urlConditions.join(newUrlConditions);
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
