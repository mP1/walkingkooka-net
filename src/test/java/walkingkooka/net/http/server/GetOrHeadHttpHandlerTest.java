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
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.Url;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.reflect.JavaVisibility;

public final class GetOrHeadHttpHandlerTest implements GetOrHeadHttpHandlerTesting<GetOrHeadHttpHandler<FakeHttpHandlerContext>, FakeHttpHandlerContext> {

    @Test
    public void testHandlePostMethodNotAllowed() {
        final HttpResponse response = HttpResponses.recording();
        response.setStatus(HttpStatusCode.METHOD_NOT_ALLOWED.setMessage("Method POST not allowed"));
        response.setEntity(
            HttpEntity.EMPTY.addHeader(
                HttpHeaderName.ALLOW,
                Lists.of(
                    HttpMethod.GET,
                    HttpMethod.HEAD
                )
            )
        );

        this.handleAndCheck(
            HttpRequests.post(
                HttpTransport.UNSECURED,
                Url.parseRelative("/api/decimalNumberSymbols/*/localeStartsWith/English?offset=0&count=2"),
                HttpProtocolVersion.VERSION_1_0,
                HttpEntity.EMPTY.addHeader(
                    HttpHeaderName.ALLOW,
                    Lists.of(
                        HttpMethod.HEAD,
                        HttpMethod.GET
                    )
                )
            ),
            this.createContext(),
            response
        );
    }

    @Test
    public void testHandleGet() {
        final HttpStatus status = HttpStatusCode.OK.setMessage("Hello world 111 OK");
        final String responseText = "Hello world 222";

        final HttpResponse response = HttpResponses.recording();
        response.setStatus(status);
        response.setEntity(
            HttpEntity.EMPTY.setBodyText(responseText)
        );

        this.handleAndCheck(
            new GetOrHeadHttpHandler<FakeHttpHandlerContext>() {
                @Override
                public void handleGetOrHead(final HttpRequest request,
                                            final HttpResponse response,
                                            final FakeHttpHandlerContext context) {
                    checkEquals(
                        HttpMethod.GET,
                        request.method(),
                        "method"
                    );
                    response.setStatus(status);
                    response.setEntity(
                        HttpEntity.EMPTY.setBodyText(
                            request.bodyText()
                        )
                    );
                }
            },
            HttpRequests.get(
                HttpTransport.UNSECURED,
                Url.parseRelative("/api/decimalNumberSymbols/*/localeStartsWith/English?offset=0&count=2"),
                HttpProtocolVersion.VERSION_1_0,
                HttpEntity.EMPTY.setBodyText(responseText)
            ),
            this.createContext(),
            response
        );
    }

    @Test
    public void testHandleHead() {
        final HttpStatus status = HttpStatusCode.OK.setMessage("Hello world 111 OK");
        final String responseText = "Hello world 222";

        final HttpResponse response = HttpResponses.recording();
        response.setStatus(status);
        response.setEntity(
            HttpEntity.EMPTY.setBodyText(responseText)
        );

        this.handleAndCheck(
            new GetOrHeadHttpHandler<FakeHttpHandlerContext>() {
                @Override
                public void handleGetOrHead(final HttpRequest request,
                                            final HttpResponse response,
                                            final FakeHttpHandlerContext context) {
                    checkEquals(
                        HttpMethod.HEAD,
                        request.method(),
                        "method"
                    );
                    response.setStatus(status);
                    response.setEntity(
                        HttpEntity.EMPTY.setBodyText(
                            request.bodyText()
                        )
                    );
                }
            },
            HttpRequests.head(
                HttpTransport.UNSECURED,
                Url.parseRelative("/api/decimalNumberSymbols/*/localeStartsWith/English?offset=0&count=2"),
                HttpProtocolVersion.VERSION_1_0,
                HttpEntity.EMPTY.setBodyText(responseText)
            ),
            this.createContext(),
            response
        );
    }

    @Override
    public GetOrHeadHttpHandler<FakeHttpHandlerContext> createHttpHandler() {
        return new GetOrHeadHttpHandler<>() {

            @Override
            public void handleGetOrHead(final HttpRequest request,
                                        final HttpResponse response,
                                        final FakeHttpHandlerContext context) {
                response.setStatus(
                    HttpStatusCode.OK.setMessage("Hello")
                );
                response.setEntity(
                    HttpEntity.EMPTY.setBodyText("World")
                );
            }
        };
    }

    @Override
    public FakeHttpHandlerContext createContext() {
        return new FakeHttpHandlerContext();
    }

    @Override
    public Class<GetOrHeadHttpHandler<FakeHttpHandlerContext>> type() {
        return Cast.to(GetOrHeadHttpHandler.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
