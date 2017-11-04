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
httpClientMock.onGet("http://localhost/login").doReturn("Ok");
httpClientMock.onPost("http://localhost/login").doReturnStatus(501);
```
### Replay
```
httpClient.execute(new HttpGet("http://localhost/login")); // returns response with body "Ok"
httpClient.execute(new HttpPost("http://localhost/login")); // returns response with status 501
```

### Verify
```
httpClientMock.verify().get("http://localhost/login").called()
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

## Define response
asdf
## Verification
sdf
## Examples
sadf
ing
