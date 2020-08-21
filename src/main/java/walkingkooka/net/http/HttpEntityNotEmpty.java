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

/**
 * A {@link HttpEntity} that captures the common functionalitu of {@link HttpEntityBinary} and {@link HttpEntityText}.
 */
abstract class HttpEntityNotEmpty extends HttpEntity {

    /**
     * Package private
     */
    HttpEntityNotEmpty(final Map<HttpHeaderName<?>, Object> headers) {
        super(headers);
    }

    // headers..........................................................................................................

    @Override//
    final <T> HttpEntity tryAddHeader(final HttpHeaderName<T> header,
                                      final T value) {
        return value.equals(this.headers().get(header)) ?
                this :
                this.addHeader0(header, value);
    }

    private <T> HttpEntity addHeader0(final HttpHeaderName<T> header,
                                      final T value) {
        final Map<HttpHeaderName<?>, Object> updated = Maps.ordered();
        updated.putAll(this.headers());
        updated.put(header, value);
        return this.replace(Maps.readOnly(updated));
    }

    @Override//
    final HttpEntity tryRemoveHeader(final HttpHeaderName<?> header) {
        return this.headers().containsKey(header) ?
                this.removeHeader0(header) :
                this;
    }

    private HttpEntity removeHeader0(final HttpHeaderName<?> header) {
        final Map<HttpHeaderName<?>, Object> without = Maps.ordered();
        this.headers().forEach((h, v) -> {
            if (false == h.equals(header)) {
                without.put(h, v);
            }
        });
        return this.replace(without);
    }

    final Charset charset() {
        return HttpHeaderName.CONTENT_TYPE.parameterValue(this.headers())
                .map(c -> c.contentTypeCharset(DEFAULT_BODY_CHARSET))
                .orElse(DEFAULT_BODY_CHARSET);
    }

    // setBodyText......................................................................................................

    @Override
    final HttpEntity setBodyText0(final String bodyText) {
        final Map<HttpHeaderName<?>, Object> headers = this.headers();

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
