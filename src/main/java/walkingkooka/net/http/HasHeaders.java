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

import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.text.LineEnding;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Defines a contract for a container that includes headers, such as a http request.
 */
public interface HasHeaders {

    /**
     * The line ending used in http requests/responses.
     */
    LineEnding LINE_ENDING = LineEnding.CRNL;

    /**
     * Returns a {@link Map} view of all headers.
     */
    Map<HttpHeaderName<?>, List<?>> headers();

    /**
     * Returns the {@link Charset} using the {@link HttpHeaderName#CONTENT_TYPE} or the default if absent.
     */
    default Charset charset(final Charset defaultCharset) {
        Objects.requireNonNull(defaultCharset, "defaultCharset");

        return HttpHeaderName.CONTENT_TYPE
                .header(this)
                .map(c -> c.contentTypeCharset(defaultCharset))
                .orElse(defaultCharset);
    }
}
