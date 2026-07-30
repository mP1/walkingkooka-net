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
import walkingkooka.net.Url;
import walkingkooka.net.header.ETag;
import walkingkooka.net.header.ETagComputerTesting;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;

import java.util.Objects;
import java.util.Optional;

public final class HttpHandlerWrapperSharedETagComputerTest extends HttpHandlerWrapperSharedTestCase<HttpHandlerWrapperSharedETagComputer<FakeHttpHandlerContext>, FakeHttpHandlerContext>
    implements
    ETagComputerTesting,
    HasCharsetTesting,
    ToStringTesting<HttpHandlerWrapperSharedETagComputer<FakeHttpHandlerContext>> {

    private final static HttpHandler<FakeHttpHandlerContext> HTTP_HANDLER = new HttpHandler<>() {
        @Override
        public void handle(final HttpRequest request,
                           final HttpResponse response,
                           final FakeHttpHandlerContext context) {
            Objects.requireNonNull(request, "request");
            Objects.requireNonNull(response, "response");
            Objects.requireNonNull(context, "context");

            response.setStatus(HttpStatusCode.CREATED.status());
            response.setEntity(HttpEntity.EMPTY);
        }

        @Override
        public String toString() {
            return "HTTP_HANDLER";
        }
    };

    private final static HttpRequest HTTP_REQUEST = new FakeHttpRequest();

    // handle...........................................................................................................

    @Test
    public void testHandleResponseOkWithoutBody() {
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
            HTTP_REQUEST,
            expected
        );
    }

    @Test
    public void testHandleResponseOkWithBodyAndETag() {
        final HttpStatus httpStatus = HttpStatusCode.OK.status();
        final HttpEntity responseBody = HttpEntity.EMPTY.setContentType(MediaType.TEXT_PLAIN)
            .addHeader(
                HttpHeaderName.E_TAG,
                ETag.strong("111")
            ).setBodyText(
                "BodyText111"
            );

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(httpStatus);
        expected.setEntity(responseBody);

        this.handleAndCheck(
            this.createHttpHandler(
                httpStatus,
                responseBody
            ),
            HTTP_REQUEST,
            expected
        );
    }

    @Test
    public void testHandleResponseOkWithBody() {
        final HttpStatus httpStatus = HttpStatusCode.OK.status();

        final Binary binary = Binary.with(
            "Hello".getBytes(CHARSET)
        );

        final HttpEntity responseBody = HttpEntity.EMPTY.setContentType(MediaType.TEXT_PLAIN)
            .setBody(binary);

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(httpStatus);
        expected.setEntity(
            responseBody.addHeader(
                HttpHeaderName.E_TAG,
                ETAG_COMPUTER.computeETag(binary)
                    .get()
            )
        );

        this.handleAndCheck(
            this.createHttpHandler(
                httpStatus,
                responseBody
            ),
            HTTP_REQUEST,
            expected
        );
    }

    @Test
    public void testHandleResponseCreatedWithBody() {
        final HttpStatus httpStatus = HttpStatusCode.CREATED.status();

        final Binary binary = Binary.with(
            "Hello".getBytes(CHARSET)
        );

        final HttpEntity responseBody = HttpEntity.EMPTY.setContentType(MediaType.TEXT_PLAIN)
            .setBody(binary)
            .setContentLength();

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(httpStatus);
        expected.setEntity(
            responseBody.addHeader(
                HttpHeaderName.E_TAG,
                ETAG_COMPUTER.computeETag(binary)
                    .get()
            )
        );

        this.handleAndCheck(
            this.createHttpHandler(
                httpStatus,
                responseBody
            ),
            HTTP_REQUEST,
            expected
        );
    }

    @Test
    public void testHandleResponseMoveTemporarily() {
        final HttpStatus httpStatus = HttpStatusCode.MOVED_TEMPORARILY.status();
        final HttpEntity responseBody = HttpEntity.EMPTY.addHeader(
            HttpHeaderName.LOCATION,
            Url.parseAbsolute("https://example.com/redirect")
        );

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(httpStatus);
        expected.setEntity(responseBody);

        this.handleAndCheck(
            this.createHttpHandler(
                httpStatus,
                responseBody
            ),
            HTTP_REQUEST,
            expected
        );
    }

    @Test
    public void testHandleResponseBadRequestWithBody() {
        final HttpStatus httpStatus = HttpStatusCode.BAD_REQUEST.status();
        final HttpEntity responseBody = HttpEntity.EMPTY.setContentType(
            MediaType.TEXT_PLAIN
        ).setBodyText("ResponseBodyText123");

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(httpStatus);
        expected.setEntity(responseBody);

        this.handleAndCheck(
            this.createHttpHandler(
                httpStatus,
                responseBody
            ),
            HTTP_REQUEST,
            expected
        );
    }

    private HttpHandlerWrapperSharedETagComputer<FakeHttpHandlerContext> createHttpHandler(final HttpStatus httpStatus,
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
    HttpHandlerWrapperSharedETagComputer<FakeHttpHandlerContext> createHttpHandler(final HttpHandler<FakeHttpHandlerContext> httpHandler) {
        return HttpHandlerWrapperSharedETagComputer.with(httpHandler);
    }

    @Override
    public FakeHttpHandlerContext createContext() {
        return new FakeHttpHandlerContext() {

            @Override
            public Optional<ETag> computeETag(final Binary binary) {
                return ETAG_COMPUTER.computeETag(binary);
            }
        };
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createHttpHandler(HTTP_HANDLER),
            HTTP_HANDLER.toString()
        );
    }

    // class............................................................................................................

    @Override
    public Class<HttpHandlerWrapperSharedETagComputer<FakeHttpHandlerContext>> type() {
        return Cast.to(HttpHandlerWrapperSharedETagComputer.class);
    }
}
