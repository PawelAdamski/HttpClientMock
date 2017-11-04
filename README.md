# HttpClientMock


* [Installation](#instalation)
* [Request matching](#request-matching)
* [Define response](#define-response)
* [Verification](#verification)
* [Examples](#examples)


## Installation 
HttpClientMock is available in Maven Central Repository. [![Maven Central](https://maven-badges.herokuapp.com/maven-central/cz.jirutka.rsql/rsql-parser/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.paweladamski/HttpClientMock)

## Request matching
```
HttpClientMock httpClientMock = new HttpClientMock("http://example.com");
httpClientMock.onGet("/wow").doReturn("WOW GET");
httpClientMock.onPost("/wow").doReturn("WOW POST");
httpClientMock.onGet("/blabla").doReturnStatus(500);

httpClientMock.execute(new HttpGet("http://example.com/wow")); //returns "WOW GET"
httpClientMock.execute(new HttpPost("http://example.com/wow")); //returns WOW POST"
httpClientMock.execute(new HttpGet("http://example.com/blabla")); //returns message with status 500
```

## Define response
asdf
## Verification
sdf
## Examples
sadf
