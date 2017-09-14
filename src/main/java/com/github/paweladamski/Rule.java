package com.github.paweladamski;

import com.github.paweladamski.action.Action;
import com.github.paweladamski.action.StatusResponse;
import com.github.paweladamski.condition.Condition;
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

public class Rule {

    public static final Rule NOT_FOUND = new Rule();
    static {
        NOT_FOUND.addAction(new StatusResponse(SC_NOT_FOUND));
    }

    Queue<Action> actions = new LinkedList<>();
    List<Condition> conditions = new ArrayList<>();


    void addAction(Action o) {
        actions.add(o);
    }

    void addCondition(Condition o) {
        conditions.add(o);
    }

    boolean matches(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) {
        return conditions.stream()
                .allMatch(c->c.matches(httpHost,httpRequest,httpContext));
    }

    HttpResponse nextResponse() throws IOException{
        Action action;
        if (actions.size()>1) {
            action = actions.poll();
        } else {
            action = actions.peek();
        }
        return action.getResponse();
    }
}
