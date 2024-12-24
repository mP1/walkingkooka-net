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

import org.junit.jupiter.api.Test;
import walkingkooka.Binary;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface MediaTypeDetectorTesting2<T extends MediaTypeDetector> extends MediaTypeDetectorTesting {

    @Test
    default void testDetectWithNullFilenameFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createMediaTypeDetector()
                        .detect(
                                null,
                                Binary.EMPTY
                        )
        );
    }

    @Test
    default void testDetectWithEmptyFilenameFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> this.createMediaTypeDetector()
                        .detect(
                                "",
                                Binary.EMPTY
                        )
        );
    }

    @Test
    default void testDetectWithNullBinaryFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createMediaTypeDetector()
                        .detect(
                                "filename",
                                null
                        )
        );
    }

    default void detectAndCheck(final String filename,
                                final Binary content,
                                final MediaType expected) {
        this.detectAndCheck(
                this.createMediaTypeDetector(),
                filename,
                content,
                expected
        );
    }

    T createMediaTypeDetector();
}
