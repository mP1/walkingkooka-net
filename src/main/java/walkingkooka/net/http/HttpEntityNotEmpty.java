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

package walkingkooka.net.http;

import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringBuilderOption;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.header.HttpHeaderName;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A {@link HttpEntity} that captures the common functionalitu of {@link HttpEntityBinary} and {@link HttpEntityText}.
 */
abstract class HttpEntityNotEmpty extends HttpEntity {

    /**
     * Package private
     */
    HttpEntityNotEmpty(final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers) {
        super();
        this.headers = headers;
    }

    // headers..........................................................................................................

    @Override //
    final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers2() {
        return this.headers;
    }

    private final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers;

    @Override //
    final <T> HttpEntity setHeader0(final HttpHeaderName<T> header,
                                    final HttpEntityHeaderList value) {
        final HttpEntityHeaderList values = this.headers2().get(header);
        return value.equals(values) ?
                this :
                this.setHeader1(header, value);
    }

    /**
     * Copy all headers into a new {@link Map} and set the new header with a single value.
     */
    private <T> HttpEntity setHeader1(final HttpHeaderName<T> header,
                                      final HttpEntityHeaderList value) {
        final Map<HttpHeaderName<?>, HttpEntityHeaderList> updated = Maps.ordered();
        updated.putAll(this.headers2());
        updated.put(header, value);

        return this.replace(Maps.readOnly(updated));
    }

    @Override//
    final <T> HttpEntity addHeader0(final HttpHeaderName<T> header,
                                    final T value) {
        final HttpEntity added;

        final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers = this.headers2();
        final HttpEntityHeaderList values = headers.get(header);
        if (null == values) {
            // add a new header
            final Map<HttpHeaderName<?>, HttpEntityHeaderList> updated = Maps.ordered();
            updated.putAll(headers);
            updated.put(header, HttpEntityHeaderList.one(header, value));

            added = this.replace(Maps.readOnly(updated));
        } else {
            final Map<HttpHeaderName<?>, HttpEntityHeaderList> updated = Maps.ordered();
            updated.putAll(headers);

            if (values.contains(value)) {
                added = this; // already contains header+value return this;
            } else {
                // append value and return new entity
                updated.put(header, values.append(header, value));
                added = this.replace(Maps.readOnly(updated));
            }
        }

        return added;
    }

    @Override//
    final HttpEntity remove0(final HttpHeaderName<?> header) {
        final Map<HttpHeaderName<?>, HttpEntityHeaderList> removed = Maps.ordered();
        boolean changed = false;

        for (final Entry<HttpHeaderName<?>, HttpEntityHeaderList> headerAndValue : this.headers2().entrySet()) {
            final HttpHeaderName<?> possibleHeader = headerAndValue.getKey();
            HttpEntityHeaderList values = headerAndValue.getValue();
            if (possibleHeader.equals(header)) {
                changed = true;
                continue;
            }

            removed.put(possibleHeader, values);
        }

        return changed ?
                this.replace(removed) :
                this;
    }

    // setBodyText......................................................................................................

    @Override //
    final HttpEntity setBodyText0(final String bodyText) {
        final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers = this.headers2();

        return bodyText.isEmpty() && headers.isEmpty() ?
                EMPTY :
                this.bodyText().equals(bodyText) ?
                        this :
                        HttpEntityText.with(headers, bodyText);
    }

    // Object..........................................................................................................

    @Override
    public final String toString() {
        final int globalAndValueLength = 32 * 1024;

        final ToStringBuilder b = ToStringBuilder.empty()
                .disable(ToStringBuilderOption.QUOTE)
                .disable(ToStringBuilderOption.SKIP_IF_DEFAULT_VALUE)
                .globalLength(globalAndValueLength)
                .valueLength(globalAndValueLength);

        final String eol = LINE_ENDING.toString();

        // headers
        b.valueSeparator(eol);
        b.labelSeparator(HEADER_NAME_SEPARATOR.string().concat(" "));
        b.value(this.headers());
        b.append(eol).append(eol);

        // body
        b.valueSeparator("");

        this.toStringBody(b);

        return b.build();
    }

    abstract void toStringBody(final ToStringBuilder b);
}
