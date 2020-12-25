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
import walkingkooka.Cast;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Mixin interface with helpers to assist testing of {@link Header} implementations.
 */
public interface HeaderTesting<V extends Header> extends HashCodeEqualsDefinedTesting2<V>,
        ToStringTesting<V> {

    @Test
    default void testIsMultipart() {
        assertEquals(this.isMultipart(), this.createHeader().isMultipart());
    }

    @Test
    default void testIsRequest() {
        assertEquals(this.isRequest(), this.createHeader().isRequest());
    }

    @Test
    default void testIsResponse() {
        assertEquals(this.isResponse(), this.createHeader().isResponse());
    }

    @Test
    default void testIsWildcardHeaderText() {
        final V header = this.createHeader();
        this.isWildcardAndCheck(header, String.valueOf(Header.WILDCARD).equals(header.toHeaderText()));
    }

    boolean isMultipart();

    boolean isRequest();

    boolean isResponse();

    V createHeader();

    V createDifferentHeader();

    //@Override
    default RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        return new HeaderException(expected.getMessage(), expected);
    }

    //@Override
    default Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> expected) {
        return HeaderException.class;
    }

    default void toHeaderTextAndCheck(final String expected) {
        this.toHeaderTextAndCheck(this.createHeader(), expected);
    }

    default void toHeaderTextAndCheck(final Header header, final String expected) {
        assertEquals(expected, header.toHeaderText(), () -> "headerText of " + header);
    }

    default void toHeaderTextListAndCheck(final String toString,
                                          final Header... headers) {
        assertEquals(toString,
                Header.toHeaderTextList(Lists.of(headers), Header.SEPARATOR.string().concat(" ")),
                () -> "toHeaderTextList returned wrong toString " + Arrays.toString(headers));
    }

    default void isWildcardAndCheck(final boolean expected) {
        this.isWildcardAndCheck(this.createHeader(), expected);
    }

    default void isWildcardAndCheck(final Header header, final boolean expected) {
        this.isWildcardAndCheck0(header, expected);

        final String text = header.toHeaderText();
        this.isWildcardAndCheck0(header, String.valueOf(Header.WILDCARD).equals(text) || "*/*".equals(text));
    }

    default void isWildcardAndCheck0(final Header header, final boolean expected) {
        assertEquals(expected, header.isWildcard(), () -> "header " + header);
    }

    // equalsIgnoringParameters.........................................................................................

    @Test
    default void testEqualsIgnoringParametersNullFalse() {
        equalsIgnoringParametersAndCheck(
                this.createHeader(),
                null,
                false
        );
    }

    @Test
    default void testEqualsIgnoringParametersInvalidTypeFalse() {
        equalsIgnoringParametersAndCheck(
                this.createHeader(),
                this,
                false
        );
    }

    @Test
    default void testEqualsIgnoringParametersDifferent() {
        equalsIgnoringParametersAndCheck(
                this.createHeader(),
                this.createDifferentHeader(),
                false
        );
    }

    @Test
    default void testEqualsIgnoringParametersSelfTrue() {
        final V header = this.createHeader();
        equalsIgnoringParametersAndCheck(
                header,
                header,
                true
        );
    }

    @Test
    default void testEqualsIgnoringParametersTrue() {
        equalsIgnoringParametersAndCheck(
                this.createHeader(),
                this.createHeader(),
                true
        );
    }

    default void equalsIgnoringParametersAndCheck(final Header header,
                                                  final Object other,
                                                  final boolean expected) {
        assertEquals(expected,
                header.equalsIgnoringParameters(other),
                () -> header + " equalsIgnoringParameters " + other);

        if (other instanceof Header) {
            final Header otherHeader = Cast.to(other);
            assertEquals(expected,
                    otherHeader.equalsIgnoringParameters(header),
                    () -> otherHeader + " equalsIgnoringParameters " + header);
        }
    }

    // equalsOnlyPresentParameters.........................................................................................

    @Test
    default void testEqualsOnlyPresentParametersNullFalse() {
        equalsOnlyPresentParametersAndCheck(
                this.createHeader(),
                null,
                false
        );
    }

    @Test
    default void testEqualsOnlyPresentParametersInvalidTypeFalse() {
        equalsOnlyPresentParametersAndCheck(
                this.createHeader(),
                this,
                false
        );
    }

    @Test
    default void testEqualsOnlyPresentParametersSelfTrue() {
        final V header = this.createHeader();
        equalsOnlyPresentParametersAndCheck(
                header,
                header,
                true
        );
    }

    @Test
    default void testEqualsOnlyPresentParametersTrue() {
        equalsOnlyPresentParametersAndCheck(
                this.createHeader(),
                this.createHeader(),
                true
        );
    }


    @Test
    default void testEqualsOnlyPresentParametersDifferentFalse() {
        equalsOnlyPresentParametersAndCheck(
                this.createHeader(),
                this.createDifferentHeader(),
                false
        );
    }

    default void equalsOnlyPresentParametersAndCheck(final Header header,
                                                     final Object other,
                                                     final boolean expected) {
        assertEquals(expected,
                header.equalsOnlyPresentParameters(other),
                () -> header + " equalsOnlyPresentParameters " + other);
    }

    @Override
    default V createObject() {
        return this.createHeader();
    }
}
