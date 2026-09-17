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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.HasTextTesting;

import java.util.Arrays;

/**
 * Mixin interface with helpers to assist testing of {@link Header} implementations.
 */
public interface HeaderTesting2<V extends Header> extends HeaderTesting,
    HasTextTesting,
    HashCodeEqualsDefinedTesting2<V>,
    ToStringTesting<V> {

    @Test
    default void testIsMultipart() {
        this.checkEquals(
            this.isMultipart(),
            this.createHeader()
                .isMultipart()
        );
    }

    @Test
    default void testIsRequest() {
        this.checkEquals(
            this.isRequest(),
            this.createHeader()
                .isRequest()
        );
    }

    @Test
    default void testIsResponse() {
        this.checkEquals(
            this.isResponse(),
            this.createHeader()
                .isResponse()
        );
    }

    @Test
    default void testIsWildcardHeaderText() {
        final V header = this.createHeader();
        this.isWildcardAndCheck(
            header,
            String.valueOf(Header.WILDCARD)
                .equals(header.text())
        );
    }

    boolean isMultipart();

    boolean isRequest();

    boolean isResponse();

    V createHeader();

    V createDifferentHeader();

    //@Override
    default RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        return new HeaderException(
            expected.getMessage(),
            expected
        );
    }

    //@Override
    default Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> expected) {
        return HeaderException.class;
    }

    default void textAndCheck(final String expected) {
        this.textAndCheck(
            this.createHeader(),
            expected
        );
    }

    default void toHeaderTextListAndCheck(final String toString,
                                          final Header... headers) {
        this.checkEquals(
            toString,
            Header.toHeaderTextList(
                Lists.of(headers),
                Header.SEPARATOR.string()
                    .concat(" ")
            ),
            () -> "toHeaderTextList returned wrong toString " + Arrays.toString(headers)
        );
    }

    default void isWildcardAndCheck(final boolean expected) {
        this.isWildcardAndCheck(
            this.createHeader(),
            expected
        );
    }

    // equalsIgnoringParameters.........................................................................................

    @Test
    default void testEqualsIgnoringParametersNullFalse() {
        this.equalsIgnoringParametersAndCheck(
            this.createHeader(),
            null,
            false
        );
    }

    @Test
    default void testEqualsIgnoringParametersInvalidTypeFalse() {
        this.equalsIgnoringParametersAndCheck(
            this.createHeader(),
            this,
            false
        );
    }

    @Test
    default void testEqualsIgnoringParametersDifferent() {
        this.equalsIgnoringParametersAndCheck(
            this.createHeader(),
            this.createDifferentHeader(),
            false
        );
    }

    @Test
    default void testEqualsIgnoringParametersSelfTrue() {
        final V header = this.createHeader();
        this.equalsIgnoringParametersAndCheck(
            header,
            header,
            true
        );
    }

    @Test
    default void testEqualsIgnoringParametersTrue() {
        this.equalsIgnoringParametersAndCheck(
            this.createHeader(),
            this.createHeader(),
            true
        );
    }

    @Override
    default V createObject() {
        return this.createHeader();
    }

    // equalsOnlyPresentParameters......................................................................................

    @Test
    default void testEqualsOnlyPresentParametersNullFalse() {
        this.equalsOnlyPresentParametersAndCheck(
            this.createHeader(),
            null,
            false
        );
    }

    @Test
    default void testEqualsOnlyPresentParametersInvalidTypeFalse() {
        this.equalsOnlyPresentParametersAndCheck(
            this.createHeader(),
            this,
            false
        );
    }

    @Test
    default void testEqualsOnlyPresentParametersSelfTrue() {
        final V header = this.createHeader();
        this.equalsOnlyPresentParametersAndCheck(
            header,
            header,
            true
        );
    }

    @Test
    default void testEqualsOnlyPresentParametersTrue() {
        this.equalsOnlyPresentParametersAndCheck(
            this.createHeader(),
            this.createHeader(),
            true
        );
    }


    @Test
    default void testEqualsOnlyPresentParametersDifferentFalse() {
        this.equalsOnlyPresentParametersAndCheck(
            this.createHeader(),
            this.createDifferentHeader(),
            false
        );
    }
}
