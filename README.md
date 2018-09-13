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

# solution

A specific requirement was to NOT use any external frameworks - no spring, no maven, no log4j, not even Mockito for testing.

I used the HttpServer from com.sun.net.httpserver to not bother parsing the HTTP message. I have an example solution for that in the https://github.com/sorinslavic/adoberserver repo.

Since Spring IoC is not used - I used examples of singletons (eager init) for components that are shared accross threads (Bet Repository and Session Cache).
Similarly I avoided using log4j or any other sl4j framework and added a very basic Log class that will print to the system.out various details from within the code.

# HTTP Server
The port is currently hard-coded to 8001 - FIXME - might change :)

The application will await for HTTP messages received on the 8001 port and will handle them using a thread pool configuration. After some google - it seems that the recomended size for a thread pool is the number of processors on the machine. I am not sure if I am following that recommandation since I have a thread for the socket connection entry point into the system and then a thread pool to handle the requests.

Since there are a predefined number of messages we will handle - I have an intermediate dispatcher that will identify if the requests are GET - get session or get highstakes - or POST (post bet offer) - and will delegate to the appropriate handler to perform the actual processing.

If the GET or POST requests are handled successfuly - we will return the HTTP 200 OK status - otherwise we might return 404 (if the requested url is unknown), 403 Forbidden (if the session key is not valid) or 500 internal error (when an exception is caught).

# Session management
For the Session management I implemented a SessionCache class that has a ConcurrentHashMap that registered the generated sessions. The actuall session key is generated using a Random.nextLong number that is then converted to HEX so to have both numbers and letters (A-F) :)
It's a silly method - but I think it is optimized since the session map uses the Long ID as the Key - instead of the String representation and that would speed up access to the values.

Given the nature of Random - there are changes that the same session id will be generated - for this we use a fall back - if the generated session is already stored in the cache - we generate a new one.
This might have a performance penalty associated.
Another possible issue is the fact that "expired" sessions are still stored on the server in the hash map - possibly reducing search time calls.

# Bet Offer repository
The use case is the following: Using a generated session - each customer will place stakes for a particular bet offer. These stakes are processed at the same time - each customer might place multiple stakes. At any point - we might want to retrieve the top 20 stakes that are placed for a particular bet offer.

Since there is no persistency required for the data - the placed stakes are stored in memory - inside a class BetRepository.
Similarly to the SessionCache - the data is stored inside a ConcurrentHashMap linking Bet Offer IDS to a set containing all of the Customer Stakes that were placed. The set is kept sorted in descending order - to use it automatically on data retrieval.

Both the operations on the map and on the set are atomic and synchronized. 

# Testing
The only JARs that are include in the build path for the project are junit and hamcrest. 
I avoided using a mocking framework (EasyMock / Mockito) - and built my own "mocks" that have the option to return predefined data and to log what parameters certain methods were called for it.

For the statefull singletons - BetRepository, SessionCache - i was forced to add a "clear" method inside them that i used to "clear" the data from the map. FIXME - might change

# JS Client
Add an index.html page and some jQuery JS that will execute parallel GET/POST requests on the server.
