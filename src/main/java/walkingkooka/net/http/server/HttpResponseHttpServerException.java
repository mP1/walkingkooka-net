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
import java.util.Optional;

/**
 * A {@link HttpServerException} that includes a {@link HttpStatus} and optional
 * {@link HttpEntity} which could hold additional messages and more.
 */
public final class HttpResponseHttpServerException extends HttpServerException {

    private final static long serialVersionUID = 1L;

    public final static Optional<HttpEntity> NO_ENTITY = Optional.empty();

    protected HttpResponseHttpServerException() {
        super();
        this.status = null;
        this.entity = null;
    }

    public HttpResponseHttpServerException(final HttpStatus status,
                                           final Optional<HttpEntity> entity) {
        super(Objects.requireNonNull(status, "status").message());
        this.status = status;
        this.entity = Objects.requireNonNull(entity, "entity");
    }

    public HttpStatus status() {
        return this.status;
    }

    private final HttpStatus status;

    public Optional<HttpEntity> entity() {
        return this.entity;
    }

    private final Optional<HttpEntity> entity;
}
