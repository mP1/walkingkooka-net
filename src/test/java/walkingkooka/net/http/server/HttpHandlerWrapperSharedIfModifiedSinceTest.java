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
import walkingkooka.HasCharsetTesting;
import walkingkooka.ToStringTesting;
import walkingkooka.datetime.HasNowTesting;
import walkingkooka.net.header.ETagComputerTesting;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.net.http.HttpTransport;

public final class HttpHandlerWrapperSharedIfModifiedSinceTest extends HttpHandlerWrapperSharedTestCase<HttpHandlerWrapperSharedIfModifiedSince<FakeHttpHandlerContext>, FakeHttpHandlerContext>
    implements ETagComputerTesting,
    HasCharsetTesting,
    HasNowTesting,
    ToStringTesting<HttpHandlerWrapperSharedIfModifiedSince<FakeHttpHandlerContext>> {

    private final static HttpStatus STATUS = HttpStatusCode.OK.setMessage("OK!");
    private final static HttpEntity ENTITY = HttpEntity.EMPTY.setBodyText("Success123");

    private final static HttpHandler<FakeHttpHandlerContext> HANDLER = new HttpHandler<>() {
        @Override
        public void handle(final HttpRequest request,
                           final HttpResponse response,
                           final FakeHttpHandlerContext context) {
            response.setStatus(STATUS);
            response.setEntity(ENTITY);
        }

        @Override
        public String toString() {
            return "HANDLER";
        }
    };

    // handle...........................................................................................................

    @Test
    public void testHandleDeleteRequest() {
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
                "DELETE /file1.txt HTTP/1.0\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Length: 5\r\n" +
                    "\r\n" +
                    "Hello"
            ),
            expected
        );
    }

    @Test
    public void testHandlePostRequest() {
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
                "POST /file1.txt HTTP/1.0\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Length: 5\r\n" +
                    "\r\n" +
                    "Hello"
            ),
            expected
        );
    }

    @Test
    public void testHandlePostRequestIfModifiedSince() {
        final HttpStatus httpStatus = HttpStatusCode.OK.status();
        final HttpEntity responseBody = HttpEntity.EMPTY.setContentType(MediaType.TEXT_PLAIN)
            .setBodyText("ResponseText123")
            .addHeader(
                HttpHeaderName.LAST_MODIFIED,
                NOW.minusYears(1)
            ).setContentLength();

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
                "POST /file1.txt HTTP/1.0\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Length: 5\r\n" +
                    "If-Modified-Since: Fri, 31 Dec 1999 12:58:59 GMT\r\n" +
                    "\r\n" +
                    "Hello"
            ),
            expected
        );
    }

    @Test
    public void testHandleGetRequestIfModifiedSinceMissing() {
        final HttpStatus httpStatus = HttpStatusCode.OK.status();
        final HttpEntity responseBody = HttpEntity.EMPTY.setContentType(MediaType.TEXT_PLAIN)
            .setBodyText("ResponseText123")
            .setContentLength();

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
                "GET /file1.txt HTTP/1.0\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Length: 5\r\n" +
                    "If-Modified-Since: Fri, 31 Dec 1999 12:58:59 GMT\r\n" +
                    "\r\n" +
                    "Hello"
            ),
            expected
        );
    }

    @Test
    public void testHandleGetRequestIfModifiedSinceResponseAfter() {
        final HttpStatus httpStatus = HttpStatusCode.OK.status();
        final HttpEntity responseBody = HttpEntity.EMPTY.setContentType(MediaType.TEXT_PLAIN)
            .setBodyText("ResponseText123")
            .addHeader(
                HttpHeaderName.LAST_MODIFIED,
                NOW.plusYears(1)
            ).setContentLength();

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.NOT_MODIFIED.status());
        expected.clearEntity();

        this.handleAndCheck(
            this.createHttpHandler(
                httpStatus,
                responseBody
            ),
            HttpRequests.parse(
                HttpTransport.UNSECURED,
                "GET /file1.txt HTTP/1.0\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Length: 5\r\n" +
                    "If-Modified-Since: Thu, 31 Dec 1998 12:58:59 GMT\r\n" +
                    "\r\n" +
                    "Hello"
            ),
            expected
        );
    }

    @Test
    public void testHandleGetRequestIfModifiedSinceResponseSame() {
        final HttpStatus httpStatus = HttpStatusCode.OK.status();
        final HttpEntity responseBody = HttpEntity.EMPTY.setContentType(MediaType.TEXT_PLAIN)
            .setBodyText("ResponseText123")
            .addHeader(
                HttpHeaderName.LAST_MODIFIED,
                NOW
            ).setContentLength();

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.NOT_MODIFIED.status());
        expected.clearEntity();

        this.handleAndCheck(
            this.createHttpHandler(
                httpStatus,
                responseBody
            ),
            HttpRequests.parse(
                HttpTransport.UNSECURED,
                "GET /file1.txt HTTP/1.0\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Length: 5\r\n" +
                    "If-Modified-Since: Fri, 31 Dec 1999 12:58:59 GMT\r\n" +
                    "\r\n" +
                    "Hello"
            ),
            expected
        );
    }

    @Test
    public void testHandleGetRequestIfModifiedSinceResponseBefore() {
        final HttpStatus httpStatus = HttpStatusCode.OK.status();
        final HttpEntity responseBody = HttpEntity.EMPTY.setContentType(MediaType.TEXT_PLAIN)
            .setBodyText("ResponseText123")
            .addHeader(
                HttpHeaderName.LAST_MODIFIED,
                NOW
            ).setContentLength();

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.NOT_MODIFIED.status());
        expected.clearEntity();

        this.handleAndCheck(
            this.createHttpHandler(
                httpStatus,
                responseBody
            ),
            HttpRequests.parse(
                HttpTransport.UNSECURED,
                "GET /file1.txt HTTP/1.0\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Length: 5\r\n" +
                    "If-Modified-Since: Thu, 31 Dec 1998 12:58:59 GMT\r\n" +
                    "\r\n" +
                    "Hello"
            ),
            expected
        );
    }

    private HttpHandlerWrapperSharedIfModifiedSince<FakeHttpHandlerContext> createHttpHandler(final HttpStatus httpStatus,
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
    HttpHandlerWrapperSharedIfModifiedSince<FakeHttpHandlerContext> createHttpHandler(final HttpHandler<FakeHttpHandlerContext> httpHandler) {
        return HttpHandlerWrapperSharedIfModifiedSince.with(httpHandler);
    }

    @Override
    public FakeHttpHandlerContext createContext() {
        return new FakeHttpHandlerContext();
    }


    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createHttpHandler(HANDLER),
            "If-Modified-Since " + HANDLER
        );
    }

    // class............................................................................................................

    @Override
    public Class<HttpHandlerWrapperSharedIfModifiedSince<FakeHttpHandlerContext>> type() {
        return Cast.to(HttpHandlerWrapperSharedIfModifiedSince.class);
    }
}
