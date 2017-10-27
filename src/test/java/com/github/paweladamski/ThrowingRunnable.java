package com.github.paweladamski;

@FunctionalInterface
public interface ThrowingRunnable {
    void run() throws Exception;
}
