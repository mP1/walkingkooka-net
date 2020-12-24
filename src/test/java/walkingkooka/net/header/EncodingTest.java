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
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class EncodingTest extends HeaderTestCase<Encoding>
        implements ComparableTesting2<Encoding>,
        ParseStringTesting<Encoding> {

    // constants ......................................................................................................

    @Test
    public void testWithExistingGzipNoParametersConstant() {
        final Encoding constant = Encoding.GZIP;
        assertSame(constant, Encoding.with("gzip"));
    }

    @Test
    public void testWithExistingBrConstant() {
        final Encoding constant = Encoding.BR;
        assertSame(constant, Encoding.with("BR"));
    }

    @Test
    public void testWithExistingCompressConstantCaseInsignificant() {
        final Encoding constant = Encoding.COMPRESS;
        assertSame(constant, Encoding.with("compRESS"));
    }

    // parse............................................................................................................

    @Test
    public void testParse() {
        this.parseStringAndCheck("gzip",
                Encoding.GZIP);
    }

    @Test
    public void testParseExtraWhitespaceFails() {
        this.parseStringFails("gzip ", InvalidCharacterException.class);
    }

    @Test
    public void testParseTokenParametersFails() {
        this.parseStringFails("abc;qrs=xyz", InvalidCharacterException.class);
    }

    @Test
    public void testHeaderText2() {
        final String text = "identity";
        this.toHeaderTextAndCheck(Encoding.with(text), text);
    }

    // equals ..........................................................................................................

    @Test
    public void testEqualsDifferentValue() {
        this.checkNotEquals(Encoding.with("different"));
    }

    // Comparison ......................................................................................................

    @Test
    public void testCompareLess() {
        this.compareToAndCheckLess(Encoding.DEFLATE, Encoding.GZIP);
    }

    @Test
    public void testCompareLessCaseInsignificant() {
        this.compareToAndCheckLess(Encoding.with("abc"),
                Encoding.with("XYZ"));
    }

    // toString ........................................................................................................

    @Test
    public void testToString2() {
        final String text = "compress";
        this.toStringAndCheck(Encoding.with(text), text);
    }

    // helper.. ........................................................................................................

    @Override
    public Encoding createHeader() {
        return this.createHeader("CUSTOM");
    }

    private Encoding createHeader(final String value) {
        return Encoding.with(value);
    }

    @Override
    public Encoding createDifferentHeader() {
        return this.createHeader("different");
    }

    @Override
    public boolean isMultipart() {
        return false;
    }

    @Override
    public boolean isRequest() {
        return false;
    }

    @Override
    public boolean isResponse() {
        return true;
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<Encoding> type() {
        return Encoding.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // ComparableTesting................................................................................................

    @Override
    public Encoding createComparable() {
        return this.createHeader();
    }

    @Override
    public Encoding createObject() {
        return this.createHeader();
    }

    // ParseStringTesting................................................................................................

    @Override
    public Encoding parseString(final String text) {
        return Encoding.parse(text);
    }
}
