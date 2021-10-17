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
import walkingkooka.net.http.HttpStatus;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Wraps another handler, catching any thrown exceptions and sending a 500 with the body holding the stacktrace
 */
final class StacktraceDumpingHttpRequestHttpResponseBiConsumer implements BiConsumer<HttpRequest, HttpResponse> {

    static StacktraceDumpingHttpRequestHttpResponseBiConsumer with(final BiConsumer<HttpRequest, HttpResponse> handler,
                                                                   final Function<Throwable, HttpStatus> throwableTranslator) {
        Objects.requireNonNull(handler, "handler");
        Objects.requireNonNull(throwableTranslator, "throwableTranslator");

        return new StacktraceDumpingHttpRequestHttpResponseBiConsumer(handler, throwableTranslator);
    }

    private StacktraceDumpingHttpRequestHttpResponseBiConsumer(final BiConsumer<HttpRequest, HttpResponse> handler,
                                                               final Function<Throwable, HttpStatus> throwableTranslator) {
        super();
        this.handler = handler;
        this.throwableTranslator = throwableTranslator;
    }

    @Override
    public void accept(final HttpRequest request,
                       final HttpResponse response) {
        try {
            this.handler.accept(request, response);
        } catch (final Throwable cause) {
            response.setStatus(this.throwableTranslator.apply(cause));
            response.addEntity(HttpEntity.dumpStackTrace(cause));
        }
    }

    private final BiConsumer<HttpRequest, HttpResponse> handler;
    private final Function<Throwable, HttpStatus> throwableTranslator;

    @Override
    public String toString() {
        return this.handler + " " + this.throwableTranslator;
    }
}
