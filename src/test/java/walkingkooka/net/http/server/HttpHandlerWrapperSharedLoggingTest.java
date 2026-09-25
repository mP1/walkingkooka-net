/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.net.http.server;

import org.junit.jupiter.api.Test;
import walkingkooka.Binary;
import walkingkooka.Cast;
import walkingkooka.HasCharsetTesting;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.logging.CanLog;
import walkingkooka.logging.CanLoggingLevels;
import walkingkooka.logging.CanLogs;
import walkingkooka.logging.LoggerPath;
import walkingkooka.logging.LoggingLevel;
import walkingkooka.net.Url;
import walkingkooka.net.header.Cookie;
import walkingkooka.net.header.CookieName;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.props.Properties;
import walkingkooka.text.HasLineEndingTesting;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.Printers;

public final class HttpHandlerWrapperSharedLoggingTest extends HttpHandlerWrapperSharedTestCase<HttpHandlerWrapperSharedLogging<HttpHandlerContext>, HttpHandlerContext>
    implements HasCharsetTesting,
    HasLineEndingTesting,
    ToStringTesting<HttpHandlerWrapperSharedLogging<HttpHandlerContext>> {

    @Test
    public void testHandleGet() {
        final StringBuilder b = new StringBuilder();

        this.createHttpHandler(
            new HttpHandler<HttpHandlerContext>() {
                @Override
                public void handle(final HttpRequest request,
                                   final HttpResponse response,
                                   final HttpHandlerContext context) {
                    response.setStatus(
                        HttpStatusCode.OK.status()
                    );
                    response.setEntity(
                        HttpEntity.EMPTY.setContentType(
                                MediaType.TEXT_PLAIN
                            ).setBodyText("ResponseBody111\nResponseBody222\nResponseBody333")
                            .setContentLength()
                    );
                }
            }
        ).handle(
            HttpRequests.get(
                HttpTransport.UNSECURED,
                Url.parseRelative("/file.txt"),
                HttpProtocolVersion.VERSION_1_0,
                HttpEntity.EMPTY.addHeader(
                    HttpHeaderName.COOKIE,
                    Lists.of(
                        Cookie.client(
                            CookieName.with("CookieName123"),
                            "CookieValue123"
                        )
                    )
                )
            ),
            HttpResponses.recording(),
            this.createContext(
                b,
                "" // enable all loggers
            )
        );

        this.checkEquals(
            "http.request INFO GET /file.txt HTTP/1.0\n" +
                "  http.request.headers DEBUG Cookie: CookieName123=CookieValue123;\n" +
                "http.response INFO 200 OK\n" +
                "  http.response.headers DEBUG Content-Type: text/plain\n" +
                "  http.response.headers DEBUG Content-Length: 47\n" +
                "  http.response.body DEBUG ResponseBody111\n" +
                "  ResponseBody222\n" +
                "  ResponseBody333\n",
            b.toString()
        );
    }

    @Test
    public void testHandleGetWithBinaryResponse() {
        final StringBuilder b = new StringBuilder();

        this.createHttpHandler(
            new HttpHandler<HttpHandlerContext>() {
                @Override
                public void handle(final HttpRequest request,
                                   final HttpResponse response,
                                   final HttpHandlerContext context) {
                    response.setStatus(
                        HttpStatusCode.OK.status()
                    );
                    response.setEntity(
                        HttpEntity.EMPTY.setContentType(
                            MediaType.BINARY
                        ).setBody(
                            Binary.with(
                                "Hello".getBytes(CHARSET)
                            )
                        ).setContentLength()
                    );
                }
            }
        ).handle(
            HttpRequests.get(
                HttpTransport.UNSECURED,
                Url.parseRelative("/file.bin"),
                HttpProtocolVersion.VERSION_1_0,
                HttpEntity.EMPTY.addHeader(
                    HttpHeaderName.COOKIE,
                    Lists.of(
                        Cookie.client(
                            CookieName.with("CookieName123"),
                            "CookieValue123"
                        )
                    )
                )
            ),
            HttpResponses.recording(),
            this.createContext(
                b,
                "" // enable all loggers
            )
        );

        this.checkEquals(
            "http.request INFO GET /file.bin HTTP/1.0\n" +
                "  http.request.headers DEBUG Cookie: CookieName123=CookieValue123;\n" +
                "http.response INFO 200 OK\n" +
                "  http.response.headers DEBUG Content-Type: application/octet-stream\n" +
                "  http.response.headers DEBUG Content-Length: 5\n",
            b.toString()
        );
    }

    @Test
    public void testHandlePostWithRequestBody() {
        final StringBuilder b = new StringBuilder();

        this.createHttpHandler(
            new HttpHandler<HttpHandlerContext>() {
                @Override
                public void handle(final HttpRequest request,
                                   final HttpResponse response,
                                   final HttpHandlerContext context) {
                    response.setStatus(
                        HttpStatusCode.OK.status()
                    );
                    response.setEntity(
                        HttpEntity.EMPTY.setContentType(
                            MediaType.BINARY
                        ).setBody(
                            Binary.with(
                                "Hello".getBytes(CHARSET)
                            )
                        ).setContentLength()
                    );
                }
            }
        ).handle(
            HttpRequests.post(
                HttpTransport.UNSECURED,
                Url.parseRelative("/file.bin"),
                HttpProtocolVersion.VERSION_1_0,
                HttpEntity.EMPTY.addHeader(
                        HttpHeaderName.COOKIE,
                        Lists.of(
                            Cookie.client(
                                CookieName.with("CookieName123"),
                                "CookieValue123"
                            )
                        )
                    ).setBodyText("RequestBody111\nRequest222\nRequest333\n")
                    .setContentType(MediaType.TEXT_PLAIN)
                    .setContentLength()
            ),
            HttpResponses.recording(),
            this.createContext(
                b,
                "" // enable all loggers
            )
        );

        this.checkEquals(
            "http.request INFO POST /file.bin HTTP/1.0\n" +
                "  http.request.headers DEBUG Cookie: CookieName123=CookieValue123;\n" +
                "  http.request.headers DEBUG Content-Type: text/plain\n" +
                "  http.request.headers DEBUG Content-Length: 37\n" +
                "  http.request.body DEBUG RequestBody111\n" +
                "  Request222\n" +
                "  Request333\n" +
                "  \n" +
                "http.response INFO 200 OK\n" +
                "  http.response.headers DEBUG Content-Type: application/octet-stream\n" +
                "  http.response.headers DEBUG Content-Length: 5\n",
            b.toString()
        );
    }

    @Override
    HttpHandlerWrapperSharedLogging<HttpHandlerContext> createHttpHandler(final HttpHandler<HttpHandlerContext> handler) {
        return HttpHandlerWrapperSharedLogging.with(handler);
    }

    @Override
    public HttpHandlerContext createContext() {
        return this.createContext(
            Printers.sink(
                LINE_ENDING
            ).indenting(INDENTATION),
            ""
        );
    }

    private HttpHandlerContext createContext(final StringBuilder b,
                                             final String properties) {
        return this.createContext(
            Printers.stringBuilder(
                b,
                LINE_ENDING
            ).indenting(INDENTATION),
            properties
        );
    }

    private HttpHandlerContext createContext(final IndentingPrinter printer,
                                             final String properties) {
        return new FakeHttpHandlerContext() {
            @Override
            public void debug(final String message) {
                this.canLog.log(
                    LoggingLevel.DEBUG,
                    message
                );
            }

            @Override
            public void info(final String message) {
                this.canLog.log(
                    LoggingLevel.INFO,
                    message
                );
            }

            @Override
            public boolean isDebugEnabled() {
                return this.canLog.isLoggingEnabled(LoggingLevel.DEBUG);
            }

            @Override
            public boolean isInfoEnabled() {
                return this.canLog.isLoggingEnabled(LoggingLevel.INFO);
            }

            @Override
            public void logEnter(final LoggerPath logger) {
                this.canLog.logEnter(logger);
            }

            @Override
            public void logExit() {
                this.canLog.logExit();
            }

            private final CanLog canLog = CanLogs.filter(
                CanLogs.indentingPrinter(printer),
                CanLoggingLevels.properties(
                    Properties.parse(properties),
                    LoggingLevel.DEBUG
                )
            );
        };
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createHttpHandler(),
            "logging FakeHttpHandler"
        );
    }

    // class............................................................................................................

    @Override
    public Class<HttpHandlerWrapperSharedLogging<HttpHandlerContext>> type() {
        return Cast.to(HttpHandlerWrapperSharedLogging.class);
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}
