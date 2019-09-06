[![Coverage Status](https://coveralls.io/repos/github/mP1/walkingkooka-net/badge.svg?branch=master)](https://coveralls.io/github/mP1/walkingkooka-net?branch=master)

# Basic Project

This project concentrates on providing improved type safe abstractions over numerous networking related artefacts and
paradigms, including:

- Better abstractions for creating and accessing components of absolute, relative and data URLs.
- Email address: Significantly improved parsing and decomposition including many unfamiliar nuances that are possible
  according to numerous RFCs.
- Headers: Rich header abstractions for most commonly used http headers.
- Http: Abstractions for numerous components of Http.
- HttpServer: A functional HTTP Server that leverages other abstractions in this project.


## Headers

A brief summary follows for some of the advantages of using the header abstractions. The well known interfaces 
`HttpServletRequest` and `HttpServletResponse` only provides header values as three rather basic types:
- String
- Long
- Date

This means the rich variety of information present in the plain text form of a header, would require regular expressions
or other string manipulations to extract the individual components. As an example something such as processing
the list of content types and honouring quality values from a request is not easily achieved starting with just a String.

Working with [Quality-values](https://developer.mozilla.org/en-US/docs/Glossary/Quality_values) becomes rather simple
using the provided abstractions and utility methods

    text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
    indicates the order of priority:

    Value	                            Priority
    text/html and application/xhtml+xml	1.0
    application/xml	                    0.9
    */*                                 0.8


## Dependencies

- [https://github.com/mP1/walkingkooka-route](https://github.com/mP1/walkingkooka-route)
- junit

No actual releases are available, instead the latest snapshot may be referenced in a Maven POM.xml using
[https://jitpack.io](https://jitpack.io) repository.

```xml
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

    <dependencies>
       <dependency>
            <groupId>com.github.mP1</groupId>
            <artifactId>walkingkooka-route</artifactId>
            <version>master-SNAPSHOT</version>
        </dependency>
    </dependencies>
```



## Getting the source

You can either download the source using the "ZIP" button at the top
of the github page, or you can make a clone using git:

```
git clone git://github.com/mP1/walkingkooka-net.git
```
