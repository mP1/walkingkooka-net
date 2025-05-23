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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.header.ContentEncoding;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class DefaultHeadersHttpResponseTest extends WrapperHttpResponseTestCase<DefaultHeadersHttpResponse> {

    @Test
    public void testWithNullHeadersFails() {
        assertThrows(NullPointerException.class, () -> DefaultHeadersHttpResponse.with(null, HttpResponses.fake()));
    }

    @Test
    public void testEmptyHeadersNotWrapped() {
        final HttpResponse wrapped = HttpResponses.fake();
        assertSame(wrapped, DefaultHeadersHttpResponse.with(Maps.empty(), wrapped));
    }

    @Test
    public void testOnlyAddsDefaultsForMissingHeadersForFirstEntity() {
        final HttpStatus status = HttpStatusCode.OK.status();

        final int contentLength = 26;

        final Map<HttpHeaderName<?>, List<?>> responseHeaders = Maps.ordered();
        responseHeaders.put(HttpHeaderName.CONTENT_LENGTH, Lists.of(Long.valueOf(contentLength)));
        responseHeaders.put(HttpHeaderName.LAST_MODIFIED, Lists.of(LocalDateTime.of(2000, 12, 31, 6, 28, 29)));
        responseHeaders.put(HttpHeaderName.SERVER, Lists.of("Replaces original value Server 123"));

        final Binary body = Binary.with(new byte[contentLength]);

        final RecordingHttpResponse recording = RecordingHttpResponse.with();
        final HttpRequest request = HttpRequests.fake();

        DefaultHeadersHttpResponse response = this.createResponse(request, recording);

        final HttpProtocolVersion version = HttpProtocolVersion.VERSION_1_1;

        response.setVersion(version);
        response.setStatus(status);
        response.setEntity(httpEntity(responseHeaders).setBody(body));

        final HttpEntity second = HttpEntity.EMPTY
            .addHeader(HttpHeaderName.SERVER, "Server2")
            .setBody(Binary.with(new byte[2]));
        response.setEntity(second);

        final Map<HttpHeaderName<?>, List<?>> finalHeaders = Maps.ordered();
        finalHeaders.putAll(this.headers());
        finalHeaders.putAll(responseHeaders);

        this.checkResponse(recording,
            request,
            version,
            status,
            httpEntity(finalHeaders).setBody(body),
            second);
    }

    @Override
    HttpRequest createRequest() {
        return HttpRequests.fake();
    }

    @Override
    DefaultHeadersHttpResponse createResponse(final HttpRequest request, final HttpResponse response) {
        return Cast.to(this.createResponse(this.headers(), response));
    }

    private Map<HttpHeaderName<?>, List<?>> headers() {
        return map(HttpHeaderName.CONTENT_ENCODING, ContentEncoding.parse("EN"),
            HttpHeaderName.SERVER, "Server 123");
    }

    private DefaultHeadersHttpResponse createResponse(final Map<HttpHeaderName<?>, List<?>> headers,
                                                      final HttpResponse response) {
        return Cast.to(DefaultHeadersHttpResponse.with(headers, response));
    }

    @Override
    public Class<DefaultHeadersHttpResponse> type() {
        return DefaultHeadersHttpResponse.class;
    }
}
