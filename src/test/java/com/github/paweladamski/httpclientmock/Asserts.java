package com.github.paweladamski.httpclientmock;

import org.junit.Assert;

@SuppressWarnings("unchecked")
class Asserts {
    static <T extends Throwable> T assertThrows(Class<T> expected, ThrowingRunnable action) throws Exception {
        try {
            action.run();
            Assert.fail("Did not throw expected " + expected.getSimpleName());
            return null; // never actually
        } catch (Exception actual) {
            if (!expected.isAssignableFrom(actual.getClass())) { // runtime '!(actual instanceof expected)'
                System.err.println("Threw " + actual.getClass().getSimpleName()
                        + ", which is not a subtype of expected "
                        + expected.getSimpleName());
                throw actual; // throw the unexpected Throwable for maximum transparency
            } else {
                return (T) actual; // return the expected Throwable for further examination
            }
        }
    }
}

