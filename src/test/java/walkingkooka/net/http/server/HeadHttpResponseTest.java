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
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class HeadHttpResponseTest extends WrapperHttpRequestHttpResponseTestCase<HeadHttpResponse> {

    private final static int CONTENT_LENGTH = 26;

    @Test
    public void testGetNotWrapped() {
        this.responseNotWrappedAndCheck(HttpMethod.GET);
    }

    @Test
    public void testPostNotWrapped() {
        this.responseNotWrappedAndCheck(HttpMethod.POST);
    }

    @Test
    public void testDeleteNotWrapped() {
        this.responseNotWrappedAndCheck(HttpMethod.DELETE);
    }

    @Test
    public void testPutNotWrapped() {
        this.responseNotWrappedAndCheck(HttpMethod.PUT);
    }

    private void responseNotWrappedAndCheck(final HttpMethod method) {
        final HttpResponse response = HttpResponses.fake();
        assertSame(response, HeadHttpResponse.with(this.createRequest(method), response));
    }

    @Test
    public void testHeadIgnoreResponseBody() {
        final Map<HttpHeaderName<?>, List<?>> headers = this.headers();

        for (final HttpStatusCode status : HttpStatusCode.values()) {
            this.setStatusAddEntityAndCheck(this.createRequest(HttpMethod.HEAD),
                    status.status(),
                    httpEntity(headers).setBody(Binary.with(new byte[CONTENT_LENGTH])),
                    status.status(),
                    httpEntity(headers));
        }
    }

    @Test
    public void testMultipartResponse() {
        final HttpStatus status = HttpStatusCode.OK.status();
        final Map<HttpHeaderName<?>, List<?>> headers = this.headers();

        final RecordingHttpResponse recording = RecordingHttpResponse.with();
        final HttpRequest request = this.createRequest(HttpMethod.HEAD);
        final HttpResponse response = HeadHttpResponse.with(request, recording);

        response.setStatus(status);

        response.addEntity(httpEntity(headers).setBody(Binary.with(new byte[CONTENT_LENGTH])));

        final byte[] bytes2 = new byte[CONTENT_LENGTH];
        Arrays.fill(bytes2, (byte) 'A');
        response.addEntity(httpEntity(headers).setBody(Binary.with(bytes2)));

        this.checkResponse(recording,
                request,
                status,
                httpEntity(headers));
    }

    private Map<HttpHeaderName<?>, List<?>> headers() {
        return Maps.of(HttpHeaderName.CONTENT_TYPE, list(MediaType.BINARY),
                HttpHeaderName.CONTENT_LENGTH, list(Long.valueOf(CONTENT_LENGTH)),
                HttpHeaderName.SERVER, list("Server 123"));
    }

    @Override
    HeadHttpResponse createResponse(final HttpRequest request, final HttpResponse response) {
        return Cast.to(HeadHttpResponse.with(request, response));
    }

    @Override
    HttpRequest createRequest() {
        return this.createRequest(HttpMethod.HEAD);
    }

    private HttpRequest createRequest(final HttpMethod method) {
        return new FakeHttpRequest() {
            @Override
            public HttpMethod method() {
                return method;
            }

            public String toString() {
                return this.method().toString();
            }
        };
    }

    @Override
    public Class<HeadHttpResponse> type() {
        return HeadHttpResponse.class;
    }
}
