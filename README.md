# Hermes
Hermes is a simple RESTful framework to create lightweight RESTful services with Java.

The Server part of Hermes is a very easy to use framework inspired by the JAX-RS API and the Client is a cool and neat API to send requests and get responses from such services.

It is written with the default Java threading model. In the background it uses a thread cache and pool to maximize the performance. It also uses a very basic ServerSocket for accepting connections, because it is the best for the short lifetime of the connections.
There is no need for complex I/O multiplexing.

Hermes is very decent by design and there for of course high-performance.

# Supported HTTP methods
- POST
- GET
- PUT
- DELETE
- PATCH

# Installation

- Install [Maven 3](http://maven.apache.org/download.cgi)
- Clone/Download this repo
- Install it with: ```mvn clean install```

**Maven dependencies**

_Client:_
```xml
<dependency>
    <groupId>de.progme</groupId>
    <artifactId>hermes-client</artifactId>
    <version>2.2.2-SNAPSHOT</version>
</dependency>
```
_Server:_
```xml
<dependency>
    <groupId>de.progme</groupId>
    <artifactId>hermes-server</artifactId>
    <version>2.2.2-SNAPSHOT</version>
</dependency>
```
_If you only want to use some of the shared code:_
```xml
<dependency>
    <groupId>de.progme</groupId>
    <artifactId>hermes-shared</artifactId>
    <version>2.2.2-SNAPSHOT</version>
</dependency>
```

# Quick start

### Client
_Simple POST request:_
```java
try {
	HermesClient client = HermesClientFactory.create();
	Response response = client.post(new URL("http://somesite.net:8080/some/path"), Body.form("name", "SomeName").form("data", "SomeData").build(), Headers.empty());
	System.out.println("Status: " + response.status());
	System.out.println("Body: " + response.body().content());
	for (Map.Entry<String, String> entry : response.headersMap().entrySet()) {
		System.out.println("Header: " + entry.getKey() + ": " + entry.getValue());
	}
} catch (Exception e) {
	e.printStackTrace();
}
```
_It is also possible to get the raw bytes from a response:_
```java
byte[] raw = response.body().bytes();
System.out.println("Body from bytes: " + new String(raw));
```

_Simple GET request:_
```java
try {
	HermesClient client = HermesClientFactory.create();
	Response response = client.get(new URL("http://somesite.net:8080/some/path"), Headers.empty());
	System.out.println("Status: " + response.status());
	System.out.println("Body: " + new String(response.body().bytes()));
	for (Map.Entry<String, String> entry : response.headersMap().entrySet()) {
		System.out.println("Header: " + entry.getKey() + ": " + entry.getValue());
	}   
} catch (Exception e) {
	e.printStackTrace();
}
```

_Simple delete request with a path parameter:_
```java
try {
	HermesClient client = HermesClientFactory.create();
	Response response = client.delete(new URL("http://localhost:8080/users/1"), Headers.empty());
    // For multiple words write a '+' instead of a blank
    //Response response = client.delete(new URL("http://localhost:8080/users/multiple+words+encoded"), Headers.empty());
    // Of course you can use URLEncoder as well
    //Response response = client.delete(new URL("http://localhost:8080/users/" + URLEncoder.encode("multiple words encoded", "UTF-8")), Headers.empty());
	System.out.println("Status: " + response.status());
} catch (Exception e) {
	e.printStackTrace();
}
```

_Simple file download:_
```java
HermesClient client = HermesClientFactory.create();
Response response = client.download(new URL("http://somesite.net:8080/some/download/path"), Headers.empty(), "C:\\Some\\Path\\To\\A\\Folder");
System.out.println("Status: " + response.status());
```

_JSON serialization:_
```java
// Imagine that we will get a response like this
/*
{
  "userId": 1,
  "id": 1,
  "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
  "body": "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum"
}
*/
try {
    HermesClient client = HermesClientFactory.create();
    
    // The 'User' class is a simple class with 4 fields (userId, id, title and body)
    // The body content will get automatically serialized from json to an instance of the given class
    User user = client.get(new URL("http://somesite.net:8080/some/path/"), Headers.empty(), User.class);
} catch (Exception e) {
	e.printStackTrace();
}
```

### Server
_Example RESTful server:_
```java
import HermesServer;
import HermesServerFactory;
import Request;
import FormParam;
import Path;
import Produces;
import GET;
import POST;
import HermesConfig;
import ContentType;
import Response;

/**
 * Created by Marvin Erkes on 17.02.2016.
 */
public class ExampleHermesServer {

    public static void main(String[] args) {

	// Create a new server instance with the hermes config below
        HermesServer hermesServer = HermesServerFactory.create(new ExampleConfig());
        // Start the server
        hermesServer.start();
    }

    public static class ExampleConfig extends HermesConfig {

        public ExampleConfig() {

            // Set the listening host ip
            host("0.0.0.0");
            // Set the port to listen on
            port(8080);

	    // Set the core size of the underlying thread pool executor
	    corePoolSize(8);
	    // Set the maximum size of the underlying thread pool executor
	    maxPoolSize(16);

       	    // Able to set the thread pool timeout to reduce thread size faster
            // Sets the timeout to 5 seconds
            threadPoolTimeout(5);
            
	    // Register our example resource
            register(ExampleResource.class);
            
            // Register our example filter
            filter(ExampleFilter.class);
        }
    }

    @Path("/rest/v1")
    public static class ExampleResource {

        @GET
        @Path("/hello")
        @Produces(ContentType.TEXT_HTML)
        public Response hello(Request httpRequest) {

            return Response.ok().content("<H1>Hello</H1>").build();
        }

        @POST
        @Path("/world")
        @Produces(ContentType.TEXT_HTML)
        public Response world(Request httpRequest, @FormParam("name") String name) {

            return Response.ok()
                    .content("<H1>World " + name + "!</H1>")
                    .header("X-Test", "Hello xD")
                    .build();
        }
        
        @GET
        @Path("/say/{text}")
        @Produces(ContentType.TEXT_HTML)
        public Response hello(Request httpRequest, @PathParam String text) {

            return Response.ok().content("<H1>" + text + "</H1>").build();
        }

        @GET
        @Path("/download")
        @Produces(ContentType.APPLICATION_OCTET_STREAM)
        public Response download(Request httpRequest) {

            return Response.file("text.zip");
            //return Response.file(new File("text.zip"));
        }
    }
    
    public static class ExampleFilter implements RequestFilter {

        @Override
        public void filter(FilteredRequest request) {

            // Here you can filter for headers or anything the request object has
            if(request.header("X-Special-Header") == null) {
                // Abort it
                request.abortWith(Response.status(Status.FORBIDDEN).build());
            }
        }
    }
}
```

# License

Licensed under the GNU General Public License, Version 3.0.
