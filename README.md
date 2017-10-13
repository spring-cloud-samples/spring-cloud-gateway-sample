# Spring Cloud Gateway Sample

Sample that shows a few different ways to route and showcases some filters.

Run `DemogatewayApplication`

## Samples

```
$ http :8080/get
HTTP/1.1 200 OK
Access-Control-Allow-Credentials: true
Access-Control-Allow-Origin: *
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Connection: keep-alive
Content-Length: 257
Content-Type: application/json
Date: Fri, 13 Oct 2017 15:36:12 GMT
Expires: 0
Pragma: no-cache
Server: meinheld/0.6.1
Via: 1.1 vegur
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-Powered-By: Flask
X-Processed-Time: 0.00123405456543
X-XSS-Protection: 1 ; mode=block

{
    "args": {}, 
    "headers": {
        "Accept": "*/*", 
        "Accept-Encoding": "gzip, deflate", 
        "Connection": "close", 
        "Host": "httpbin.org", 
        "User-Agent": "HTTPie/0.9.8"
    }, 
    "origin": "207.107.158.66", 
    "url": "http://httpbin.org/get"
}


$ http :8080/headers Host:www.myhost.org
HTTP/1.1 200 OK
Access-Control-Allow-Credentials: true
Access-Control-Allow-Origin: *
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Connection: keep-alive
Content-Length: 175
Content-Type: application/json
Date: Fri, 13 Oct 2017 15:36:35 GMT
Expires: 0
Pragma: no-cache
Server: meinheld/0.6.1
Via: 1.1 vegur
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-Powered-By: Flask
X-Processed-Time: 0.0012538433075
X-XSS-Protection: 1 ; mode=block

{
    "headers": {
        "Accept": "*/*", 
        "Accept-Encoding": "gzip, deflate", 
        "Connection": "close", 
        "Host": "httpbin.org", 
        "User-Agent": "HTTPie/0.9.8"
    }
}

$ http :8080/foo/get Host:www.rewrite.org
HTTP/1.1 200 OK
Access-Control-Allow-Credentials: true
Access-Control-Allow-Origin: *
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Connection: keep-alive
Content-Length: 257
Content-Type: application/json
Date: Fri, 13 Oct 2017 15:36:51 GMT
Expires: 0
Pragma: no-cache
Server: meinheld/0.6.1
Via: 1.1 vegur
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-Powered-By: Flask
X-Processed-Time: 0.000664949417114
X-XSS-Protection: 1 ; mode=block

{
    "args": {}, 
    "headers": {
        "Accept": "*/*", 
        "Accept-Encoding": "gzip, deflate", 
        "Connection": "close", 
        "Host": "httpbin.org", 
        "User-Agent": "HTTPie/0.9.8"
    }, 
    "origin": "207.107.158.66", 
    "url": "http://httpbin.org/get"
}

$ http :8080/delay/2 Host:www.hystrix.org
HTTP/1.1 504 Gateway Timeout
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Expires: 0
Pragma: no-cache
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1 ; mode=block
content-length: 0


```

## Websocket Sample

[install wscat](https://www.npmjs.com/package/wscat)

In one terminal, run websocket server:
```
wscat --listen 9000
``` 

In another, run a client, connecting through gateway:
```
wscat --connect ws://localhost:8080/echo
```

type away in either server and client, messages will be passed appropriately.

## Running Redis Rate Limiter Test

Make sure redis is running on localhost:6379 (using brew or apt or docker).

Then run `DemogatewayApplicationTests`. It should pass which means one of the calls received a 429 TO_MANY_REQUESTS HTTP status.