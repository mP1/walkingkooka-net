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

import walkingkooka.Cast;
import walkingkooka.text.HasTextTesting;

/**
 * Mixin interface with helpers to assist testing of {@link Header} implementations.
 */
public interface HeaderTesting extends HasTextTesting {

    default void isWildcardAndCheck(final Header header,
                                    final boolean expected) {
        this.isWildcardAndCheck0(
            header,
            expected
        );

        final String text = header.text();
        this.isWildcardAndCheck0(
            header,
            String.valueOf(Header.WILDCARD)
                .equals(text) ||
                "*/*".equals(text)
        );
    }

    default void isWildcardAndCheck0(final Header header,
                                     final boolean expected) {
        this.checkEquals(
            expected,
            header.isWildcard(),
            () -> "header " + header
        );
    }

    default void equalsIgnoringParametersAndCheck(final Header header,
                                                  final Object other,
                                                  final boolean expected) {
        this.checkEquals(
            expected,
            header.equalsIgnoringParameters(other),
            () -> header + " equalsIgnoringParameters " + other
        );

        if (other instanceof Header) {
            final Header otherHeader = Cast.to(other);
            this.checkEquals(expected,
                otherHeader.equalsIgnoringParameters(header),
                () -> otherHeader + " equalsIgnoringParameters " + header);
        }
    }

    default void equalsOnlyPresentParametersAndCheck(final Header header,
                                                     final Object other,
                                                     final boolean expected) {
        this.checkEquals(
            expected,
            header.equalsOnlyPresentParameters(other),
            () -> header + " equalsOnlyPresentParameters " + other
        );
    }
}
