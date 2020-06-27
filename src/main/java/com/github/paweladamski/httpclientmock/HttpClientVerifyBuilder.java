package com.github.paweladamski.httpclientmock;

import static org.hamcrest.Matchers.equalTo;

import com.github.paweladamski.httpclientmock.condition.BodyMatcher;
import com.github.paweladamski.httpclientmock.condition.Condition;
import com.github.paweladamski.httpclientmock.condition.HeaderCondition;
import com.github.paweladamski.httpclientmock.matchers.ParametersMatcher;
import java.util.List;
import org.hamcrest.Matcher;

public class HttpClientVerifyBuilder {

  private final RuleBuilder ruleBuilder;
  private final List<Request> requests;

  HttpClientVerifyBuilder(RuleBuilder ruleBuilder, List<Request> requests) {
    this.requests = requests;
    this.ruleBuilder = ruleBuilder;
  }

  /**
   * Adds header condition. Header must be equal to provided value.
   *
   * @param header header name
   * @param value expected value
   * @return verification builder
   */
  public HttpClientVerifyBuilder withHeader(String header, String value) {
    return withHeader(header, equalTo(value));
  }

  /**
   * Adds header condition. Header must be equal to provided value.
   *
   * @param header header name
   * @param matcher header value matcher
   * @return verification builder
   */
  public HttpClientVerifyBuilder withHeader(String header, Matcher<String> matcher) {
    ruleBuilder.addCondition(new HeaderCondition(header, matcher));
    return this;
  }

  /**
   * Adds reference condition. Reference must be equal to provided value.
   *
   * @param reference expected reference
   * @return conditions builder
   */
  public HttpClientVerifyBuilder withReference(String reference) {
    return withReference(equalTo(reference));
  }

  /**
   * Adds reference condition. Reference must match.
   *
   * @param matcher reference matcher
   * @return conditions builder
   */
  public HttpClientVerifyBuilder withReference(Matcher<String> matcher) {
    ruleBuilder.addReferenceCondition(matcher);
    return this;
  }

  /**
   * Adds parameter condition. Parameter must be equal to provided value.
   *
   * @param name parameter name
   * @param value expected parameter value
   * @return verification builder
   */
  public HttpClientVerifyBuilder withParameter(String name, String value) {
    return withParameter(name, equalTo(value));
  }

  /**
   * Adds parameter condition. Parameter value must match.
   *
   * @param name parameter name
   * @param matcher parameter value matcher
   * @return verification builder
   */
  public HttpClientVerifyBuilder withParameter(String name, Matcher<String> matcher) {
    ruleBuilder.addParameterCondition(name, matcher);
    return this;
  }

  /**
   * Request body must contain the given URL-encoded form parameter (typically found in POST requests). Alternatively, parameters may be specified all at once
   * using {@link #withFormParameters(ParametersMatcher)}.
   *
   * @param name parameter name
   * @param value expected parameter value
   * @return condition builder
   */
  public HttpClientVerifyBuilder withFormParameter(String name, String value) {
    return withFormParameter(name, equalTo(value));
  }

  /**
   * Request body must contain the given URL-encoded form parameter (typically found in POST requests). Alternatively, parameters may be specified all at once
   * using {@link #withFormParameters(ParametersMatcher)}.
   *
   * @param name parameter name
   * @param matcher parameter value matcher
   * @return condition builder
   */
  public HttpClientVerifyBuilder withFormParameter(String name, Matcher<String> matcher) {
    ruleBuilder.addFormParameterCondition(name, matcher);
    return this;
  }

  /**
   * Request body must contain the given URL-encoded form parameters (typically used in POST requests). Alternatively, parameters may be specified individually
   * using {@link #withFormParameter(String, Matcher)}.
   *
   * @param parameters the parameters
   * @return condition builder
   */
  public HttpClientVerifyBuilder withFormParameters(ParametersMatcher parameters) {
    ruleBuilder.addFormParameterConditions(parameters);
    return this;
  }

  /**
   * Adds custom conditions.
   *
   * @param condition custom condition
   * @return verification builder
   */
  public HttpClientVerifyBuilder with(Condition condition) {
    ruleBuilder.addCondition(condition);
    return this;
  }

  /**
   * Adds body condition. Request body must match provided matcher.
   *
   * @param matcher custom condition
   * @return verification builder
   */
  public HttpClientVerifyBuilder withBody(Matcher<String> matcher) {
    ruleBuilder.addCondition(new BodyMatcher(matcher));
    return this;
  }

  /**
   * Adds host condition. Request host must be equal to provided value.
   *
   * @param host expected host
   * @return verification builder
   */
  public HttpClientVerifyBuilder withHost(String host) {
    ruleBuilder.addHostCondition(host);
    return this;
  }

  /**
   * Adds path condition. Request path must be equal to provided value.
   *
   * @param path expected path
   * @return verification builder
   */
  public HttpClientVerifyBuilder withPath(String path) {
    return withPath(equalTo(path));
  }

  /**
   * Adds path condition. Request path must match.
   *
   * @param matcher path matcher
   * @return verification builder
   */
  public HttpClientVerifyBuilder withPath(Matcher<String> matcher) {
    ruleBuilder.addPathCondition(matcher);
    return this;
  }

  /**
   * Allows additional parameters (not defined in condition) in query and form.
   *
   * @return condition builder
   */
  public HttpClientVerifyBuilder withAdditionalParameters() {
    ruleBuilder.setAllowExtraParameters(true);
    return this;
  }

  /**
   * Disallows additional parameters (not defined in condition) in query and form.
   *
   * @return condition builder
   */
  public HttpClientVerifyBuilder withoutAdditionalParameters() {
    ruleBuilder.setAllowExtraParameters(false);
    return this;
  }

  /**
   * Verifies if there were no request matching defined conditions.
   */
  public void notCalled() {
    called(0);
  }

  /**
   * Verifies if there was exactly one request matching defined conditions.
   */
  public void called() {
    called(1);
  }

  /**
   * Verifies number of request matching defined conditions.
   *
   * @param numberOfCalls expected number of calls
   */
  public void called(int numberOfCalls) {
    called(equalTo(numberOfCalls));
  }

  /**
   * Verifies number of request matching defined conditions.
   *
   * @param numberOfCalls expected number of calls
   */
  public void called(Matcher<Integer> numberOfCalls) {
    Rule rule = ruleBuilder.toRule();
    int matchingCalls = (int) requests.stream()
        .filter(rule::matches)
        .count();

    if (!numberOfCalls.matches(matchingCalls)) {
      throw new IllegalStateException(String.format("Expected %s calls, but found %s.", numberOfCalls, matchingCalls));
    }
  }

}
