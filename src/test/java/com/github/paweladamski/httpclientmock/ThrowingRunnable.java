package com.github.paweladamski.httpclientmock;

@FunctionalInterface
public interface ThrowingRunnable {
    void run() throws Exception;
}
