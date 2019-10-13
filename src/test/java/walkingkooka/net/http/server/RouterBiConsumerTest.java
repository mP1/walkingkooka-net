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
import walkingkooka.collect.map.Maps;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.route.Router;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.ToStringTesting;
import walkingkooka.type.JavaVisibility;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class RouterBiConsumerTest implements ClassTesting2<RouterBiConsumer>,
        ToStringTesting<RouterBiConsumer> {

    @Test
    public void testWithNullRouterFails() {
        assertThrows(NullPointerException.class, () -> RouterBiConsumer.with(null, this.notFound()));
    }

    @Test
    public void testWithNotFoundFails() {
        assertThrows(NullPointerException.class, () -> RouterBiConsumer.with(this.router(), null));
    }

    @Test
    public void testRouted() {
        this.consumeAndCheck(HttpMethod.POST, HttpStatusCode.OK.status());
    }

    @Test
    public void testNotFound() {
        this.consumeAndCheck(HttpMethod.GET, HttpStatusCode.NOT_FOUND.status());
    }

    private void consumeAndCheck(final HttpMethod method, final HttpStatus status) {
        final RecordingHttpResponse response = HttpResponses.recording();

        this.createBiConsumer()
                .accept(this.request(method), response);

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(status);
        assertEquals(expected, response);
    }

    @Test
    public void testToString() {
        final Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>> router = this.router();
        final BiConsumer<HttpRequest, HttpResponse> notFound = this.notFound();

        this.toStringAndCheck(RouterBiConsumer.with(router, notFound), router + " OR " + notFound);
    }

    private RouterBiConsumer createBiConsumer() {
        return RouterBiConsumer.with(this.router(), this.notFound());
    }

    private Router<HttpRequestAttribute<?>, BiConsumer<HttpRequest, HttpResponse>> router() {
        return this::router0;
    }

    private Optional<BiConsumer<HttpRequest, HttpResponse>> router0(Map<HttpRequestAttribute<?>, Object> parameters) {
        return Optional.ofNullable(HttpMethod.POST == parameters.get(HttpRequestAttributes.METHOD) ? this.ok() : null);
    }

    private BiConsumer<HttpRequest, HttpResponse> ok() {
        return this::ok0;
    }

    private void ok0(final HttpRequest request, final HttpResponse response) {
        response.setStatus(HttpStatusCode.OK.status());
    }

    private BiConsumer<HttpRequest, HttpResponse> notFound() {
        return this::notFound0;
    }

    private void notFound0(final HttpRequest request, final HttpResponse response) {
        response.setStatus(HttpStatusCode.NOT_FOUND.status());
    }

    private HttpRequest request(final HttpMethod method) {
        return new FakeHttpRequest() {

            public HttpMethod method() {
                return method;
            }

            @Override
            public Map<HttpRequestAttribute<?>, Object> routerParameters() {
                return Maps.of(HttpRequestAttributes.METHOD, this.method());
            }

            @Override
            public String toString() {
                return this.method().toString();
            }
        };
    }

    @Override
    public Class<RouterBiConsumer> type() {
        return RouterBiConsumer.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
