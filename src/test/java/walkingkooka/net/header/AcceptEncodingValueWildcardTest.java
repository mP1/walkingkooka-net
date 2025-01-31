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

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class AcceptEncodingValueWildcardTest extends AcceptEncodingValueTestCase<AcceptEncodingValueWildcard> {

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

    // predicate........................................................................................................

    @Test
    public void testTestContentEncodingGzip() {
        this.testTrue(ContentEncoding.GZIP);
    }

    @Test
    public void testTestContentEncodingDeflate() {
        this.testTrue(ContentEncoding.DEFLATE);
    }

    @Test
    public void testTestContentEncoding() {
        this.testTrue(ContentEncoding.parse("xyz"));
    }

    // parse............................................................................................................

    @Test
    public void testParseWildcard() {
        this.parseStringAndCheck("*",
            AcceptEncodingValue.WILDCARD_ENCODING);
    }

    @Test
    public void testParseWhitespaceTokenWhitespace() {
        this.parseStringAndCheck(" * ",
            AcceptEncodingValue.WILDCARD_ENCODING);
    }

    // equalsIgnoringParameters.........................................................................................

    @Test
    public void testEqualsIgnoringParametersDifferent() {
        this.equalsIgnoringParametersAndCheck(
            AcceptEncodingValue.WILDCARD_ENCODING,
            AcceptEncodingValue.GZIP,
            false);
    }

    @Test
    public void testEqualsIgnoringParametersDifferentParameters() {
        this.equalsIgnoringParametersAndCheck(
            AcceptEncodingValue.parse("*;q=1.0"),
            AcceptEncodingValue.parse("*;q=0.5"),
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
            AcceptEncodingValue.parse("*;q=1.0"),
            AcceptEncodingValue.parse("*;q=0.5"),
            false);
    }

    @Test
    public void testEqualsOnlyPresentParametersDifferentParameters2() {
        this.equalsOnlyPresentParametersAndCheck(
            AcceptEncodingValue.parse("*;q=1.0;parameter-2=value2"),
            AcceptEncodingValue.parse("*;q=1.0"),
            false);
    }

    @Test
    public void testEqualsOnlyPresentParametersSharedParameters() {
        this.equalsOnlyPresentParametersAndCheck(
            AcceptEncodingValue.parse("*;q=1.0"),
            AcceptEncodingValue.parse("*;q=1.0"),
            true);
    }

    @Test
    public void testEqualsOnlyPresentParametersSharedAndIgnoredParameters() {
        this.equalsOnlyPresentParametersAndCheck(
            AcceptEncodingValue.parse("*;q=1.0"),
            AcceptEncodingValue.parse("*;q=1.0;parameter-2=value2"),
            true);
    }

    // helpers..........................................................................................................

    @Override
    public AcceptEncodingValueWildcard createHeaderWithParameters() {
        return AcceptEncodingValueWildcard.INSTANCE;
    }

    @Override
    String value() {
        return "*";
    }

    @Override
    public AcceptEncodingValue createDifferentHeader() {
        return AcceptEncodingValue.with("BR");
    }

    @Override
    Class<AcceptEncodingValueWildcard> encodingType() {
        return AcceptEncodingValueWildcard.class;
    }
}
