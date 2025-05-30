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

import walkingkooka.Cast;
import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringBuilderOption;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpStatus;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link HttpResponse} that records set status and added entity.
 */
final class RecordingHttpResponse implements HttpResponse {

    /**
     * Creates an empty recording http response.
     */
    static RecordingHttpResponse with() {
        return new RecordingHttpResponse();
    }

    /**
     * Private ctor use factory.
     */
    private RecordingHttpResponse() {
        super();
        this.entity = HttpEntity.EMPTY;
    }

    @Override
    public void setVersion(final HttpProtocolVersion version) {
        Objects.requireNonNull(version, "version");
        this.version = version;
    }

    @Override
    public Optional<HttpProtocolVersion> version() {
        return Optional.ofNullable(this.version);
    }

    private HttpProtocolVersion version;

    @Override
    public void setStatus(final HttpStatus status) {
        Objects.requireNonNull(status, "status");
        this.status = status;
    }

    @Override
    public Optional<HttpStatus> status() {
        return Optional.ofNullable(this.status);
    }

    private HttpStatus status;

    @Override
    public void setEntity(final HttpEntity entity) {
        Objects.requireNonNull(entity, "entity");
        this.entity = entity;
    }

    @Override
    public HttpEntity entity() {
        return this.entity;
    }

    private HttpEntity entity;

    @Override
    public int hashCode() {
        return Objects.hash(this.version, this.status, this.entity);
    }

    public boolean equals(final Object other) {
        return this == other ||
            other instanceof RecordingHttpResponse &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final RecordingHttpResponse other) {
        return Objects.equals(this.version, other.version) &&
            Objects.equals(this.status, other.status) &&
            Objects.equals(this.entity, other.entity);
    }

    @Override
    public String toString() {
        final int length = 64 * 1024;

        return ToStringBuilder.empty()
            .globalLength(length)
            .valueLength(length)
            .valueSeparator("\r\n")
            .separator("\r\n")
            .disable(ToStringBuilderOption.QUOTE)
            .value(ToStringBuilder.empty().valueSeparator(" ").value(this.version).value(this.status).build())
            .value(this.entity)
            .build();
    }
}
