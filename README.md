# Hermes

A tiny, Spring Boot-based application that can be used to fetch web search results from various web search implementations for some given query value.

## Application Features

The primary feature of the application is the capability to return the first (regular, parsable) results of the [Google web search](https://www.google.com/) and the [Bing web search](http://www.bing.com/search). This can be achieved by issuing an HTTP GET request to the `HOST:PORT/api/search` endpoint and providing the appropriate parameters. Supported parameters are as follows:

Parameter|Description|Values|Required
---|---|---|---
`q`|The query term to be used|Any non-blank string|Yes
`n`|The number of results to be returned by the search|A positive integer|No (defaults to 10)
`p`|The search provider to be used|`GOOGLE` or `BING`|No (defaults to `GOOGLE`)

An example request for searching the term *neptune*, expecting 20 results and using Bing web search is shown in the following:

```
GET HOST:PORT/api/search?q=neptune&n=20&p=BING
```

Furthermore, the application features UI-based API documentation, which can be found at `HOST:PORT/swagger-ui/index.html`. This page can be used to inspect and try out the available API endpoints.

The application is "secured" using a very crude basic authentication implementation, relying on in-memory authentication. By default, two users are available:

- Regular user (username and password: *user*): This user is authorized to make calls to the search-endpoint.
- Admin user (username and password: *admin*): This user is authorized to perform all actions the regular user is allowed to execute. Furthermore, this user can access the API documentation.

## Getting Started

To get a local copy up and running follow these simple steps.

### Prerequisites

Make sure you have the following software on your system:

- A Java 11 JDK (preferably [AdoptOpenJDK](https://adoptopenjdk.net/))
- A Java IDE (preferably [IntelliJ IDEA](https://www.jetbrains.com/idea/))

### Configuration

The application has configuration properties for tuning the behavior of the search implementation. The properties can be defined in the ``application.properties`` file. Their value ranges and effects are described in the following table.

Property|Value|Description
---|---|---
``hermes.search.cache-expire-after-mins``|Any number equal to or greater than one|The number of minutes for which each search result will remain in the cache.
``hermes.search.cache-max-size``|Any number equal to or greater than one|The maximum size of the query result cache.
``hermes.search.max-tries``|Any number equal to or greater than one|The maximum number of times the search implementation will attempt to contact the search provider for acquiring the desired amount of search results for a specified query term.

### Running the Application

For launching the application from an IDE, run the `HermesApplication` class.

To build the application, simply call the `gradle bootJar` task. This will generate an application JAR and place it in `PROJECT_ROOT/build/libs`.

For starting a Docker container hosting the application, call `docker-compose up -d` while in the root directory of the project. This will build and launch an appropriate container.

## Contributing

All changes must honor the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html). Please make sure to update tests and documentation as appropriate.
