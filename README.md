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

### Verify


## Request matching
###Check HTTP method
HttpClientMock supports all HTTP methods. You can 

## Define response
asdf
## Verification
sdf
## Examples
sadf
ing
