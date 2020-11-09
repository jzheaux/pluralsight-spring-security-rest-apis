Spring Security for REST APIs
------------------------

This repo demonstrates three main security concerns when securing a REST API:

1. Mitigating Cross-Origin Attacks
2. Authorizing with OAuth 2.0
3. Using an API Gateway

To see it working, please start the three different applications, like so:

* To start the Authorization Server, run: `cd authz && ./mvnw spring-boot:run`
* To start the Resource Server (the REST API), run `cd api && ./mvnw spring-boot:run`
* And, to start the SPA, run `cd app && ./mvnw spring-boot:run`

