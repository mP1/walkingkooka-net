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
import walkingkooka.net.header.ContentEncoding;
import walkingkooka.net.header.ETagComputerTesting;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.net.http.HttpTransport;

import java.util.Objects;

public final class HttpHandlerWrapperSharedAutoGzipEncodingTest extends HttpHandlerWrapperSharedTestCase<HttpHandlerWrapperSharedAutoGzipEncoding<FakeHttpHandlerContext>, FakeHttpHandlerContext>
    implements
    ETagComputerTesting,
    HasCharsetTesting,
    ToStringTesting<HttpHandlerWrapperSharedAutoGzipEncoding<FakeHttpHandlerContext>> {

    private final static HttpHandler<FakeHttpHandlerContext> HTTP_HANDLER = new HttpHandler<>() {
        @Override
        public void handle(final HttpRequest request,
                           final HttpResponse response,
                           final FakeHttpHandlerContext context) {
            Objects.requireNonNull(request, "request");
            Objects.requireNonNull(response, "response");
            Objects.requireNonNull(context, "context");

            response.setStatus(HttpStatusCode.CREATED.status());
            response.clearEntity();
        }

        @Override
        public String toString() {
            return "HTTP_HANDLER";
        }
    };

    // handle...........................................................................................................

    @Test
    public void testHandleRequestAcceptEncodingsButResponseWithoutBody() {
        final HttpStatus httpStatus = HttpStatusCode.OK.status();
        final HttpEntity responseBody = HttpEntity.EMPTY.setContentType(MediaType.TEXT_PLAIN);

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(httpStatus);
        expected.setEntity(responseBody);

        this.handleAndCheck(
            this.createHttpHandler(
                httpStatus,
                responseBody
            ),
            HttpRequests.parse(
                HttpTransport.UNSECURED,
                "GET /api/spreadsheet/1/storage/ HTTP/1.0\r\n" +
                    "Accept-Encoding: gzip\r\n" +
                    "\r\n"
            ),
            expected
        );
    }

    @Test
    public void testHandleResponseNonEmptyMissingAcceptEncoding() {
        final HttpStatus httpStatus = HttpStatusCode.OK.status();
        final HttpEntity responseBody = HttpEntity.EMPTY.setContentType(MediaType.TEXT_PLAIN)
            .setBodyText("BodyText111");

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(httpStatus);
        expected.setEntity(responseBody);

        this.handleAndCheck(
            this.createHttpHandler(
                httpStatus,
                responseBody
            ),
            HttpRequests.parse(
                HttpTransport.UNSECURED,
                "GET /api/spreadsheet/1/storage/ HTTP/1.0\r\n" +
                    "\r\n"
            ),
            expected
        );
    }

    @Test
    public void testHandleResponseNonEmptyWithAcceptEncoding() {
        final HttpStatus httpStatus = HttpStatusCode.OK.status();

        final HttpEntity responseBody = HttpEntity.EMPTY.setContentType(MediaType.TEXT_PLAIN)
            .setBody(
                Binary.with(
                    "BodyText111".getBytes(CHARSET)
                )
            );

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(httpStatus);
        expected.setEntity(
            responseBody.setBody(
                HttpHandlerWrapperSharedAutoGzipEncoding.gzip(
                    responseBody.binary()
                )
            ).addHeader(
                HttpHeaderName.CONTENT_ENCODING,
                ContentEncoding.GZIP
            ).setContentLength()
        );

        this.handleAndCheck(
            this.createHttpHandler(
                httpStatus,
                responseBody
            ),
            HttpRequests.parse(
                HttpTransport.UNSECURED,
                "GET /api/spreadsheet/1/storage/ HTTP/1.0\r\n" +
                    "Accept-Encoding: gzip\r\n" +
                    "\r\n"
            ),
            expected
        );
    }

    private HttpHandlerWrapperSharedAutoGzipEncoding<FakeHttpHandlerContext> createHttpHandler(final HttpStatus httpStatus,
                                                                                               final HttpEntity httpEntity) {
        return this.createHttpHandler(
            new FakeHttpHandler<>() {
                @Override
                public void handle(final HttpRequest request,
                                   final HttpResponse response,
                                   final FakeHttpHandlerContext context) {
                    response.setStatus(httpStatus);
                    response.setEntity(httpEntity);
                }
            }
        );
    }

    @Override
    HttpHandlerWrapperSharedAutoGzipEncoding<FakeHttpHandlerContext> createHttpHandler(final HttpHandler<FakeHttpHandlerContext> httpHandler) {
        return HttpHandlerWrapperSharedAutoGzipEncoding.with(httpHandler);
    }

    @Override
    public FakeHttpHandlerContext createContext() {
        return new FakeHttpHandlerContext() {
        };
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createHttpHandler(HTTP_HANDLER),
            "AutoGzipEncoding " + HTTP_HANDLER
        );
    }

    // class............................................................................................................

    @Override
    public Class<HttpHandlerWrapperSharedAutoGzipEncoding<FakeHttpHandlerContext>> type() {
        return Cast.to(HttpHandlerWrapperSharedAutoGzipEncoding.class);
    }
}
