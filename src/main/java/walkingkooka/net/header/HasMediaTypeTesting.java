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

import walkingkooka.test.Testing;

import java.util.function.Supplier;

public interface HasMediaTypeTesting extends Testing {

    default void mediaTypeAndCheck(final HasMediaType has,
                                   final MediaType expected) {
        this.mediaTypeAndCheck(
                has,
                expected,
                () -> has.toString()
        );
    }

    default void mediaTypeAndCheck(final HasMediaType has,
                                   final MediaType expected,
                                   final String message) {
        this.mediaTypeAndCheck(
                has,
                expected,
                () -> message
        );
    }

    default void mediaTypeAndCheck(final HasMediaType has,
                                   final MediaType expected,
                                   final Supplier<String> message) {
        this.checkEquals(
                expected,
                has.mediaType(),
                message
        );
    }
}