package com.github.paweladamski.httpclientmock.matchers;

import org.hamcrest.Matcher;

import java.util.ArrayList;

public class MatchersList<T> extends ArrayList<Matcher<T>> {

    public boolean allMatches(T value) {
        return this.stream()
                .allMatch(m -> m.matches(value));
    }

}
