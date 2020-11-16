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
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpStatusCode;

import java.util.Optional;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StacktraceDumpingHttpRequestHttpResponseBiConsumerTest extends HttpRequestHttpResponseBiConsumerTestCase2<StacktraceDumpingHttpRequestHttpResponseBiConsumer> {

    @Test
    public void testWithNullRouterFails() {
        assertThrows(NullPointerException.class, () -> StacktraceDumpingHttpRequestHttpResponseBiConsumer.with(null));
    }

    @Test
    public void testNothingThrown() {
        this.handled = false;

        final HttpRequest request = HttpRequests.fake();
        final HttpResponse response = HttpResponses.fake();
        StacktraceDumpingHttpRequestHttpResponseBiConsumer.with((r, rr) -> {
            assertSame(r, request);
            assertSame(rr, response);

            this.handled = true;
        }).accept(request, response);

        assertEquals(true, this.handled);
    }

    private boolean handled;

    @Test
    public void testThrown() {
        final String message = "message 123";

        final HttpRequest request = HttpRequests.fake();
        final HttpResponse response = HttpResponses.recording();
        StacktraceDumpingHttpRequestHttpResponseBiConsumer.with((r, rr) -> {
            assertSame(r, request);
            assertSame(rr, response);

            throw new UnsupportedOperationException(message);
        }).accept(request, response);

        assertEquals(Optional.of(HttpStatusCode.INTERNAL_SERVER_ERROR.setMessage(message)), response.status());
        final HttpEntity entity = response.entities().get(0);

        assertEquals(Lists.of(MediaType.TEXT_PLAIN), entity.headers().get(HttpHeaderName.CONTENT_TYPE), "content-type header");

        final String body = entity.bodyText();
        assertEquals(true, body.contains(UnsupportedOperationException.class.getSimpleName()), () -> body);
    }

    @Test
    public void testToString() {
        final BiConsumer<HttpRequest, HttpResponse> wrapped = wrapped();
        this.toStringAndCheck(StacktraceDumpingHttpRequestHttpResponseBiConsumer.with(wrapped), wrapped.toString());
    }

    private BiConsumer<HttpRequest, HttpResponse> wrapped() {
        return new BiConsumer<>() {
            @Override
            public void accept(final HttpRequest request, final HttpResponse response) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String toString() {
                return "Custom-to-String-123";
            }
        };
    }

    @Override
    public Class<StacktraceDumpingHttpRequestHttpResponseBiConsumer> type() {
        return StacktraceDumpingHttpRequestHttpResponseBiConsumer.class;
    }
}
