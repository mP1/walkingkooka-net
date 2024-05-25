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

import walkingkooka.collect.set.Sets;
import walkingkooka.net.header.HttpHeaderName;

import java.util.Objects;
import java.util.Set;

/**
 * Copies headers from the request to the response. If a header is absent from the request it is skipped.
 */
final class HeadersCopyHttpHandler implements HttpHandler {

    static HeadersCopyHttpHandler with(final Set<HttpHeaderName<?>> headers,
                                       final HttpHandler handler) {
        Objects.requireNonNull(headers, "headers");
        Objects.requireNonNull(handler, "handler");

        final Set<HttpHeaderName<?>> copy = Sets.immutable(headers);
        if (copy.isEmpty()) {
            throw new IllegalArgumentException("Headers to copy must not be empty");
        }

        return new HeadersCopyHttpHandler(copy, handler);
    }

    private HeadersCopyHttpHandler(final Set<HttpHeaderName<?>> headers,
                                   final HttpHandler handler) {
        super();
        this.headers = headers;
        this.handler = handler;
    }

    @Override
    public void handle(final HttpRequest request,
                       final HttpResponse response) {
        this.handler.handle(request,
                HttpResponses.headersCopy(
                        request,
                        this.headers,
                        response
                )
        );
    }

    /**
     * Headers that will be copied from the request to the response.
     */
    private final Set<HttpHeaderName<?>> headers;

    private final HttpHandler handler;

    @Override
    public String toString() {
        return this.headers + " " + this.handler;
    }
}
