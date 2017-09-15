# HttpClientMock

## Installation 
Simply add following snippet to your pom.
```
<dependency>
  <groupId>com.github.paweladamski</groupId>
  <artifactId>HttpClientMock</artifactId>
  <version>0.1</version>
</dependency>
```

## Usage
```
HttpClientMock httpClientMock = new HttpClientMock("http://example.com");
httpClientMock.onGet("/wow").doReturn("WOW GET");
httpClientMock.onPost("/wow").doReturn("WOW POST");
httpClientMock.onGet("/blabla").doReturnStatus(500);

httpClientMock.execute(new HttpGet("http://example.com/wow")); //returns "WOW GET"
httpClientMock.execute(new HttpPost("http://example.com/wow")); //returns WOW POST"
httpClientMock.execute(new HttpGet("http://example.com/blabla")); //returns message with status 500
