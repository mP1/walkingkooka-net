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
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class RangeHeaderUnitTest extends HeaderTestCase<RangeHeaderUnit>
    implements ParseStringTesting<RangeHeaderUnit> {

    // isWildcard ..................................................................................................

    @Test
    public void testIsWildcard() {
        this.isWildcardAndCheck(false);
    }

    // parse ..................................................................................................

    @Test
    public void testParseUnknownFails() {
        this.parseStringFails("unknown", IllegalArgumentException.class);
    }

    @Test
    public void testParseBytes() {
        assertSame(RangeHeaderUnit.BYTES, RangeHeaderUnit.parse("bytes"));
    }

    @Test
    public void testParseBytesCaseUnimportant() {
        assertSame(RangeHeaderUnit.BYTES, RangeHeaderUnit.parse("BYtes"));
    }

    @Test
    public void testParseNone() {
        assertSame(RangeHeaderUnit.NONE, RangeHeaderUnit.parse("none"));
    }

    // ParseStringTesting ........................................................................................

    @Override
    public RangeHeaderUnit parseString(final String text) {
        return RangeHeaderUnit.parse(text);
    }

    @Override
    public RangeHeaderUnit createHeader() {
        return RangeHeaderUnit.BYTES;
    }

    @Override
    public RangeHeaderUnit createDifferentHeader() {
        return RangeHeaderUnit.NONE;
    }

    @Override
    public boolean isMultipart() {
        return false;
    }

    @Override
    public boolean isRequest() {
        return true;
    }

    @Override
    public boolean isResponse() {
        return true;
    }

    @Override
    public Class<RangeHeaderUnit> type() {
        return RangeHeaderUnit.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
