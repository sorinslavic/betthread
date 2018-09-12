# betthread

Write a HTTP-based backend in Java which stores and provides the stakes for different customers and bet offers, with the capability to return the highest stakes on a bet offer. Also, the service needs to have a simple session management system as well, but with no authentication. 

Nonfunctional Requirements 
- There is no persistence and the application needs to be able to run indefinitely without crashing 
- The service needs to be able to handle a lot of simultaneous requests, so bear in mind the memory and CPU resources at your disposal 
- The data needs to stay consistent 
- Do not use any external frameworks, except maybe for testing. For HTTP the server it’s up to you on how you wish to implement it. If you wish to use an already implemented one, you can use com.sun.net.httpserver.HttpServer. 

Functional Requirements 
- <value> means a call parameter value or a return value 
- All calls need to result in a 200 HTTP status code, unless something goes wrong, when anything but 200 must be returned 
- Number parameters and returned values are sent in decimal ASCII as expected 
- Customers and bet offers are created on the spot, the first time they are referenced 
