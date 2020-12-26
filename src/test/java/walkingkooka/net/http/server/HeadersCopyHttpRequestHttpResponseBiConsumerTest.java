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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HeadersCopyHttpRequestHttpResponseBiConsumerTest extends HttpRequestHttpResponseBiConsumerTestCase2<HeadersCopyHttpRequestHttpResponseBiConsumer> {

    @Test
    public void testWithNullHeadersFails() {
        assertThrows(NullPointerException.class, () -> HeadersCopyHttpRequestHttpResponseBiConsumer.with(null, handler()));
    }

    @Test
    public void testWithNullHandlersFails() {
        assertThrows(NullPointerException.class, () -> HeadersCopyHttpRequestHttpResponseBiConsumer.with(headers(), null));
    }

    @Test
    public void testAccept() {
        final MediaType contentType = MediaType.parse("custon/media-type");

        final HttpRequest request = new FakeHttpRequest() {
            @Override
            public Map<HttpHeaderName<?>, List<?>> headers() {
                return Maps.of(HttpHeaderName.CONTENT_TYPE, Lists.of(contentType));
            }
        };
        final HttpResponse response = HttpResponses.recording();
        this.createBiConsumer().accept(request, response);

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(status());
        expected.addEntity(entity()
                .addHeader(HttpHeaderName.CONTENT_TYPE, contentType)
        );

        assertEquals(expected, response);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createBiConsumer(), headers() + " " + TOSTRING);
    }

    private HeadersCopyHttpRequestHttpResponseBiConsumer createBiConsumer() {
        return HeadersCopyHttpRequestHttpResponseBiConsumer.with(
                headers(),
                handler()
        );
    }

    private static Set<HttpHeaderName<?>> headers() {
        return Sets.of(HttpHeaderName.CONTENT_TYPE, HttpHeaderName.CONTENT_LENGTH, HttpHeaderName.with("X-Custom-Header"));
    }

    private static BiConsumer<HttpRequest, HttpResponse> handler() {
        return new BiConsumer<>() {
            @Override
            public void accept(final HttpRequest request, final HttpResponse response) {
                response.setStatus(status());
                response.addEntity(entity());
            }

            @Override
            public String toString() {
                return TOSTRING;
            }
        };
    }

    private final static String TOSTRING = "Custom to String 123";

    private static HttpStatus status() {
        return HttpStatusCode.INTERNAL_SERVER_ERROR.setMessage("Something went wrong");
    }

    private static HttpEntity entity() {
        return HttpEntity.EMPTY
                .addHeader(HttpHeaderName.CONTENT_LENGTH, 1234L)
                .setBodyText("Body1234");
    }

    @Override
    public Class<HeadersCopyHttpRequestHttpResponseBiConsumer> type() {
        return HeadersCopyHttpRequestHttpResponseBiConsumer.class;
    }
}
