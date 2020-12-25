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
import walkingkooka.net.http.HttpStatusCode;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Wraps another handler, catching any thrown exceptions and sending a 500 with the body holding the stacktrace
 */
final class StacktraceDumpingHttpRequestHttpResponseBiConsumer implements BiConsumer<HttpRequest, HttpResponse> {

    static StacktraceDumpingHttpRequestHttpResponseBiConsumer with(final BiConsumer<HttpRequest, HttpResponse> handler) {
        Objects.requireNonNull(handler, "handler");
        return new StacktraceDumpingHttpRequestHttpResponseBiConsumer(handler);
    }

    private StacktraceDumpingHttpRequestHttpResponseBiConsumer(final BiConsumer<HttpRequest, HttpResponse> handler) {
        super();
        this.handler = handler;
    }

    @Override
    public void accept(final HttpRequest request,
                       final HttpResponse response) {
        try {
            this.handler.accept(request, response);
        } catch (final Exception cause) {
            response.setStatus(HttpStatusCode.INTERNAL_SERVER_ERROR
                    .setMessageOrDefault(cause.getMessage()));
            response.addEntity(HttpEntity.dumpStackTrace(cause));
        }
    }

    private final BiConsumer<HttpRequest, HttpResponse> handler;

    @Override
    public String toString() {
        return this.handler.toString();
    }
}
