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
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.map.Maps;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class AcceptEncodingValueNonWildcardTest extends AcceptEncodingValueTestCase<AcceptEncodingValueNonWildcard> {

    @Test
    public void testWithNullValueFails() {
        assertThrows(NullPointerException.class, () -> AcceptEncodingValue.with(null));
    }

    @Test
    public void testWithEmptyValueFails() {
        assertThrows(IllegalArgumentException.class, () -> AcceptEncodingValue.with(""));
    }

    @Test
    public void testWithInvalidCharacterFails() {
        assertThrows(InvalidCharacterException.class, () -> AcceptEncodingValue.with("\u001f"));
    }

    @Test
    public void testWith() {
        this.checkValue(this.createHeader());
    }

    @Test
    public void testWith2() {
        final String text = "unknown";
        this.checkValue(AcceptEncodingValue.with(text),
            text,
            AcceptEncodingValue.NO_PARAMETERS);
    }

    // constants ......................................................................................................

    @Test
    public void testWithExistingGzipNoParametersConstant() {
        final AcceptEncodingValue constant = AcceptEncodingValue.GZIP;
        assertSame(constant, AcceptEncodingValue.with("gzip"));
    }

    @Test
    public void testWithExistingBrConstant() {
        final AcceptEncodingValue constant = AcceptEncodingValue.BR;
        assertSame(constant, AcceptEncodingValue.with("BR"));
    }

    @Test
    public void testWithExistingCompressConstantCaseInsignificant() {
        final AcceptEncodingValue constant = AcceptEncodingValue.COMPRESS;
        assertSame(constant, AcceptEncodingValue.with("compRESS"));
    }

    // test.............................................................................................................

    @Test
    public void testTestContentEncodingSame() {
        this.testTrue(AcceptEncodingValueNonWildcard.with("GZIP"),
            ContentEncoding.GZIP);
    }

    @Test
    public void testTestContentEncodingDifferentCase() {
        this.testTrue(AcceptEncodingValueNonWildcard.with("XYZ"),
            ContentEncoding.parse("xyz"));
    }

    @Test
    public void testTestContentEncodingSameIgnoresParameters() {
        this.testTrue(AcceptEncodingValueNonWildcard.with("XYZ").setParameters(Maps.of(AcceptEncodingValueParameterName.Q, 0.5f)),
            ContentEncoding.parse("XYZ"));
    }

    @Test
    public void testTestContentEncodingDifferent() {
        this.testFalse(AcceptEncodingValueNonWildcard.with("DEFLATE"), ContentEncoding.GZIP);
    }

    // parse............................................................................................................

    @Test
    public void testParse() {
        this.parseStringAndCheck("gzip",
            AcceptEncodingValue.GZIP);
    }

    @Test
    public void testParseExtraWhitespace() {
        this.parseStringAndCheck("gzip ",
            AcceptEncodingValue.GZIP);
    }

    @Test
    public void testParseTokenParameters() {
        this.parseStringAndCheck("abc;qrs=xyz",
            AcceptEncodingValue.with("abc").setParameters(Maps.of(AcceptEncodingValueParameterName.with("qrs"), "xyz")));
    }

    @Test
    public void testHeaderText2() {
        final String text = "identity";
        this.toHeaderTextAndCheck(AcceptEncodingValue.with(text), text);
    }

    // equals ..........................................................................................................

    @Test
    public void testEqualsDifferentValue() {
        this.checkNotEquals(AcceptEncodingValue.with("different"));
    }

    // Comparison ......................................................................................................

    @Test
    public void testCompareLess() {
        this.compareToAndCheckLess(AcceptEncodingValue.DEFLATE, AcceptEncodingValue.GZIP);
    }

    @Test
    public void testCompareLessCaseInsignificant() {
        this.compareToAndCheckLess(AcceptEncodingValue.with("abc"),
            AcceptEncodingValue.with("XYZ"));
    }

    // equalsIgnoringParameters.........................................................................................

    @Test
    public void testEqualsIgnoringParametersDifferent() {
        this.equalsIgnoringParametersAndCheck(
            AcceptEncodingValue.BR,
            AcceptEncodingValue.GZIP,
            false);
    }

    @Test
    public void testEqualsIgnoringParametersDifferentParameters() {
        this.equalsIgnoringParametersAndCheck(
            AcceptEncodingValue.parse("BR;q=1.0"),
            AcceptEncodingValue.parse("BR;q=0.5"),
            true);
    }

    // equalsOnlyPresentParameters.........................................................................................

    @Test
    public void testEqualsOnlyPresentParametersDifferent() {
        this.equalsOnlyPresentParametersAndCheck(
            AcceptEncodingValue.BR,
            AcceptEncodingValue.GZIP,
            false);
    }

    @Test
    public void testEqualsOnlyPresentParametersDifferentParameters() {
        this.equalsOnlyPresentParametersAndCheck(
            AcceptEncodingValue.parse("BR;q=1.0"),
            AcceptEncodingValue.parse("BR;q=0.5"),
            false);
    }

    @Test
    public void testEqualsOnlyPresentParametersDifferentParameters2() {
        this.equalsOnlyPresentParametersAndCheck(
            AcceptEncodingValue.parse("BR;q=1.0;parameter-2=value2"),
            AcceptEncodingValue.parse("BR;q=1.0"),
            false);
    }

    @Test
    public void testEqualsOnlyPresentParametersSharedParameters() {
        this.equalsOnlyPresentParametersAndCheck(
            AcceptEncodingValue.parse("BR;q=1.0"),
            AcceptEncodingValue.parse("BR;q=1.0"),
            true);
    }

    @Test
    public void testEqualsOnlyPresentParametersSharedAndIgnoredParameters() {
        this.equalsOnlyPresentParametersAndCheck(
            AcceptEncodingValue.parse("BR;q=1.0"),
            AcceptEncodingValue.parse("BR;q=1.0;parameter-2=value2"),
            true);
    }

    // toString ........................................................................................................

    @Test
    public void testToString2() {
        final String text = "compress";
        this.toStringAndCheck(AcceptEncodingValue.with(text), text);
    }

    @Override
    public AcceptEncodingValueNonWildcard createHeaderWithParameters() {
        return this.createHeader(this.value());
    }

    private AcceptEncodingValueNonWildcard createHeader(final String value) {
        return AcceptEncodingValue.nonWildcard(value, AcceptEncodingValueNonWildcard.NO_PARAMETERS);
    }

    @Override
    String value() {
        return "gzip";
    }

    @Override
    public AcceptEncodingValueNonWildcard createDifferentHeader() {
        return this.createHeader("different");
    }

    @Override
    Class<AcceptEncodingValueNonWildcard> encodingType() {
        return AcceptEncodingValueNonWildcard.class;
    }
}
