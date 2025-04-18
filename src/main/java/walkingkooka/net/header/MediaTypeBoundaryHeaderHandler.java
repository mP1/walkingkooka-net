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

package walkingkooka.net.header;

import walkingkooka.naming.Name;

/**
 * A {@link HeaderHandler} that handles parsing a quoted boundary string into its raw form.<br>
 * <pre>
 * Content-type: multipart/mixed; boundary="abcdefGHIJK"
 * </pre>
 */
final class MediaTypeBoundaryHeaderHandler extends NonStringHeaderHandler<MediaTypeBoundary> {

    /**
     * Singleton
     */
    final static MediaTypeBoundaryHeaderHandler INSTANCE = new MediaTypeBoundaryHeaderHandler();

    /**
     * Private ctor use singleton.
     */
    private MediaTypeBoundaryHeaderHandler() {
        super();
    }

    @Override
    MediaTypeBoundary parse0(final String text) {
        return MediaTypeBoundary.with(STRING_PARSER.parse(text));
    }

    private final HeaderHandler<String> STRING_PARSER = HeaderHandler.quotedUnquotedString(MediaTypeBoundary.QUOTED_CHARACTER_PREDICATE,
        false,
        MediaTypeBoundary.UNQUOTED_CHARACTER_PREDICATE);

    @Override
    void checkNonNull(final Object value) {
        this.checkType(value,
            (v) -> v instanceof MediaTypeBoundary,
            MediaTypeBoundary.class
        );
    }

    /**
     * Delegates to the {@link MediaTypeBoundary#toHeaderText()}.
     */
    @Override
    String toText0(final MediaTypeBoundary boundary, final Name name) {
        return boundary.toHeaderText();
    }

    @Override
    public String toString() {
        return MediaTypeBoundary.class.getSimpleName();
    }
}
