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

import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpStatusCode;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * A {@link BiConsumer} that tests if the request method is acceptable otherwise responds with a {@link walkingkooka.net.http.HttpStatusCode#METHOD_NOT_ALLOWED}.
 * The {@link Predicate#toString()} is also used to report the allowed methods.
 */
final class MethodNotAllowedHttpRequestHttpResponseBiConsumer implements BiConsumer<HttpRequest, HttpResponse> {

    static MethodNotAllowedHttpRequestHttpResponseBiConsumer with(final Predicate<HttpMethod> method,
                                                                  final BiConsumer<HttpRequest, HttpResponse> handler) {
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(handler, "handler");

        return new MethodNotAllowedHttpRequestHttpResponseBiConsumer(method, handler);
    }

    private MethodNotAllowedHttpRequestHttpResponseBiConsumer(final Predicate<HttpMethod> method,
                                                              final BiConsumer<HttpRequest, HttpResponse> handler) {
        super();
        this.method = method;
        this.handler = handler;
    }

    @Override
    public void accept(final HttpRequest request,
                       final HttpResponse response) {
        final HttpMethod method = request.method();
        if (this.method.test(method)) {
            this.handler.accept(request, response);
        } else {
            response.setStatus(HttpStatusCode.METHOD_NOT_ALLOWED.setMessage("Expected " + this.method + " got " + method));
            response.addEntity(HttpEntity.EMPTY);
        }
    }

    /**
     * A {@link Predicate} used to test if the request method is acceptable.
     */
    private final Predicate<HttpMethod> method;

    private final BiConsumer<HttpRequest, HttpResponse> handler;

    @Override
    public String toString() {
        return this.method + " " + this.handler;
    }
}
