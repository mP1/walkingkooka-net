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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.header.AcceptEncoding;
import walkingkooka.net.header.ContentEncoding;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.http.HttpEntity;

import java.util.List;
import java.util.Map;

public final class AutoGzipEncodingHttpResponseTest extends WrapperHttpRequestHttpResponseTestCase<AutoGzipEncodingHttpResponse> {

    private final static String GZIP = "gzip";
    private final static String NO_CONTENT_ENCODING = null;

    @Test
    public void testAddEntityRequestAcceptEncodingNotGzip() {
        final byte[] body = new byte[]{1, 2, 3};
        this.addEntityRequestWithAcceptEncodingAndCheck("X",
            body,
            NO_CONTENT_ENCODING,
            Maps.empty(),
            body);
    }

    @Test
    public void testAddEntityAcceptEncodingNotGzip() {
        final byte[] body = new byte[]{1, 2, 3};
        this.addEntityRequestWithAcceptEncodingAndCheck("X",
            body,
            NO_CONTENT_ENCODING,
            Maps.empty(),
            body);
    }

    @Test
    public void testAddEntityRequestAcceptEncodingWildcard() {
        final byte[] body = new byte[]{1, 2, 3};
        this.addEntityRequestWithAcceptEncodingAndCheck("*",
            body,
            NO_CONTENT_ENCODING,
            this.headersContentEncoding(GZIP),
            this.gzip(body));
    }

    @Test
    public void testAddEntityRequestAcceptEncodingGzip() {
        final byte[] body = new byte[]{1, 2, 3};
        this.addEntityRequestWithAcceptEncodingAndCheck("*",
            body,
            NO_CONTENT_ENCODING,
            this.headersContentEncoding(GZIP),
            this.gzip(body));
    }

    @Test
    public void testAddEntityRequestAcceptEncodingWildcardResponseContentEncodingNotGzip() {
        final byte[] body = new byte[]{1, 2, 3};
        this.addEntityRequestWithAcceptEncodingAndCheck("*",
            body,
            "NOT-GZIP",
            this.headersContentEncoding("NOT-GZIP"),
            body);
    }

    @Test
    public void testAddEntityRequestAcceptEncodingWildcardResponseContentEncodingGzipPresent() {
        final byte[] body = new byte[]{1, 2, 3, 4};
        this.addEntityRequestWithAcceptEncodingAndCheck("*",
            body,
            GZIP,
            this.headersContentEncoding(GZIP),
            body);
    }

    @Test
    public void testAddEntityRequestAcceptEncodingGzipResponseContentEncodingGzipPresent() {
        final byte[] body = new byte[]{1, 2, 3, 4};
        this.addEntityRequestWithAcceptEncodingAndCheck("gzip",
            body,
            GZIP,
            this.headersContentEncoding(GZIP),
            body);
    }

    private Map<HttpHeaderName<?>, List<?>> headersContentEncoding(final String contentEncoding) {
        return Maps.of(HttpHeaderName.CONTENT_ENCODING, Lists.of(ContentEncoding.parse(contentEncoding)));
    }

    private byte[] gzip(final byte[] body) {
        return AutoGzipEncodingHttpResponse.gzip(Binary.with(body)).value();
    }

    private void addEntityRequestWithAcceptEncodingAndCheck(final String acceptEncoding,
                                                            final byte[] body,
                                                            final String contentEncoding,
                                                            final Map<HttpHeaderName<?>, List<?>> expectedHeaders,
                                                            final byte[] expectedBody) {
        this.addEntity = 0;
        final Map<HttpHeaderName<?>, List<?>> headers = Maps.ordered();
        final HttpResponse response = this.createResponse(
            acceptEncoding,
            new FakeHttpResponse() {

                @Override
                public void setEntity(final HttpEntity e) {
                    checkEquals(httpEntity(expectedHeaders).setBody(Binary.with(expectedBody)),
                        e,
                        "entity");
                    addEntity++;
                }
            });
        if (null != contentEncoding) {
            headers.put(HttpHeaderName.CONTENT_ENCODING, Lists.of(ContentEncoding.parse(contentEncoding)));
        }
        response.setEntity(httpEntity(headers).setBody(Binary.with(body)));
        this.checkEquals(1, this.addEntity, "wrapped response setEntity(body) not called");
    }

    private int addEntity;

    private AutoGzipEncodingHttpResponse createResponse(final String acceptEncoding, final HttpResponse response) {
        return AutoGzipEncodingHttpResponse.with(
            this.createRequest(acceptEncoding),
            response);
    }

    @Override
    AutoGzipEncodingHttpResponse createResponse(final HttpRequest request, final HttpResponse response) {
        return AutoGzipEncodingHttpResponse.with(request, response);
    }

    @Override
    HttpRequest createRequest() {
        return this.createRequest(Maps.empty());
    }

    private HttpRequest createRequest(final String acceptEncoding) {
        return this.createRequest(null == acceptEncoding ?
            Maps.empty() :
            map(HttpHeaderName.ACCEPT_ENCODING, AcceptEncoding.parse(acceptEncoding)));
    }

    private HttpRequest createRequest(final Map<HttpHeaderName<?>, List<?>> headers) {
        return new FakeHttpRequest() {
            @Override
            public Map<HttpHeaderName<?>, List<?>> headers() {
                return Maps.readOnly(headers);
            }
        };
    }

    @Override
    public Class<AutoGzipEncodingHttpResponse> type() {
        return AutoGzipEncodingHttpResponse.class;
    }
}
