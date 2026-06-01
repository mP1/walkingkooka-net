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

import walkingkooka.Binary;
import walkingkooka.text.CharSequences;

import java.util.Objects;

/**
 * A {@link MediaTypeDetector} that always returns {@link MediaType#BINARY}.
 */
final class BinaryMediaTypeDetector implements MediaTypeDetector {

    /**
     * Singleton
     */
    final static BinaryMediaTypeDetector INSTANCE = new BinaryMediaTypeDetector();

    private BinaryMediaTypeDetector() {
        super();
    }

    private final MediaType MEDIA_TYPE = MediaType.BINARY;

    @Override
    public MediaType detect(final String filename,
                            final Binary content) {
        CharSequences.failIfNullOrEmpty(filename, "filename");
        Objects.requireNonNull(content, "content");
        return MEDIA_TYPE;
    }

    @Override
    public String toString() {
        return MEDIA_TYPE.toString();
    }
}
