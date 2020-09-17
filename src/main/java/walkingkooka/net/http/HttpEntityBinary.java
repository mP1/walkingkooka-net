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
import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringBuilderOption;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.header.CharsetName;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.header.MediaTypeParameterName;
import walkingkooka.text.Ascii;
import walkingkooka.text.CharSequences;
import walkingkooka.text.LineEnding;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Optional;

/**
 * A http entity containing headers and body. Note that the content-length is not automatically updated in any factory or
 * setter method.<br>
 * Methods that return a {@link HttpEntityBinary} are marked with @GwtIncompatible
 */
final class HttpEntityBinary extends HttpEntityNotEmpty {

    /**
     * Creates a new {@link HttpEntityBinary}
     */
    // @VisibleForTesting
    static HttpEntityBinary with(final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers,
                                 final Binary body) {
        return new HttpEntityBinary(headers, checkBody(body));
    }

    /**
     * Private ctor
     */
    private HttpEntityBinary(final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers,
                             final Binary body) {
        super(headers);
        this.body = body;
    }

    // headers..........................................................................................................

    @Override
    HttpEntity setHeaders0(final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers) {
        return new HttpEntityBinary(headers, this.body);
    }

    // contentLength....................................................................................................

    @Override
    public long contentLength() {
        return this.body().size();
    }

    // body ............................................................................................................

    @Override
    public Binary body() {
        return this.body;
    }

    private final Binary body;

    // bodyText ........................................................................................................

    public String bodyText() {
        return new String(this.body.value(), this.charset());
    }

    // replace....................................................................................................

    @Override
    HttpEntity replace(final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers) {
        return this.replace(headers, this.body);
    }

    @Override
    HttpEntity replace(final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers,
                             final Binary body) {
        return headers.isEmpty() && this.body.size() == 0L ?
                EMPTY :
                new HttpEntityBinary(Maps.readOnly(headers), body);
    }

    // Object....................................................................................................

    /**
     * The {@link String} produced looks almost like a http entity, each header will appear on a single, a colon separates
     * the header name and value, and a blank line between headers and body, with the body bytes appearing in hex form.
     */
    @Override
    void toStringBody(final ToStringBuilder b) {
        byte[] body = this.body.value();
        final Optional<MediaType> mediaType = HttpHeaderName.CONTENT_TYPE.headerValue(this);
        if (mediaType.isPresent()) {
            final Optional<CharsetName> charsetName = MediaTypeParameterName.CHARSET.parameterValue(mediaType.get());
            if (charsetName.isPresent()) {
                final Optional<Charset> charset = charsetName.get().charset();
                if (charset.isPresent()) {
                    b.value(new String(body, charset.get()));
                    body = null;
                }
            }
        }

        if (null != body) {
            // hex dump
            // offset-HEX_DUMP_WIDTH-chars space hexdigit *HEX_DUMP_WIDTH space separated ascii
            b.enable(ToStringBuilderOption.HEX_BYTES);
            b.valueSeparator(" ");
            b.labelSeparator(" ");

            final int length = body.length;
            for (int i = 0; i < length; i = i + HEX_DUMP_WIDTH) {
                // offset
                b.append(CharSequences.padLeft(Integer.toHexString(i), 8, '0').toString());
                b.append(' ');

                for (int j = 0; j < HEX_DUMP_WIDTH; j++) {
                    final int k = i + j;
                    if (k < length) {
                        b.value(body[k]);
                    } else {
                        b.value("  ");
                    }
                }

                b.append(' ');

                for (int j = 0; j < HEX_DUMP_WIDTH; j++) {
                    final int k = i + j;
                    if (k < length) {
                        final char c = (char) body[k];
                        b.append(Ascii.isPrintable(c) ? c : UNPRINTABLE_CHAR);
                    } else {
                        b.append(' ');
                    }
                }

                b.append(LineEnding.SYSTEM);
            }
        }
    }

    private final static int HEX_DUMP_WIDTH = 16;
    private final static char UNPRINTABLE_CHAR = '.';
}
