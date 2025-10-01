[![Build Status](https://github.com/mP1/walkingkooka-net/actions/workflows/build.yaml/badge.svg)](https://github.com/mP1/walkingkooka-net/actions/workflows/build.yaml/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/mP1/walkingkooka-net/badge.svg?branch=master)](https://coveralls.io/github/mP1/walkingkooka-net?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/mP1/walkingkooka-net.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-net/context:java)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/mP1/walkingkooka-net.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/mP1/walkingkooka-net/alerts/)
![](https://tokei.rs/b1/github/mP1/walkingkooka-net)
[![J2CL compatible](https://img.shields.io/badge/J2CL-compatible-brightgreen.svg)](https://github.com/mP1/j2cl-central)

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



## Java example

A simple java example that shows the powerful immutable abstractions over many networking values offering type safety.

```java
assertEquals(Url.absolute(UrlScheme.HTTPS,
        AbsoluteUrl.NO_CREDENTIALS,
        HostAddress.with("example.com"),
        Optional.empty(),
        UrlPath.parse("/path1/path2"),
        UrlQueryString.EMPTY.addParameter(UrlParameterName.with("query3"), "value3"),
        UrlFragment.EMPTY),
        Url.parse("https://example.com/path1/path2?query3=value3"));

final EmailAddress email = EmailAddress.parse("user4@example5.com");
Assertions.assertEquals(HostAddress.with("example5.com"), email.host());
Assertions.assertEquals("user4", email.user());

// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Range
final ContentRange contentRange = ContentRange.parse("bytes 2-11/888");
assertEquals(RangeHeaderValueUnit.BYTES, contentRange.unit());
assertEquals(Optional.of(Range.greaterThanEquals(2L).and(Range.lessThanEquals(11L))), contentRange.range());
assertEquals(Optional.of(888L), contentRange.size());

// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept-Encoding
final AcceptEncoding acceptEncoding = AcceptEncoding.parse("a;q=0.5,b");
assertEquals(Lists.of(AcceptEncodingValue.with("b"),
        AcceptEncodingValue.with("a").setParameters(Maps.of(AcceptEncodingValueParameterName.with("q"), 0.5f))),
        acceptEncoding.qualityFactorSortedValues());
```

### [Converters](https://github.com/mP1/walkingkooka-convert/blob/master/src/main/java/walkingkooka/convert/Converter.java)

A collection of Converters for various types within `walkingkooka.net`

- [has-host-address](https://github.com/mP1/walkingkooka-net/blob/master/src/main/java/walkingkooka/net/convert/NetConverterHasHostAddress.java)
- [http-entity-content-type](https://github.com/mP1/walkingkooka-net/blob/master/src/main/java/walkingkooka/net/convert/NetConverterHttpEntityWithContentType.java)
- [net](https://github.com/mP1/walkingkooka-net/blob/master/src/main/java/walkingkooka/net/convert/NetConverters#net)
- [text-to-email-address](https://github.com/mP1/walkingkooka-net/blob/master/src/main/java/walkingkooka/net/convert/NetConverterTextToEmailAddress.java)
- [text-to-has-host-address](https://github.com/mP1/walkingkooka-net/blob/master/src/main/java/walkingkooka/net/convert/NetConverterTextToHasHostAddress.java)
- [text-to-host-address](https://github.com/mP1/walkingkooka-net/blob/master/src/main/java/walkingkooka/net/convert/NetConverterTextToHostAddress.java)
- [text-to-mail-to-url](https://github.com/mP1/walkingkooka-net/blob/master/src/main/java/walkingkooka/net/convert/NetConverterTextToMailToUrl.java)
- [text-to-url](https://github.com/mP1/walkingkooka-net/blob/master/src/main/java/walkingkooka/net/convert/NetConverterTextToUrl.java)
- [text-to-url-fragment](https://github.com/mP1/walkingkooka-net/blob/master/src/main/java/walkingkooka/net/convert/NetConverterTextToUrlFragment.java)
- [text-to-url-query-string](https://github.com/mP1/walkingkooka-net/blob/master/src/main/java/walkingkooka/net/convert/NetConverterTextToUrlQueryString.java)

### [Functions](https://github.com/mP1/walkingkooka-tree/blob/master/src/main/java/walkingkooka/tree/expression/function/ExpressionFunction.java)

Functions that will be useful to get or set components to various networking types such as

- [AbsoluteUrl](https://github.com/mP1/walkingkooka-net/blob/master/src/main/java/walkingkooka/net/AbsoluteUrl.java)
- [EmailAddress](https://github.com/mP1/walkingkooka-net/blob/master/src/main/java/walkingkooka/net/email/EmailAddress.java)
- [HostAddress](https://github.com/mP1/walkingkooka-net/blob/master/src/main/java/walkingkooka/net/HostAddress.java)
- [MailToUrl](https://github.com/mP1/walkingkooka-net/blob/master/src/main/java/walkingkooka/net/MailToUrl.java)
- [RelativeUrl](https://github.com/mP1/walkingkooka-net/blob/master/src/main/java/walkingkooka/net/RelativeUrl.java)
- [Url](https://github.com/mP1/walkingkooka-net/blob/master/src/main/java/walkingkooka/net/Url.java)
- [UrlFragment](https://github.com/mP1/walkingkooka-net/blob/master/src/main/java/walkingkooka/net/UrlFragment.java)
- [UrlPath](https://github.com/mP1/walkingkooka-net/blob/master/src/main/java/walkingkooka/net/UrlPath.java)
- [UrlQueryString](https://github.com/mP1/walkingkooka-net/blob/master/src/main/java/walkingkooka/net/UrlQueryString.java)

Available Functions

- [getHost](https://github.com/mP1/walkingkooka-net/blob/master/src/main/java/walkingkooka/net/expression/function/NetExpressionFunctionGetHost.java)
- [setHost](https://github.com/mP1/walkingkooka-net/blob/master/src/main/java/walkingkooka/net/expression/function/NetExpressionFunctionSetHost.java)
- [url](https://github.com/mP1/walkingkooka-net/blob/master/src/main/java/walkingkooka/net/expression/function/NetExpressionFunctionUrl.java)
