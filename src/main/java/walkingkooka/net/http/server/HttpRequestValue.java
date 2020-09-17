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

import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringBuilderOption;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpTransport;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A {@link HttpRequest} assembled from various components including {@link HttpEntity}.
 * Currently this is the only {@link HttpRequest} type that support json marshalling/unmarshalling.
 */
final class HttpRequestValue implements HttpRequest {

    /**
     * Factory that creates a {@link HttpRequestValue} with the given values.
     */
    static HttpRequestValue with(final HttpMethod method,
                                 final HttpTransport transport,
                                 final RelativeUrl url,
                                 final HttpProtocolVersion protocolVersion,
                                 final HttpEntity... entities) {
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(transport, "transport");
        Objects.requireNonNull(url, "url");
        Objects.requireNonNull(protocolVersion, "protocolVersion");
        Objects.requireNonNull(entities, "entities");

        switch (entities.length) {
            case 0:
            case 1:
                break;
            default:
                throw new IllegalArgumentException("Multipart is not supported: " + entities.length);
        }
        return new HttpRequestValue(method, transport, url, protocolVersion, Arrays.copyOf(entities, entities.length));
    }

    private HttpRequestValue(final HttpMethod method,
                             final HttpTransport transport,
                             final RelativeUrl url,
                             final HttpProtocolVersion protocolVersion,
                             final HttpEntity[] entities) {
        super();

        Arrays.stream(entities)
                .forEach(e -> Objects.requireNonNull(e, "entities contains null"));

        this.transport = transport;
        this.method = method;
        this.url = url;
        this.protocolVersion = protocolVersion;
        this.entities = entities;
    }

    @Override
    public HttpMethod method() {
        return this.method;
    }

    private final HttpMethod method;

    @Override
    public HttpTransport transport() {
        return this.transport;
    }

    private final HttpTransport transport;

    @Override
    public RelativeUrl url() {
        return this.url;
    }

    private final RelativeUrl url;

    @Override
    public HttpProtocolVersion protocolVersion() {
        return this.protocolVersion;
    }

    private final HttpProtocolVersion protocolVersion;

    @Override
    public Map<HttpHeaderName<?>, List<?>> headers() {
        return this.entities[0].headers();
    }

    @Override
    public byte[] body() {
        return this.entities[0].body().value();
    }

    @Override
    public String bodyText() {
        return this.entities[0].bodyText();
    }

    private final HttpEntity[] entities;

    // parameters are always empty......................................................................................

    /**
     * ALways returns no parameters.
     */
    @Override
    public Map<HttpRequestParameterName, List<String>> parameters() {
        return Maps.empty();
    }

    /**
     * Always returns nothing.
     */
    @Override
    public List<String> parameterValues(final HttpRequestParameterName parameterName) {
        Objects.requireNonNull(parameterName, "parameterName");
        return Lists.empty();
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.transport, this.method, this.url, this.protocolVersion, Arrays.hashCode(this.entities));
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || other instanceof HttpRequestValue && this.equals0((HttpRequestValue) other);
    }

    private boolean equals0(final HttpRequestValue other) {
        return this.transport().equals(other.transport()) &&
                this.method().equals(other.method()) &&
                this.url().equals(other.url()) &&
                this.protocolVersion().equals(other.protocolVersion()) &&
                Arrays.equals(this.entities, other.entities);
    }

    @Override
    public String toString() {
        final ToStringBuilder b = ToStringBuilder.empty();

        b.disable(ToStringBuilderOption.QUOTE);

        final String eol = HttpRequest.LINE_ENDING.toString();

        // request line
        b.valueSeparator(" ");
        b.value(this.method());
        b.value(this.url());
        b.valueSeparator(eol);
        b.value(this.protocolVersion());

        b.append(eol);
        b.value(this.entities[0]);

        return b.build();
    }
}
