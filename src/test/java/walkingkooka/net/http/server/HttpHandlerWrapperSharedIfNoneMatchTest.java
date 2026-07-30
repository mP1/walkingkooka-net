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
import walkingkooka.net.header.ETagList;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.net.http.HttpTransport;

import java.util.Optional;

public final class HttpHandlerWrapperSharedIfNoneMatchTest extends HttpHandlerWrapperSharedTestCase<HttpHandlerWrapperSharedIfNoneMatch<FakeHttpHandlerContext>, FakeHttpHandlerContext>
    implements ETagComputerTesting,
    HasCharsetTesting,
    ToStringTesting<HttpHandlerWrapperSharedIfNoneMatch<FakeHttpHandlerContext>> {

    private final static HttpMethod METHOD = HttpMethod.PATCH;
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

    private final static ETag STRONG_ETAG = ETag.strong("Strong111");

    private final static ETag DIFFERENT_STRONG_ETAG = ETag.strong("DifferentStrong111");

    // handle...........................................................................................................

    @Test
    public void testHandleRequestMissingIfNoneMatchResponseOkWithoutBody() {
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
            this.request(),
            expected
        );
    }

    @Test
    public void testHandleRequestWithStrongIfNoneMatchResponseOkWithoutBody() {
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
            this.request(STRONG_ETAG),
            expected
        );
    }

    @Test
    public void testHandleRequestWithStrongIfNoneMatchResponseWithDifferentETag() {
        final HttpStatus httpStatus = HttpStatusCode.OK.status();
        final HttpEntity responseBody = HttpEntity.EMPTY.setContentType(MediaType.TEXT_PLAIN)
            .addHeader(
                HttpHeaderName.E_TAG,
                DIFFERENT_STRONG_ETAG
            ).setBodyText("ResponseBodyText123")
            .setContentLength();

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.OK.status());
        expected.setEntity(responseBody);

        this.handleAndCheck(
            this.createHttpHandler(
                httpStatus,
                responseBody
            ),
            this.request(STRONG_ETAG),
            expected
        );
    }

    @Test
    public void testHandleRequestWithStrongIfNoneMatchResponseOkWithSameETag() {
        final HttpStatus httpStatus = HttpStatusCode.OK.status();
        final HttpEntity responseBody = HttpEntity.EMPTY.setContentType(MediaType.TEXT_PLAIN);

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.NOT_MODIFIED.status());
        expected.setEntity(HttpEntity.EMPTY);

        this.handleAndCheck(
            this.createHttpHandler(
                httpStatus,
                responseBody.addHeader(
                    HttpHeaderName.E_TAG,
                    STRONG_ETAG
                ).setBodyText(
                    "Response111"
                )
            ),
            this.request(STRONG_ETAG),
            expected
        );
    }

    @Test
    public void testHandleRequestWithStrongIfNoneMatchResponseCreatedWithSameETag() {
        final HttpStatus httpStatus = HttpStatusCode.CREATED.status();
        final HttpEntity responseBody = HttpEntity.EMPTY.setContentType(MediaType.TEXT_PLAIN);

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.NOT_MODIFIED.status());
        expected.setEntity(HttpEntity.EMPTY);

        this.handleAndCheck(
            this.createHttpHandler(
                httpStatus,
                responseBody.addHeader(
                    HttpHeaderName.E_TAG,
                    STRONG_ETAG
                ).setBodyText(
                    "Response111"
                )
            ),
            this.request(STRONG_ETAG),
            expected
        );
    }

    @Test
    public void testHandleRequestWithWildcardIfNoneMatchResponseWithDifferentETag() {
        final HttpStatus httpStatus = HttpStatusCode.OK.status();
        final HttpEntity responseBody = HttpEntity.EMPTY.setContentType(MediaType.TEXT_PLAIN)
            .addHeader(
                HttpHeaderName.E_TAG,
                DIFFERENT_STRONG_ETAG
            ).setBodyText("ResponseBodyText123")
            .setContentLength();

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(HttpStatusCode.NOT_MODIFIED.status());
        expected.setEntity(HttpEntity.EMPTY);

        this.handleAndCheck(
            this.createHttpHandler(
                httpStatus,
                responseBody
            ),
            this.request(ETag.wildcard()),
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
            this.request(),
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
            this.request(),
            expected
        );
    }

    private HttpHandlerWrapperSharedIfNoneMatch<FakeHttpHandlerContext> createHttpHandler(final HttpStatus httpStatus,
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
    HttpHandlerWrapperSharedIfNoneMatch<FakeHttpHandlerContext> createHttpHandler(final HttpHandler<FakeHttpHandlerContext> httpHandler) {
        return HttpHandlerWrapperSharedIfNoneMatch.with(httpHandler);
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

    private HttpRequest request() {
        return this.request(
            HttpEntity.EMPTY
        );
    }

    private HttpRequest request(final ETag ifNoneMatch) {
        return this.request(
            HttpEntity.EMPTY.addHeader(
                HttpHeaderName.IF_NONE_MATCHED,
                ETagList.EMPTY.concat(ifNoneMatch)
            )
        );
    }

    private HttpRequest request(final HttpEntity httpEntity) {
        return HttpRequests.get(
            HttpTransport.UNSECURED,
            Url.parseRelative("/"),
            HttpProtocolVersion.VERSION_1_0,
            httpEntity
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createHttpHandler(HANDLER),
            "If-None-Match " + HANDLER
        );
    }

    // class............................................................................................................

    @Override
    public Class<HttpHandlerWrapperSharedIfNoneMatch<FakeHttpHandlerContext>> type() {
        return Cast.to(HttpHandlerWrapperSharedIfNoneMatch.class);
    }
}
