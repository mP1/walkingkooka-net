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

import walkingkooka.HasCharset;
import walkingkooka.net.header.CharsetName;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.text.HasLineEnding;
import walkingkooka.text.LineEnding;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Defines a contract for a container that includes headers, such as a http request.
 */
public interface HasHeaders extends HasCharset,
    HasLineEnding {

    /**
     * The line ending used in http requests/responses.
     */
    LineEnding LINE_ENDING = LineEnding.CRNL;

    @Override
    default LineEnding lineEnding() {
        return LINE_ENDING;
    }

    /**
     * Returns a {@link Map} view of all headers.
     */
    Map<HttpHeaderName<?>, List<?>> headers();

    //https://www.w3.org/International/articles/http-charset/index#:~:text=Documents%20transmitted%20with%20HTTP%20that,is%20ISO%2D8859%2D1.
    Charset CHARSET = CharsetName.ISO_8859_1.charset()
        .get();

    /**
     * Returns the {@link Charset} of this instance default to {@link #CHARSET} when absent.
     */
    @Override
    default Charset charset() {
        return HttpHeaderName.CONTENT_TYPE
            .header(this)
            .map(c -> c.contentTypeCharset(CHARSET))
            .orElse(CHARSET);
    }
}
