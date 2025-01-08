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
import walkingkooka.collect.Range;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.header.CharsetName;
import walkingkooka.net.header.ContentRange;
import walkingkooka.net.header.ETag;
import walkingkooka.net.header.ETagValidator;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.IfRange;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.header.MediaTypeBoundary;
import walkingkooka.net.header.RangeHeader;
import walkingkooka.net.header.RangeHeaderUnit;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpStatusCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class RangeAwareHttpResponseTest extends BufferingHttpResponseTestCase<RangeAwareHttpResponse> {

    private final static Supplier<Byte> BOUNDARY_CHARACTERS = new Supplier<>() {
        @Override
        public Byte get() {
            return (byte) "0123456789".charAt(this.i++ % 10);
        }

        int i = 0;
    };

    private final static ETag ETAG_ABSENT = null;
    private final static IfRange<?> IF_RANGE_ABSENT = null;
    private final static LocalDateTime LASTMODIFIED_ABSENT = null;

    private final static byte[] BODY = "abcdefghijklmnopqrstuvwxyz".getBytes(CharsetName.UTF_8.charset().get());
    private final static long BODY_LENGTH = BODY.length;
    private final static byte[] NO_BODY = new byte[0];

    @Test
    public void testWithNullRequestFails() {
        assertThrows(NullPointerException.class, () -> RangeAwareHttpResponse.with(null,
            HttpResponses.fake(),
            BOUNDARY_CHARACTERS));
    }

    @Test
    public void testWithNullBoundaryCharactersFails() {
        assertThrows(NullPointerException.class, () -> RangeAwareHttpResponse.with(HttpRequests.fake(),
            HttpResponses.fake(),
            null));
    }

    @Test
    public void testRangeAbsentNotWrapped() {
        final HttpResponse response = HttpResponses.fake();
        assertSame(response,
            RangeAwareHttpResponse.with(createRequest(null, ifRange()),
                response,
                BOUNDARY_CHARACTERS));
    }

    // wrapped...

    @Test
    public void testResponseInformation1XX() {
        this.setStatusAddEntityAndCheck(HttpStatusCode.CONTINUE);
    }

    @Test
    public void testResponseRedirect3XX() {
        this.setStatusAddEntityAndCheck(HttpStatusCode.TEMPORARY_REDIRECT);
    }

    @Test
    public void testResponseBadRequest4XX() {
        this.setStatusAddEntityAndCheck(HttpStatusCode.TEMPORARY_REDIRECT);
    }

    @Test
    public void testResponseServerFailure5XX() {
        this.setStatusAddEntityAndCheck(HttpStatusCode.INTERNAL_SERVER_ERROR);
    }

    private void setStatusAddEntityAndCheck(final HttpStatusCode status) {
        for (final HttpProtocolVersion version : HttpProtocolVersion.values()) {
            this.setVersionStatusAddEntityAndCheck(version, status.status(), this.httpEntity());
        }
    }

    @Test
    public void testIfRangeETagRequiredAndAbsent() {
        this.ifRangeFailSetStatusAddEntityAndCheck(
            this.ranges(),
            IfRange.with(this.etag()),
            ETAG_ABSENT,
            this.lastModified());
    }

    @Test
    public void testIfRangeETagRequiredAndIncorrect() {
        this.ifRangeFailSetStatusAddEntityAndCheck(
            this.ranges(),
            IfRange.with(this.etag()),
            this.etagDifferent(),
            this.lastModified());
    }

    @Test
    public void testIfRangeLastModifiedRequiredAndAbsent() {
        this.ifRangeFailSetStatusAddEntityAndCheck(
            this.ranges(),
            IfRange.with(this.lastModified()),
            this.etag(),
            null);
    }

    @Test
    public void testIfRangeLastModifiedRequiredAndIncorrect() {
        this.ifRangeFailSetStatusAddEntityAndCheck(
            this.ranges(),
            IfRange.with(this.lastModified()),
            this.etag(),
            this.lastModifiedDifferent());
    }

    private void ifRangeFailSetStatusAddEntityAndCheck(final RangeHeader requestRanges,
                                                       final IfRange<?> requestIfRange,
                                                       final ETag responseETag,
                                                       final LocalDateTime responseLastModified) {

        this.setStatusAddEntityAndCheck4(requestRanges,
            requestIfRange,
            responseETag,
            responseLastModified,
            BODY,
            HttpStatusCode.OK,
            BODY);
    }

    // range unsatisfiable.....................................

    @Test
    public void testRangeUnsatisfiable() {
        this.invalidRangeSetStatusAddEntityAndCheck(IF_RANGE_ABSENT,
            ETAG_ABSENT,
            this.lastModified());
    }

    @Test
    public void testIfRangeETagAndRangeUnsatisfiable() {
        this.invalidRangeSetStatusAddEntityAndCheck(IF_RANGE_ABSENT,
            this.etag(),
            LASTMODIFIED_ABSENT);
    }

    @Test
    public void testIfRangeLastModifiedAndRangeUnsatisfiable() {
        this.invalidRangeSetStatusAddEntityAndCheck(IF_RANGE_ABSENT,
            ETAG_ABSENT,
            this.lastModified());
    }

    private void invalidRangeSetStatusAddEntityAndCheck(final IfRange<?> requestIfRange,
                                                        final ETag responseETag,
                                                        final LocalDateTime responseLastModified) {
        this.setStatusAddEntityAndCheck4(RangeHeader.parse("bytes=1-9999"),
            requestIfRange,
            responseETag,
            responseLastModified,
            BODY,
            HttpStatusCode.REQUESTED_RANGE_NOT_SATISFIABLE,
            NO_BODY);
    }

    private void setStatusAddEntityAndCheck4(final RangeHeader requestRanges,
                                             final IfRange<?> requestIfRange,
                                             final ETag responseETag,
                                             final LocalDateTime responseLastModified,
                                             final byte[] body,
                                             final HttpStatusCode expectedStatus,
                                             final byte[] expectedBody) {

        final Map<HttpHeaderName<?>, List<?>> headers = Maps.ordered();
        if (null != responseETag) {
            headers.put(HttpHeaderName.E_TAG, list(responseETag));
        }
        if (null != responseLastModified) {
            headers.put(HttpHeaderName.LAST_MODIFIED, list(responseLastModified));
        }
        headers.put(HttpHeaderName.SERVER, list("Server123"));

        for (final HttpProtocolVersion version : HttpProtocolVersion.values()) {
            this.setVersionStatusAddEntityAndCheck(
                this.createRequest(requestRanges, requestIfRange),
                version,
                HttpStatusCode.OK.status(),
                httpEntity(headers).setBody(Binary.with(body)),
                version,
                expectedStatus.status(),
                httpEntity(headers).setBody(Binary.with(expectedBody)));
        }
    }

    // range ............................................................................................

    @Test
    public void testRangeBeginning() {
        this.rangeAndCheck("bytes=0-2", 0, 2, "abc");
    }

    @Test
    public void testRangeMiddle() {
        this.rangeAndCheck("bytes=1-3", 1, 3, "bcd");
    }

    @Test
    public void testRangeEnd() {
        this.rangeAndCheck("bytes=22-25", 22, 25, "wxyz");
    }

    @Test
    public void testRangeOpenEnd() {
        this.rangeAndCheck("bytes=22-", 22, 25, "wxyz");
    }

    private void rangeAndCheck(final String requestRange, final int lower, final int upper, final String multipart2Body) {
        final Map<HttpHeaderName<?>, List<?>> headers = Maps.ordered();
        headers.put(HttpHeaderName.SERVER, list("Server123"));
        headers.put(HttpHeaderName.E_TAG, list(this.etag()));
        headers.put(HttpHeaderName.LAST_MODIFIED, list(this.lastModified()));
        headers.put(HttpHeaderName.CONTENT_TYPE, list(MediaType.TEXT_PLAIN));
        headers.put(HttpHeaderName.CONTENT_LENGTH, list(BODY_LENGTH));

        final HttpEntity multipart1 = HttpEntity.EMPTY
            .addHeader(HttpHeaderName.SERVER, "Server123")
            .addHeader(HttpHeaderName.E_TAG, this.etag())
            .addHeader(HttpHeaderName.LAST_MODIFIED, this.lastModified())
            .addHeader(HttpHeaderName.CONTENT_TYPE, this.multipartContentType())
            .addHeader(HttpHeaderName.CONTENT_LENGTH, BODY_LENGTH);

        final HttpEntity multipart2 = HttpEntity.EMPTY
            .addHeader(HttpHeaderName.CONTENT_TYPE, MediaType.TEXT_PLAIN)
            .addHeader(HttpHeaderName.CONTENT_RANGE, this.contentRange(lower, upper))
            .setBodyText(multipart2Body);

        this.setHeaderAddEntityAndCheck(requestRange,
            IF_RANGE_ABSENT,
            headers,
            multipart1,
            multipart2);
    }

    @Test
    public void testRangeTwoParts() {
        final Map<HttpHeaderName<?>, List<?>> headers = Maps.ordered();
        headers.put(HttpHeaderName.SERVER, list("Server123"));
        headers.put(HttpHeaderName.E_TAG, list(this.etag()));
        headers.put(HttpHeaderName.LAST_MODIFIED, list(this.lastModified()));
        headers.put(HttpHeaderName.CONTENT_TYPE, list(MediaType.TEXT_PLAIN));
        headers.put(HttpHeaderName.CONTENT_LENGTH, list(BODY_LENGTH));

        final HttpEntity multipart1 = HttpEntity.EMPTY
            .addHeader(HttpHeaderName.SERVER, "Server123")
            .addHeader(HttpHeaderName.E_TAG, this.etag())
            .addHeader(HttpHeaderName.LAST_MODIFIED, this.lastModified())
            .addHeader(HttpHeaderName.CONTENT_TYPE, this.multipartContentType())
            .addHeader(HttpHeaderName.CONTENT_LENGTH, BODY_LENGTH);

        final HttpEntity multipart2 = HttpEntity.EMPTY
            .addHeader(HttpHeaderName.CONTENT_TYPE, MediaType.TEXT_PLAIN)
            .addHeader(HttpHeaderName.CONTENT_RANGE, this.contentRange(1, 2))
            .setBodyText("bc");

        final HttpEntity multipart3 = HttpEntity.EMPTY
            .addHeader(HttpHeaderName.CONTENT_TYPE, MediaType.TEXT_PLAIN)
            .addHeader(HttpHeaderName.CONTENT_RANGE, this.contentRange(10, 12))
            .setBodyText("klm");

        this.setHeaderAddEntityAndCheck("bytes=1-2,10-12",
            IF_RANGE_ABSENT,
            headers,
            multipart1,
            multipart2,
            multipart3);
    }

    private ContentRange contentRange(final long lower, final long upper) {
        return ContentRange.with(RangeHeaderUnit.BYTES,
            Optional.of(Range.greaterThanEquals(lower).and(Range.lessThanEquals(upper))),
            Optional.of(BODY_LENGTH));
    }

    private void setHeaderAddEntityAndCheck(final String requestRanges,
                                            final IfRange<?> requestIfRange,
                                            final Map<HttpHeaderName<?>, List<?>> headers,
                                            final HttpEntity... expectedEntities) {
        for (final HttpProtocolVersion version : HttpProtocolVersion.values()) {
            this.setVersionStatusAddEntityAndCheck(
                this.createRequest(RangeHeader.parse(requestRanges), requestIfRange),
                version,
                HttpStatusCode.OK.status(),
                httpEntity(headers).setBody(Binary.with(BODY)),
                version,
                HttpStatusCode.PARTIAL_CONTENT.status(),
                expectedEntities);
        }
    }

    // helpers ............................................................................................

    @Override
    RangeAwareHttpResponse createResponse(final HttpResponse response) {
        return this.createResponse(this.createRequest(this.ranges(), this.ifRange()), response);
    }

    @Override
    RangeAwareHttpResponse createResponse(final HttpRequest request, final HttpResponse response) {
        return Cast.to(RangeAwareHttpResponse.with(request, response, BOUNDARY_CHARACTERS));
    }

    @Override
    HttpRequest createRequest() {
        return this.createRequest(this.ranges(), this.ifRange());
    }

    private RangeHeader ranges() {
        return RangeHeader.parse("bytes=1-100");
    }

    private IfRange<?> ifRange() {
        return IfRange.with(this.etag());
    }

    private ETag etag() {
        return ETagValidator.STRONG.setValue("etag-correct");
    }

    private ETag etagDifferent() {
        return ETagValidator.STRONG.setValue("etag-incorrect");
    }

    private LocalDateTime lastModified() {
        return LocalDateTime.of(2000, 12, 31, 6, 28, 29);
    }

    private LocalDateTime lastModifiedDifferent() {
        return LocalDateTime.of(2001, 1, 1, 1, 1, 1);
    }

    private MediaType multipartContentType() {
        return MediaTypeBoundary.with("mnopqrstuvmnopqrstuvmnopqrstuvmnopqrstuv").multipartByteRanges();
    }

    private HttpEntity httpEntity() {
        return HttpEntity.EMPTY.addHeader(HttpHeaderName.SERVER, "server abc123").setBody(Binary.with(BODY));
    }

    private HttpRequest createRequest(final RangeHeader ranges, final IfRange<?> ifRange) {
        final Map<HttpHeaderName<?>, List<?>> headers = Maps.ordered();

        if (null != ranges) {
            headers.put(HttpHeaderName.RANGE, list(ranges));
        }

        if (null != ifRange) {
            headers.put(HttpHeaderName.IF_RANGE, list(ifRange));
        }

        return new FakeHttpRequest() {
            @Override
            public Map<HttpHeaderName<?>, List<?>> headers() {
                return Maps.readOnly(headers);
            }

            @Override
            public String toString() {
                return this.headers().toString();
            }
        };
    }

    @Override
    public Class<RangeAwareHttpResponse> type() {
        return RangeAwareHttpResponse.class;
    }
}
