# HttpClientMock

HttpClientMock is a library for mocking Apache HttpClient. Mocking it using existing frameworks is very cumbersome because of very general interface. HttpClientMock is record and replay framework with intuitive fluent API.

* [Installation](#instalation)
* [Usage](#usage)
* [Request matching](#request-matching)
* [Define response](#define-response)
* [Verification](#verification)
* [Examples](#examples)


## Installation 
HttpClientMock is available in Maven Central Repository. [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.paweladamski/HttpClientMock/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.paweladamski/HttpClientMock)

## Usage

### Record

```
HttpClientMock httpClientMock = new HttpClientMock();
httpClientMock.onGet("http://localhost/login")
  .withParameter("user","john")
  .doReturn("Ok");
httpClientMock.onPost("http://localhost/login").doReturnStatus(501);
```
### Replay
```
httpClient.execute(new HttpGet("http://localhost/login?user:john")); // returns response with body "Ok"
httpClient.execute(new HttpPost("http://localhost/login")); // returns response with status 501
```

### Verify
```
httpClientMock.verify().get("http://localhost/login").withParameter("user","john").called()
httpClientMock.verify().post("http://localhost/login").notCalled()
```


## Request matching
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
Every `on...()` method accept also URL. It is possible to write:
```
httpClientMock.onGet("http://localhost/login?user=john").doReturnStatus(200);
```

### Host, path, parameters, reference
It is possible to define each part of url separately.
```
httpClientMock.onGet()
  .withHost("httt://locahost")
  .withPath("/login")
  .withParameter("user","john")
  .withReference("edit")
  .doReturnStatus(200);
```

### Header
It is possbile to check request header value.
```
httpClientMock.onGet("http://localhost/login")
  .withHeader("tracking","123")
  .doReturn("ok");
```

### Body
It is possbile to check request body.
```
httpClientMock.onGet("http://localhost/login")
  .withBody("tracking","123")
  .doReturn("ok");
```

### Custom condition
```
        Condition fooCondition = new Condition() {
            @Override
            public boolean matches(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) {
                return httpRequest.getRequestLine().getUri().contains("foo");
            }
        };

        httpClientMock.onGet("http://localhost/foo/bar")
                .with(fooCondition)
                .doReturn("yes");
```         


## Define response
asdf
## Verification
sdf
## Examples
sadf
ing
