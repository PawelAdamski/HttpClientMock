package com.github.paweladamski.httpclientmock;

import com.github.paweladamski.httpclientmock.action.Action;
import com.github.paweladamski.httpclientmock.action.StatusResponse;
import com.github.paweladamski.httpclientmock.condition.Condition;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;

import static java.util.Collections.emptyList;

public class Rule {

    public static final Rule NOT_FOUND = new Rule(new UrlConditions(), emptyList(), notFoundAction());

    private static List<Action> notFoundAction() {
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(new StatusResponse(SC_NOT_FOUND));
        return actions;
    }

    private final Queue<Action> actions;
    private final List<Condition> conditions;
    private final UrlConditions urlConditions;

    public Rule(UrlConditions urlConditions, List<Condition> conditions, List<Action> actions) {
        this.urlConditions = urlConditions;
        this.conditions = conditions;
        this.actions = new LinkedList(actions);
    }

    boolean matches(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) {
        return urlConditions.matches(httpRequest.getRequestLine().getUri())
                && conditions.stream()
                .allMatch(c -> c.matches(httpHost, httpRequest, httpContext));
    }

    HttpResponse nextResponse(Request request) throws IOException {
        Action action;
        if (actions.size() > 1) {
            action = actions.poll();
        } else {
            action = actions.peek();
        }
        return action.getResponse(request);
    }

}
