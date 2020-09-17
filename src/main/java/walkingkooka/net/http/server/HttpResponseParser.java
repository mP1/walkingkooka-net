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
import walkingkooka.net.http.HttpStatusCode;
import walkingkooka.reflect.StaticHelper;
import walkingkooka.text.CharSequences;

import java.util.Objects;

/**
 * Accepts text holding a complete HTTP RESPONSE and returns a {@link HttpResponse}.
 * <a href="https://en.wikipedia.org/wiki/Hypertext_Transfer_Protocol#Server_response">HTTP</a>
 */
final class HttpResponseParser implements StaticHelper {

    static HttpResponse parse(final String text) {
        Objects.requireNonNull(text, "text");

        final LineReader reader = LineReader.with(text);
        final HttpResponseParser parser = new HttpResponseParser(reader);

        // response version and status...
        final String line = reader.readLine();
        if (null == line) {
            throw new IllegalArgumentException("Missing version and status");
        }

        final int firstSpace = line.indexOf(' ');
        if (-1 == firstSpace) {
            throw new IllegalArgumentException("Invalid status " + CharSequences.quoteAndEscape(line));
        }

        final int secondSpace = line.indexOf(' ', firstSpace + 1);
        if (-1 == secondSpace) {
            throw new IllegalArgumentException("Invalid status " + CharSequences.quoteAndEscape(line));
        }

        final HttpProtocolVersion version = HttpProtocolVersion.with(line.substring(0, firstSpace));

        final HttpStatusCode statusCode;
        try {
            statusCode = HttpStatusCode.withCode(Integer.parseInt(line.substring(firstSpace + 1, secondSpace)));
        } catch (final NumberFormatException cause) {
            throw new IllegalArgumentException("Invalid status code " + CharSequences.quoteAndEscape(line));
        }
        final HttpStatus status = statusCode.setMessage(line.substring(secondSpace + 1));

        // headers
        final HttpEntity entity = parser.reader.readHeaders();
        final String body = parser.reader.leftOver();

        final HttpResponse response = HttpResponses.recording();
        response.setVersion(version);
        response.setStatus(status);

        final boolean notEmptyBody = false == CharSequences.isNullOrEmpty(body);
        if (false == entity.isEmpty() || notEmptyBody) {
            response.addEntity(notEmptyBody ?
                    entity.setBodyText(body) :
                    entity);
        }

        return response;
    }

    private HttpResponseParser(final LineReader reader) {
        super();
        this.reader = reader;
    }

    private final LineReader reader;

    @Override
    public String toString() {
        return this.reader.toString();
    }
}
