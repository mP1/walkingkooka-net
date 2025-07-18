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

import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.reflect.StaticHelper;
import walkingkooka.text.CharSequences;

import java.util.Objects;

/**
 * Accepts text holding a complete HTTP REQUEST and returns a {@link HttpRequest}.
 */
final class HttpRequestParser implements StaticHelper {

    static HttpRequest parse(final HttpTransport transport,
                             final String request) {
        Objects.requireNonNull(transport, "transport");
        Objects.requireNonNull(request, "request");

        final HttpRequestParser parser = new HttpRequestParser(LineReader.with(request));

        // request line...
        final String[] requestLineTokens = parser.readRequestLine();
        final HttpMethod method = HttpMethod.with(requestLineTokens[0]);
        final RelativeUrl url = Url.parseRelative(requestLineTokens[1]);
        final HttpProtocolVersion version = HttpProtocolVersion.with(requestLineTokens[2]);

        // headers
        final HttpEntity entity = parser.reader.readHeaders();
        final String body = parser.reader.leftOver();

        return HttpRequests.value(
            transport,
            method,
            url,
            version,
            CharSequences.isNullOrEmpty(body) ?
                entity :
                entity.setBodyText(body)
        );
    }

    private HttpRequestParser(final LineReader reader) {
        super();
        this.reader = reader;
    }

    /**
     * Reads the request line and ensures there are three tokens, the METHOD, url and http version.
     */
    private String[] readRequestLine() {
        final String line = this.reader.readLine();
        if (null == line) {
            throw new IllegalArgumentException("Missing request line");
        }
        final String[] tokens = line.split(" ");
        if (3 != tokens.length) {
            throw new IllegalArgumentException("Request line invalid: " + CharSequences.quoteAndEscape(line));
        }
        return tokens;
    }

    private final LineReader reader;

    @Override
    public String toString() {
        return this.reader.toString();
    }
}
