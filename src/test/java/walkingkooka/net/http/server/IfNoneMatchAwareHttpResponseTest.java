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
import walkingkooka.collect.map.Maps;
import walkingkooka.net.header.ETag;
import walkingkooka.net.header.ETagValidator;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpStatusCode;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class IfNoneMatchAwareHttpResponseTest extends BufferingHttpResponseTestCase<IfNoneMatchAwareHttpResponse> {

    private final static List<ETag> IF_NONE_MATCH_ABSENT = null;
    private final static Function<byte[], ETag> COMPUTER = (b) -> ETagValidator.STRONG.setValue("X" + b.length);

    private final static byte[] BODY = new byte[]{1, 2, 3, 4};

    private final static String SERVER = "Server 123";
    private final static ETag E_TAG_ABSENT = null;

    // test.........................................................................................

    @Test
    public void testWithRequestNullFails() {
        assertThrows(NullPointerException.class, () -> LastModifiedAwareHttpResponse.with(null,
            HttpResponses.fake()));
    }

    @Test
    public void testWithDelete() {
        this.withAndNotWrappedCheck(HttpMethod.DELETE,
            ifNoneMatch());
    }

    @Test
    public void testWithPost() {
        this.withAndNotWrappedCheck(HttpMethod.POST,
            ifNoneMatch());
    }

    @Test
    public void testWithPut() {
        this.withAndNotWrappedCheck(HttpMethod.PUT,
            ifNoneMatch());
    }

    @Test
    public void testWithIfNoneMatchAbsent() {
        this.withAndNotWrappedCheck(HttpMethod.GET,
            IF_NONE_MATCH_ABSENT);
    }

    private void withAndNotWrappedCheck(final HttpMethod method,
                                        final List<ETag> ifNoneMatch) {
        final HttpResponse response = HttpResponses.fake();
        assertSame(response,
            this.createResponseWithoutCast(method,
                ifNoneMatch,
                response),
            "method=" + method + " should have resulted in the response not being wrapped");
    }

    // server response body status code.........................................................................

    @Test
    public void testStatusContinueBody() {
        this.statusBodyAndCheck(HttpStatusCode.CONTINUE);
    }

    @Test
    public void testStatusRedirectBody() {
        this.statusBodyAndCheck(HttpStatusCode.TEMPORARY_REDIRECT);
    }

    @Test
    public void testStatusClientErrorBody() {
        this.statusBodyAndCheck(HttpStatusCode.BAD_REQUEST);
    }

    @Test
    public void testStatusServerErrorBody() {
        this.statusBodyAndCheck(HttpStatusCode.SERVICE_UNAVAILABLE);
    }

    private void statusBodyAndCheck(final HttpStatusCode status) {
        this.setVersionStatusAddEntityAndCheck(HttpProtocolVersion.VERSION_2,
            status,
            this.headersWithContentHeaders(E_TAG_ABSENT),
            BODY);
    }

    // incorrect etag.............................................................

    @Test
    public void testStatusOkIfNoneMatchFail() {
        this.setStatusOkAndAddEntityAndCheck(E_TAG_ABSENT,
            this.etag(9),
            new byte[9]);
    }

    @Test
    public void testStatusOkComputedETagFailMatchesWeak() {
        this.setStatusOkAndAddEntityAndCheck(E_TAG_ABSENT,
            this.etag(1),
            new byte[1]);
    }

    @Test
    public void testStatusOkETagIfNoneMatchFail() {
        final ETag etag = this.etag(9);
        this.setStatusOkAndAddEntityAndCheck(etag,
            etag,
            new byte[9]);
    }

    private void setStatusOkAndAddEntityAndCheck(final ETag etag,
                                                 final ETag expectedETag,
                                                 final byte[] body) {
        for (final HttpProtocolVersion version : HttpProtocolVersion.values()) {
            this.setVersionStatusAddEntityAndCheck(version,
                HttpStatusCode.OK,
                this.headersWithContentHeaders(etag),
                body,
                version,
                HttpStatusCode.OK,
                this.headersWithContentHeaders(expectedETag),
                body);
        }
    }

    // response  ...............................................................

    @Test
    public void testStatusOkEtagPrecomputedIfNoneMatchMatched() {
        final ETag etag = this.etag(2);
        this.setBodyAndNotModifiedCheck(etag,
            etag,
            new byte[2]);
    }

    @Test
    public void testStatusOkEtagIfNoneMatchMatched() {
        this.setBodyAndNotModifiedCheck(E_TAG_ABSENT,
            this.etag(2),
            new byte[2]);
    }

    @Test
    public void testStatusOkEtagIfNoneMatchMatched2() {
        this.setBodyAndNotModifiedCheck(E_TAG_ABSENT,
            this.etag(3),
            new byte[3]);
    }

    private void setBodyAndNotModifiedCheck(final ETag etag,
                                            final ETag expectedETag,
                                            final byte[] body) {
        for (final HttpProtocolVersion version : HttpProtocolVersion.values()) {
            this.setVersionStatusAddEntityAndCheck(version,
                HttpStatusCode.OK,
                this.headersWithContentHeaders(etag),
                body,
                version,
                HttpStatusCode.NOT_MODIFIED,
                this.headers(expectedETag),
                body);
        }
    }

    // helpers.........................................................................................

    private Map<HttpHeaderName<?>, List<?>> headers(final ETag etag) {
        final Map<HttpHeaderName<?>, List<?>> headers = Maps.ordered();
        headers.put(HttpHeaderName.SERVER, list(SERVER));
        if (null != etag) {
            headers.put(HttpHeaderName.E_TAG, list(etag));
        }
        return headers;
    }

    private Map<HttpHeaderName<?>, List<?>> headersWithContentHeaders(final ETag etag) {
        final Map<HttpHeaderName<?>, List<?>> headers = this.headers(etag);
        headers.put(HttpHeaderName.CONTENT_TYPE, list(MediaType.ANY_TEXT));
        headers.put(HttpHeaderName.CONTENT_LENGTH, list(123L));
        return headers;
    }

    private void setVersionStatusAddEntityAndCheck(final HttpProtocolVersion version,
                                                   final HttpStatusCode status,
                                                   final Map<HttpHeaderName<?>, List<?>> headers,
                                                   final byte[] body) {
        this.setVersionStatusAddEntityAndCheck(version, status, headers, body, version, status, headers, body);
    }

    private void setVersionStatusAddEntityAndCheck(final HttpProtocolVersion version,
                                                   final HttpStatusCode status,
                                                   final Map<HttpHeaderName<?>, List<?>> headers,
                                                   final byte[] body,
                                                   final HttpProtocolVersion expectedVersion,
                                                   final HttpStatusCode expectedStatus,
                                                   final Map<HttpHeaderName<?>, List<?>> expectedHeaders,
                                                   final byte[] expectedBody) {
        this.setVersionStatusAddEntityAndCheck(
            this.createRequest(),
            version,
            status.status(),
            httpEntity(headers).setBody(Binary.with(body)),
            expectedVersion,
            expectedStatus.status(),
            httpEntity(expectedHeaders).setBody(Binary.with(expectedBody)));
    }

    @Override
    IfNoneMatchAwareHttpResponse createResponse(final HttpResponse response) {
        return this.createResponse(HttpMethod.GET,
            this.ifNoneMatch(),
            response);
    }

    private IfNoneMatchAwareHttpResponse createResponse(final HttpMethod method,
                                                        final List<ETag> ifNoneMatch,
                                                        final HttpResponse response) {
        return Cast.to(this.createResponse(
            createRequest(method, ifNoneMatch),
            response));
    }

    @Override
    IfNoneMatchAwareHttpResponse createResponse(final HttpRequest request,
                                                final HttpResponse response) {
        return Cast.to(IfNoneMatchAwareHttpResponse.with(request, response, COMPUTER));
    }

    private HttpResponse createResponseWithoutCast(final HttpMethod method,
                                                   final List<ETag> ifNoneMatch,
                                                   final HttpResponse response) {
        return IfNoneMatchAwareHttpResponse.with(
            createRequest(method, ifNoneMatch),
            response,
            COMPUTER);
    }

    @Override
    HttpRequest createRequest() {
        return createRequest(HttpMethod.GET, this.ifNoneMatch());
    }

    private HttpRequest createRequest(final HttpMethod method,
                                      final List<ETag> ifNoneMatch) {
        Objects.requireNonNull(method, "method");

        final Map<HttpHeaderName<?>, List<?>> headers = Maps.ordered();
        if (null != ifNoneMatch) {
            headers.put(HttpHeaderName.IF_NONE_MATCHED, list(ifNoneMatch));
        }

        return new FakeHttpRequest() {
            @Override
            public HttpMethod method() {
                return method;
            }

            @Override
            public Map<HttpHeaderName<?>, List<?>> headers() {
                return Maps.readOnly(headers);
            }

            @Override
            public String toString() {
                return this.method() + " " + this.headers();
            }

        };
    }

    private List<ETag> ifNoneMatch() {
        return ETag.parseList("W/\"X1\",\"X2\",\"X3\"");
    }

    private ETag etag(final int value) {
        return ETagValidator.STRONG.setValue("X" + value);
    }

    @Override
    public Class<IfNoneMatchAwareHttpResponse> type() {
        return IfNoneMatchAwareHttpResponse.class;
    }
}
