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
    HttpEntity replaceHeaders(final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers) {
        return this.replace(
            headers,
            this.body
        );
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

    @Override
    public String bodyText() {
        return new String(this.body.value(), this.charset());
    }

    // replace..........................................................................................................

    @Override
    HttpEntity replace(final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers,
                       final Binary body) {
        return headers.isEmpty() && this.body.isEmpty() ?
            EMPTY :
            new HttpEntityBinary(Maps.readOnly(headers), body);
    }
}
