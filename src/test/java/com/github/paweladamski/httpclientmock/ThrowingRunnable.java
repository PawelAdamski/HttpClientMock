package com.github.paweladamski.httpclientmock;

@FunctionalInterface
interface ThrowingRunnable {

  void run() throws Exception;
}
