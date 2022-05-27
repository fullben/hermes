# Hermes

A tiny, Spring Boot-based application that can be used to fetch web search results from various web search implementations for some given query value.

## Application Features

The primary feature of the application is the capability to return the first (regular, parsable) results of the [Google web search](https://www.google.com/) and the [Bing web search](http://www.bing.com/search). This can be achieved by issuing an HTTP GET request to the `HOST:PORT/api/search` endpoint and providing the appropriate parameters. Supported parameters are as follows:

Parameter|Description|Values|Required
---|---|---|---
`q`|The query term to be used|Any non-blank string|Yes
`n`|The number of results to be returned by the search|A positive integer|No (defaults to 10)
`p`|The search provider to be used|`google` or `bing`|No (defaults to `google`)

An example request for searching the term *neptune*, expecting 20 results and using Bing web search is shown in the following:

```
GET HOST:PORT/api/search?q=neptune&n=20&p=bing
```

Furthermore, the application features UI-based API documentation, which can be found at `HOST:PORT/swagger-ui/index.html`. This page can be used to inspect and try out the available API endpoints.

The application is "secured" using a very crude basic authentication implementation, relying on in-memory authentication. By default, two users are available:

- Regular user (username and password: *user*): This user is authorized to make calls to the search-endpoint.
- Admin user (username and password: *admin*): This user is authorized to perform all actions the regular user is allowed to execute. Furthermore, this user can access the API documentation.

## Setup and Deployment

For launching the application from an IDE, run the `HermesApplication` class.

To build the application, simply call the `gradle bootJar` task. This will generate an application JAR and place it in `PROJECT_ROOT/build/libs`.

For starting a Docker container hosting the application, simply call `docker-compose up -d` while in the root directory of the project. This will build and launch an appropriate container.

## Contributing

All changes must honor the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html). Please make sure to update tests and documentation as appropriate.
