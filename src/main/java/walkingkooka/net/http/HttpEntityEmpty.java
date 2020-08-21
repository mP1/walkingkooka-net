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

import walkingkooka.Binary;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.header.HttpHeaderName;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * A {@link HttpEntity} without headers or body
 */
final class HttpEntityEmpty extends HttpEntity {

    /**
     * Singleton instance.
     */
    final static HttpEntityEmpty instance() {
        if(null == INSTANCE) {
            INSTANCE = new HttpEntityEmpty(); // required because super class extracts singleton
        }
        return INSTANCE;
    }

    private static HttpEntityEmpty INSTANCE;

    /**
     * Private ctor
     */
    private HttpEntityEmpty() {
        super(Maps.empty());
    }

    // headers..........................................................................................................

    @Override
    HttpEntity setHeaders0(final Map<HttpHeaderName<?>, Object> headers) {
        return this.replace(headers);
    }

    @Override
    <T> HttpEntity tryAddHeader(final HttpHeaderName<T> header,
                                final T value) {
        return this.setHeaders0(Maps.of(header, value));
    }

    @Override
    HttpEntity tryRemoveHeader(final HttpHeaderName<?> header) {
        return this; // nop
    }

    final Charset charset() {
        throw new UnsupportedOperationException();
    }

    // body.............................................................................................................

    @Override
    public Binary body() {
        return Binary.EMPTY;
    }

    @Override
    public String bodyText() {
        return "";
    }

    @Override
    HttpEntity setBodyText0(final String bodyText) {
        return bodyText.isEmpty() ?
                this :
                HttpEntityText.with(NO_HEADERS, bodyText);
    }

    @Override
    HttpEntity replace(final Map<HttpHeaderName<?>, Object> headers) {
        return HttpEntityBinaryEnabler.ENABLED ?
                HttpEntityBinary.with(headers, Binary.EMPTY) :
                HttpEntityText.with(Maps.empty(), "");
    }

    @Override
    HttpEntity replace(final Map<HttpHeaderName<?>, Object> headers,
                       final Binary body) {
        return HttpEntityBinary.with(headers, body);
    }

    // toString.........................................................................................................

    @Override
    public String toString() {
        return "";
    }
}
