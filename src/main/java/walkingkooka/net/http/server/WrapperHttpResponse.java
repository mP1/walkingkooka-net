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
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpStatus;

import java.util.Optional;

/**
 * Base class for any response, that wraps another {@link HttpResponse}.
 */
abstract class WrapperHttpResponse implements HttpResponse {

    WrapperHttpResponse(final HttpResponse response) {
        super();
        this.response = response;
    }

    @Override
    public void setVersion(final HttpProtocolVersion version) {
        this.response.setVersion(version);
    }

    @Override
    public Optional<HttpProtocolVersion> version() {
        return this.response.version();
    }

    @Override
    public final Optional<HttpStatus> status() {
        return this.response.status();
    }

    @Override
    public final HttpEntity entity() {
        return this.response.entity();
    }

    /**
     * The wrapped {@link HttpResponse} that eventually receives the status and entities after any filtering/processing.
     */
    final HttpResponse response;

    @Override
    public final String toString() {
        return this.response.toString();
    }
}
