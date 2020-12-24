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
 * Mixin interface with helpers to assist testing of {@link HeaderValue} implementations.
 */
public interface HeaderValueTesting<V extends HeaderValue> extends HashCodeEqualsDefinedTesting2<V>,
        ToStringTesting<V> {

    @Test
    default void testIsMultipart() {
        assertEquals(this.isMultipart(), this.createHeaderValue().isMultipart());
    }

    @Test
    default void testIsRequest() {
        assertEquals(this.isRequest(), this.createHeaderValue().isRequest());
    }

    @Test
    default void testIsResponse() {
        assertEquals(this.isResponse(), this.createHeaderValue().isResponse());
    }

    @Test
    default void testIsWildcardHeaderText() {
        final V header = this.createHeaderValue();
        this.isWildcardAndCheck(header, String.valueOf(HeaderValue.WILDCARD).equals(header.toHeaderText()));
    }

    boolean isMultipart();

    boolean isRequest();

    boolean isResponse();

    V createHeaderValue();

    //@Override
    default RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        return new HeaderValueException(expected.getMessage(), expected);
    }

    //@Override
    default Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> expected) {
        return HeaderValueException.class;
    }

    default void toHeaderTextAndCheck(final String expected) {
        this.toHeaderTextAndCheck(this.createHeaderValue(), expected);
    }

    default void toHeaderTextAndCheck(final HeaderValue headerValue, final String expected) {
        assertEquals(expected, headerValue.toHeaderText(), () -> "headerText of " + headerValue);
    }

    default void toHeaderTextListAndCheck(final String toString,
                                          final HeaderValue... headerValues) {
        assertEquals(toString,
                HeaderValue.toHeaderTextList(Lists.of(headerValues), HeaderValue.SEPARATOR.string().concat(" ")),
                () -> "toHeaderTextList returned wrong toString " + Arrays.toString(headerValues));
    }

    default void isWildcardAndCheck(final boolean expected) {
        this.isWildcardAndCheck(this.createHeaderValue(), expected);
    }

    default void isWildcardAndCheck(final HeaderValue headerValue, final boolean expected) {
        this.isWildcardAndCheck0(headerValue, expected);

        final String text = headerValue.toHeaderText();
        this.isWildcardAndCheck0(headerValue, String.valueOf(HeaderValue.WILDCARD).equals(text) || "*/*".equals(text));
    }

    default void isWildcardAndCheck0(final HeaderValue headerValue, final boolean expected) {
        assertEquals(expected, headerValue.isWildcard(), () -> "header " + headerValue);
    }

    // equalsIgnoringParameters.........................................................................................

    @Test
    default void testEqualsIgnoringParametersNullFalse() {
        equalsIgnoringParametersAndCheck(
                this.createHeaderValue(),
                null,
                false
        );
    }

    @Test
    default void testEqualsIgnoringParametersInvalidTypeFalse() {
        equalsIgnoringParametersAndCheck(
                this.createHeaderValue(),
                this,
                false
        );
    }

    @Test
    default void testEqualsIgnoringParametersSelfTrue() {
        final V header = this.createHeaderValue();
        equalsIgnoringParametersAndCheck(
                header,
                header,
                true
        );
    }

    @Test
    default void testEqualsIgnoringParametersTrue() {
        equalsIgnoringParametersAndCheck(
                this.createHeaderValue(),
                this.createHeaderValue(),
                true
        );
    }

    default void equalsIgnoringParametersAndCheck(final HeaderValue header,
                                                  final Object other,
                                                  final boolean expected) {
        assertEquals(expected,
                header.equalsIgnoringParameters(other),
                () -> header + " equalsIgnoringParameters " + other);

        if (other instanceof HeaderValue) {
            final HeaderValue otherHeader = Cast.to(other);
            assertEquals(expected,
                    otherHeader.equalsIgnoringParameters(header),
                    () -> otherHeader + " equalsIgnoringParameters " + header);
        }
    }

    // equalsOnlyPresentParameters.........................................................................................

    @Test
    default void testEqualsOnlyPresentParametersNullFalse() {
        equalsOnlyPresentParametersAndCheck(
                this.createHeaderValue(),
                null,
                false
        );
    }

    @Test
    default void testEqualsOnlyPresentParametersInvalidTypeFalse() {
        equalsOnlyPresentParametersAndCheck(
                this.createHeaderValue(),
                this,
                false
        );
    }

    @Test
    default void testEqualsOnlyPresentParametersSelfTrue() {
        final V header = this.createHeaderValue();
        equalsOnlyPresentParametersAndCheck(
                header,
                header,
                true
        );
    }

    @Test
    default void testEqualsOnlyPresentParametersTrue() {
        equalsOnlyPresentParametersAndCheck(
                this.createHeaderValue(),
                this.createHeaderValue(),
                true
        );
    }

    default void equalsOnlyPresentParametersAndCheck(final HeaderValue header,
                                                     final Object other,
                                                     final boolean expected) {
        assertEquals(expected,
                header.equalsOnlyPresentParameters(other),
                () -> header + " equalsOnlyPresentParameters " + other);
    }

    @Override
    default V createObject() {
        return this.createHeaderValue();
    }
}
