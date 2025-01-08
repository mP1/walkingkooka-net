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

import java.util.Map;

/**
 * This {@link HttpEntity} is only used by transpiled javascript.
 */
final class HttpEntityText extends HttpEntityNotEmpty {

    static HttpEntityText with(final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers,
                               final String text) {
        return new HttpEntityText(headers, text);
    }

    private HttpEntityText(final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers,
                           final String text) {
        super(headers);
        this.text = text;
    }

    @Override
    HttpEntity replaceHeaders(final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers) {
        final String text = this.bodyText();
        return headers.isEmpty() && text.isEmpty() ?
            EMPTY :
            new HttpEntityText(Maps.readOnly(headers), text);
    }

    // contentLength....................................................................................................

    /**
     * Measures the encoded byte length of the body text.
     */
    @Override
    public long contentLength() {
        return this.bodyText().getBytes(this.charset()).length;
    }

    @Override
    public Binary body() {
        return Binary.with(this.text.getBytes(this.charset()));
    }

    @Override
    public String bodyText() {
        return this.text;
    }

    private final String text;

    @Override
    HttpEntity replace(final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers,
                       final Binary body) {
        return headers.isEmpty() && this.bodyText().isEmpty() ?
            EMPTY :
            HttpEntityBinary.with(headers, body);
    }
}
