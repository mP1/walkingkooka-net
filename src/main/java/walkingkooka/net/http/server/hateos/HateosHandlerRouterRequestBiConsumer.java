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

package walkingkooka.net.http.server.hateos;

import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpResponse;
import walkingkooka.tree.Node;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * A {@link BiConsumer} which accepts a request and then dispatches after testing the {@link HttpMethod}. This is the product of
 * {@link HateosHandlerRouterBuilder}.
 */
final class HateosHandlerRouterRequestBiConsumer<N extends Node<N, ?, ?, ?>> implements BiConsumer<HttpRequest, HttpResponse> {

    /**
     * Factory called by {@link HateosHandlerRouter#route}
     */
    static <N extends Node<N, ?, ?, ?>> HateosHandlerRouterRequestBiConsumer<N> with(final HateosHandlerRouter<N> router) {
        return new HateosHandlerRouterRequestBiConsumer<>(router);
    }

    /**
     * Private ctor use factory.
     */
    private HateosHandlerRouterRequestBiConsumer(final HateosHandlerRouter<N> router) {
        super();
        this.router = router;
    }

    /**
     * Dispatches the request to {@link HateosHandlerRouterRequestBiConsumerHttpMethodVisitor}.
     */
    @Override
    public void accept(final HttpRequest request, final HttpResponse response) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(response, "response");

        try {
            HateosHandlerRouterRequestBiConsumerHttpMethodVisitor.with(request, response, this.router)
                    .accept(request.method());
        } catch (final UnsupportedOperationException unsupported) {
            response.setStatus(HttpStatusCode.NOT_IMPLEMENTED.setMessageOrDefault(unsupported.getMessage()));
        } catch (final IllegalArgumentException badRequest) {
            response.setStatus(HttpStatusCode.BAD_REQUEST.setMessageOrDefault(badRequest.getMessage()));
        }
    }

    private final HateosHandlerRouter<N> router;

    @Override
    public String toString() {
        return this.router.toString();
    }
}
