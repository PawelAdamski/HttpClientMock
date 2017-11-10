# HttpClientMock

HttpClientMock is a library for mocking Apache HttpClient. It has an intuitive API for defining client behaviour and verifing number of made requests. 

* [Installation](#instalation)
* [Usage](#usage)
* [Request matching](#request-matching)
* [Define response](#define-response)
* [Verification](#verification)


## Installation 
HttpClientMock is available in Maven Central Repository. [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.paweladamski/HttpClientMock/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.paweladamski/HttpClientMock)

## Usage

#### Record
Working with HttpClientMock starts with defining client behaviour. Before code under tests starts HttpClientMock must know how to respond to every request.
```
HttpClientMock httpClientMock = new HttpClientMock();
httpClientMock.onGet("http://localhost/login")
  .withParameter("user","john")
  .doReturn("Ok");
httpClientMock.onPost("http://localhost/login").doReturnStatus(501);
```

#### Replay
Code uder test starts and uses HttpClientMock with defined behaviour.
```
httpClient.execute(new HttpGet("http://localhost/login?user:john")); // returns response with body "Ok"
httpClient.execute(new HttpPost("http://localhost/login")); // returns response with status 501
```

#### Verify
When code under test finishes, HttpClientMock allows to check number of made request. It is possible to use the same set of conditions as for defining mock behaviour.
```
httpClientMock.verify().get("http://localhost/login").withParameter("user","john").called()
httpClientMock.verify().post("http://localhost/login").notCalled()
```


## Request matching
On every request made using HttpClientMock each rule is checked if it matches. From all matching rules last defined one is selected. If no rule matches HttpClientMock return response with status 404.


### HTTP method
HttpClientMock supports all HTTP methods.
```
httpClientMock.onGet().doReturn("get");
httpClientMock.onPost().doReturn("post");
httpClientMock.onPut().doReturn("put");
httpClientMock.onDelete().doReturn("delete");
httpClientMock.onOptions().doReturn("options");
httpClientMock.onHead().doReturn("head");
```
### URL
Every `onGet(), onPost(), ....` method can accept URL. It is possible to write:
```
httpClientMock.onGet("http://localhost/login?user=john").doReturnStatus(200);
```
which is equal to
```
httpClientMock.onGet()
  .withHost("httt://locahost")
  .withPath("/login")
  .withParameter("user","john")
  .doReturnStatus(200);
```

HttpClientMock constuctor can accept default host, so later methods can accept relative URL-s.
```
HttpClientMock httpClientMock = new HttpClientMock("http://localhost");
httpClientMock.onGet("/login").doReturn("ok");
```

### Host, path, parameters, reference conditions
It is possible to define each part of url separately.
```
httpClientMock.onGet()
  .withHost("httt://locahost")
  .withPath("/login")
  .withParameter("user","john")
  .withReference("edit")
  .doReturnStatus(200);
```

### Header condition
```
httpClientMock.onGet("http://localhost/login")
  .withHeader("tracking","123")
  .doReturn("ok");
```

### Body condition
```
httpClientMock.onGet("http://localhost/login")
  .withBody("tracking",containsString(123))
  .doReturn("ok");
```

### Custom condition
```
Condition fooCondition = request -> request.getUri().contains("foo");
httpClientMock.onGet("http://localhost/foo/bar")
  .with(fooCondition)
  .doReturn("yes");
```         

### Matchers
Every condition method accepts [Hamcrest Matcher](https://github.com/hamcrest/JavaHamcrest) which allows to define custom conditions on requests.
```
httpClientMock.onGet("http://localhost")
  .withPath(containsString("login"))
  .withParameter("user",equalToIgnoringCase("John)")
  .reference(not(equalTo("edit")));
```

## Define response

### Response
Response with provided body and status 200.
```
httpClientMock.onGet("http://localhost").doReturn("my response")
```
### Status
Response with empty body and provided status
```
httpClientMock.onGet("http://localhost").doReturnStatus(300)
httpClientMock.onGet("http://localhost").doReturn("Overloaded").withStatus("500");
```
### Exception
Response with empty body and provided status
```
httpClientMock.onGet("http://localhost").doThrowException(new IOException());
```
### Custom action
Response with empty body and provided status
```
Action echo r -> {
  HttpEntity entity = ((HttpEntityEnclosingRequestBase) r.getHttpRequest()).getEntity();
  BasicHttpResponse response = new BasicHttpResponse(new ProtocolVersion("http", 1, 1), 200, "ok");
  response.setEntity(entity);
  return response;
};
httpClientMock.onGet("http://localhost").doAction(echo);
```
### Header
Adds header to response.
```
httpClientMock.onPost("/login").doReturn("foo").withHeader("tracking", "123")
```

### JSON
Response with provided body, status 200 and content type "application/json"
```
httpClientMock.onPost("/login").doReturnJSON("{foo:1}");
```

### XML
Response with provided body, status 200 and content type "application/xml"
```
httpClientMock.onPost("/login").doReturnXML("<foo>bar</foo>");
```

### Multiple actions
It is possible to add multiple actions to one rule. Every call will use next action until last is reached.
```
httpClientMock.onPut("/addUser")
  .doReturn("ok");
  .doReturnStatus(500);

httpClientMock.execute(new HttpPut("http://localhost/addUser")); //returns "ok"
httpClientMock.execute(new HttpPut("http://localhost/addUser")); //returns status 500
httpClientMock.execute(new HttpPut("http://localhost/addUser")); //returns status 500
```


## Verification
HttpClientMock allows to check how many calls were made. Verificatio supports the same set of conditions us rule defining.
```
httpClientMock.verify().get("http://localhost").called();

httpClientMock.verify().get("http://localhost/login")
  .withParameter("user","john")
  .called();

httpClientMock.verify().get("http://localhost/login")
  .withParameter("user","Ben")
  .notCalled();

httpClientMock.verify().delete().notCalled();

httpClientMock.verify().get().called(greaterThanOrEqualTo(1));

```
