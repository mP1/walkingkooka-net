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

import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.http.HttpEntity;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

/**
 * Copies headers from the request to the response. If a header is absent from the request it is skipped.
 */
final class HttpHandlerWrapperSharedHeadersCopy<C extends HttpHandlerContext> extends HttpHandlerWrapperShared<C> {

    static <C extends HttpHandlerContext> HttpHandlerWrapperSharedHeadersCopy<C> with(final Set<HttpHeaderName<?>> headers,
                                                                                      final HttpHandler<C> handler) {
        Objects.requireNonNull(headers, "headers");

        final Set<HttpHeaderName<?>> copy = Sets.immutable(headers);
        if (copy.isEmpty()) {
            throw new IllegalArgumentException("Headers to copy must not be empty");
        }

        return new HttpHandlerWrapperSharedHeadersCopy<>(copy, handler);
    }

    private HttpHandlerWrapperSharedHeadersCopy(final Set<HttpHeaderName<?>> headers,
                                                final HttpHandler<C> handler) {
        super(handler);
        this.headers = headers;
    }

    @Override
    void handle0(final HttpRequest request,
                 final HttpResponse response,
                 final C context) {
        this.handler.handle(
            request,
            response,
            context
        );

        HttpEntity responseEntity = response.entity();
        if (responseEntity.isNotEmpty()) {

            final Map<HttpHeaderName<?>, List<?>> requestHeaders = request.headers();
            final Map<HttpHeaderName<?>, List<?>> allHeaders = Maps.sorted();

            // copy request headers...
            for (final HttpHeaderName<?> name : this.headers) {
                final List<?> copy = requestHeaders.get(name);
                if (null != copy && copy.size() > 0) {
                    allHeaders.put(
                        name,
                        copy
                    );
                }
            }

            // copy entity added headers...
            for (final Entry<HttpHeaderName<?>, List<?>> nameAndHeader : responseEntity.headers().entrySet()) {
                allHeaders.put(
                    nameAndHeader.getKey(),
                    nameAndHeader.getValue()
                );
            }

            responseEntity = responseEntity.setHeaders(allHeaders);
        }
        response.setEntity(responseEntity);
    }

    /**
     * Headers that will be copied from the request to the response.
     */
    private final Set<HttpHeaderName<?>> headers;

    @Override
    public String toString() {
        return this.headers + " " + this.handler;
    }
}
