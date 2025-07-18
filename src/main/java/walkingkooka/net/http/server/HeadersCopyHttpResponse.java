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
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link HttpResponse} that merges the headers from the request when the first entity is added. All other methods
 * simple delegate to the wrapped {@link HttpResponse}.
 */
final class HeadersCopyHttpResponse extends WrapperHttpRequestHttpResponse {

    static HeadersCopyHttpResponse with(final HttpRequest request,
                                        final Set<HttpHeaderName<?>> headers,
                                        final HttpResponse response) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(headers, "headers");
        final Set<HttpHeaderName<?>> copy = Sets.immutable(headers);
        if (copy.isEmpty()) {
            throw new IllegalArgumentException("Headers to copy must not be empty");
        }

        Objects.requireNonNull(response, "response");

        return new HeadersCopyHttpResponse(request,
            headers,
            response);
    }

    private HeadersCopyHttpResponse(final HttpRequest request,
                                    final Set<HttpHeaderName<?>> headers,
                                    final HttpResponse response) {
        super(request, response);
        this.headers = headers;
    }

    @Override
    public Optional<HttpProtocolVersion> version() {
        return this.response.version();
    }

    @Override
    public void setVersion(final HttpProtocolVersion version) {
        this.response.setVersion(version);
    }

    @Override
    public void setStatus(final HttpStatus status) {
        this.response.setStatus(status);
    }

    /**
     * Merges the request headers followed by the entity's original headers.
     */
    @Override
    public void setEntity(final HttpEntity entity) {
        Objects.requireNonNull(entity, "entity");

        HttpEntity set = entity;

        final HttpResponse response = this.response;
        if (response.entity().isEmpty()) {

            final Map<HttpHeaderName<?>, List<?>> requestHeaders = this.request.headers();
            final Map<HttpHeaderName<?>, List<?>> allHeaders = Maps.sorted();

            // copy request headers...
            for (final HttpHeaderName<?> name : this.headers) {
                final List<?> copy = requestHeaders.get(name);
                if (null != copy && copy.size() > 0) {
                    allHeaders.put(name, copy);
                }
            }

            // copy entity being added headers...
            for (final Entry<HttpHeaderName<?>, List<?>> nameAndHeader : entity.headers().entrySet()) {
                allHeaders.put(nameAndHeader.getKey(), nameAndHeader.getValue());
            }

            set = entity.setHeaders(allHeaders);
        }
        response.setEntity(set);
    }

    private final Set<HttpHeaderName<?>> headers;
}
