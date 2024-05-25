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

import walkingkooka.route.Router;

import java.util.Objects;

/**
 * A {@link HttpHandler} that attempts locate the handler from the request or uses the default handler.
 */
final class RouterHttpHandler implements HttpHandler {

    static RouterHttpHandler with(final Router<HttpRequestAttribute<?>, HttpHandler> router,
                                  final HttpHandler notFound) {
        Objects.requireNonNull(router, "router");
        Objects.requireNonNull(notFound, "notFound");

        return new RouterHttpHandler(router, notFound);
    }

    private RouterHttpHandler(final Router<HttpRequestAttribute<?>, HttpHandler> router,
                              final HttpHandler notFound) {
        super();
        this.router = router;
        this.notFound = notFound;
    }

    @Override
    public void handle(final HttpRequest request, final HttpResponse response) {
        this.router.route(request.routerParameters())
                .orElse(this.notFound)
                .handle(request, response);
    }

    private final Router<HttpRequestAttribute<?>, HttpHandler> router;
    private final HttpHandler notFound;

    @Override
    public String toString() {
        return this.router + " OR " + this.notFound;
    }
}
