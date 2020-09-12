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

import walkingkooka.net.http.server.WebFile;

import java.nio.charset.Charset;

/**
 * Will be used by javascript using bodyText rather than body bytes for equality tests and copying from a {@link WebFile}.
 */
class HttpEntityInteropJ2cl {

    static final boolean BINARY = false;

    static boolean equalsBody(final HttpEntity left,
                              final HttpEntity right) {
        return left.bodyText().equals(right.bodyText());
    }

    static HttpEntity setBody(final HttpEntity entity,
                              final WebFile file,
                              final Charset defaultCharset) {
        return entity.setBodyText(file.contentText(defaultCharset));
    }
}
