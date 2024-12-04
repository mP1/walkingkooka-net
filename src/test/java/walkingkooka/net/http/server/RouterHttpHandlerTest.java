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

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class RouterHttpHandlerTest extends HttpHandlerTestCase<RouterHttpHandler> {

    @Test
    public void testWithNullRouterFails() {
        assertThrows(NullPointerException.class, () -> RouterHttpHandler.with(null, this.notFound()));
    }

    @Test
    public void testWithNotFoundFails() {
        assertThrows(NullPointerException.class, () -> RouterHttpHandler.with(this.router(), null));
    }

    @Test
    public void testRouted() {
        this.handleAndCheck(HttpMethod.POST, HttpStatusCode.OK.status());
    }

    @Test
    public void testNotFound() {
        this.handleAndCheck(HttpMethod.GET, HttpStatusCode.NOT_FOUND.status());
    }

    private void handleAndCheck(final HttpMethod method,
                                final HttpStatus status) {
        final HttpResponse response = HttpResponses.recording();

        this.createHttpHandler()
                .handle(
                        this.request(method),
                        response
                );

        final HttpResponse expected = HttpResponses.recording();
        expected.setStatus(status);
        this.checkEquals(expected, response);
    }

    @Test
    public void testToString() {
        final Router<HttpRequestAttribute<?>, HttpHandler> router = this.router();
        final HttpHandler notFound = this.notFound();

        this.toStringAndCheck(RouterHttpHandler.with(router, notFound), router + " OR " + notFound);
    }

    private RouterHttpHandler createHttpHandler() {
        return RouterHttpHandler.with(this.router(), this.notFound());
    }

    private Router<HttpRequestAttribute<?>, HttpHandler> router() {
        return this::router0;
    }

    private Optional<HttpHandler> router0(Map<HttpRequestAttribute<?>, Object> parameters) {
        return Optional.ofNullable(HttpMethod.POST == parameters.get(HttpRequestAttributes.METHOD) ? this.ok() : null);
    }

    private HttpHandler ok() {
        return this::ok0;
    }

    private void ok0(final HttpRequest request, final HttpResponse response) {
        response.setStatus(HttpStatusCode.OK.status());
    }

    private HttpHandler notFound() {
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
    public Class<RouterHttpHandler> type() {
        return RouterHttpHandler.class;
    }
}
