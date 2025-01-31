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
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.NotAcceptableHeaderException;
import walkingkooka.net.http.HttpEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class AutoContentLengthHttpResponseTest extends WrapperHttpRequestHttpResponseTestCase<AutoContentLengthHttpResponse> {

    @Test
    public void testAddEntityContentLengthAbsent() {
        this.addEntityAndCheck(null,
            new byte[]{1, 2, 3});
    }

    @Test
    public void testAddEntityContentLengthSetBefore() {
        final byte[] bytes = new byte[]{1, 2, 3};
        this.addEntityAndCheck((long) bytes.length,
            bytes);
    }

    @Test
    public void testAddEntityContentLengthIncorrectFail() {
        assertThrows(NotAcceptableHeaderException.class, () -> this.addEntityAndCheck(999L,
            new byte[]{1, 2, 3}));
    }

    private void addEntityAndCheck(final Long contentLength,
                                   final byte[] body) {
        final List<HttpEntity> added = Lists.array();

        final HttpResponse response = this.createResponse(
            new FakeHttpResponse() {

                @Test
                public void setEntity(final HttpEntity e) {
                    added.add(e);
                }
            });

        final Binary bodyBinary = Binary.with(body);
        response.setEntity((null == contentLength ?
            HttpEntity.EMPTY :
            HttpEntity.EMPTY.addHeader(HttpHeaderName.CONTENT_LENGTH, contentLength)).setBody(bodyBinary));
        this.checkEquals(Lists.of(HttpEntity.EMPTY.addHeader(HttpHeaderName.CONTENT_LENGTH, (long) body.length).setBody(bodyBinary)),
            added,
            "added entity");
    }

    @Override
    AutoContentLengthHttpResponse createResponse(final HttpRequest request, final HttpResponse response) {
        return AutoContentLengthHttpResponse.with(request, response);
    }

    @Override
    HttpRequest createRequest() {
        return this.createRequest(Maps.ordered());
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
    public Class<AutoContentLengthHttpResponse> type() {
        return AutoContentLengthHttpResponse.class;
    }
}
