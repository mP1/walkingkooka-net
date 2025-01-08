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
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.header.AcceptLanguage;
import walkingkooka.net.header.ETag;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HeadersCopyHttpResponseTest extends WrapperHttpRequestHttpResponseTestCase<HeadersCopyHttpResponse> {

    @Test
    public void testWithNullHeadersFails() {
        assertThrows(NullPointerException.class, () -> HeadersCopyHttpResponse.with(HttpRequests.fake(),
            null,
            HttpResponses.fake()));
    }

    @Test
    public void testWithEmptyHeadersFails() {
        assertThrows(IllegalArgumentException.class, () -> HeadersCopyHttpResponse.with(HttpRequests.fake(),
            Sets.empty(),
            HttpResponses.fake()));
    }

    @Test
    public void testHeadersCopied() {
        final List<MediaType> contentType = Lists.of(MediaType.TEXT_PLAIN);

        final HeadersCopyHttpResponse response = this.with(
            Maps.of(
                HttpHeaderName.CONTENT_TYPE, contentType,
                HttpHeaderName.CONTENT_LENGTH, Lists.of(123L),
                HttpHeaderName.ACCEPT_LANGUAGE, Lists.of(AcceptLanguage.parse("EN"))
            ),
            Sets.of(HttpHeaderName.CONTENT_TYPE)
        );

        final HttpEntity entity = HttpEntity.EMPTY
            .addHeader(HttpHeaderName.CONTENT_LENGTH, 456L)
            .addHeader(HttpHeaderName.E_TAG, ETag.wildcard());

        response.setEntity(entity);

        this.checkEquals(
            entity.setContentType(MediaType.TEXT_PLAIN),
            response.entity(),
            "entity"
        );
    }

    @Test
    public void testResponseHeaderReplaces() {
        final List<MediaType> contentType = Lists.of(MediaType.TEXT_PLAIN);

        final HeadersCopyHttpResponse response = this.with(
            Maps.of(
                HttpHeaderName.CONTENT_TYPE, contentType,
                HttpHeaderName.CONTENT_LENGTH, Lists.of(123L),
                HttpHeaderName.ACCEPT_LANGUAGE, Lists.of(AcceptLanguage.parse("EN"))
            ),
            Sets.of(HttpHeaderName.CONTENT_TYPE)
        );

        final HttpEntity entity = HttpEntity.EMPTY
            .setContentType(MediaType.IMAGE_BMP) // wins will not be overwritten
            .addHeader(HttpHeaderName.CONTENT_LENGTH, 456L)
            .addHeader(HttpHeaderName.E_TAG, ETag.wildcard());

        response.setEntity(entity);

        this.checkEquals(
            entity,
            response.entity(),
            "entity"
        );
    }

    private HeadersCopyHttpResponse with(final Map<HttpHeaderName<?>, List<?>> requestHeaders,
                                         final Set<HttpHeaderName<?>> headers) {
        return HeadersCopyHttpResponse.with(
            new FakeHttpRequest() {
                @Override
                public Map<HttpHeaderName<?>, List<?>> headers() {
                    return requestHeaders;
                }
            },
            headers,
            new FakeHttpResponse() {
                @Override
                public void setEntity(final HttpEntity entity) {
                    this.httpEntity = entity;
                }

                @Override
                public HttpEntity entity() {
                    return httpEntity;
                }

                private HttpEntity httpEntity = HttpEntity.EMPTY;

                @Override
                public String toString() {
                    return this.httpEntity.toString();
                }
            }
        );
    }

    @Override
    HeadersCopyHttpResponse createResponse(final HttpRequest request, final HttpResponse response) {
        return Cast.to(HeadersCopyHttpResponse.with(request,
            Sets.of(HttpHeaderName.CONTENT_TYPE),
            response));
    }

    @Override
    HttpRequest createRequest() {
        return HttpRequests.fake();
    }

    @Override
    public Class<HeadersCopyHttpResponse> type() {
        return HeadersCopyHttpResponse.class;
    }
}
