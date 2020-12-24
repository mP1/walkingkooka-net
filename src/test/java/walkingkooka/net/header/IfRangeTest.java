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
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class IfRangeTest extends HeaderTestCase<IfRange<?>> implements ParseStringTesting<IfRange<?>> {

    @Test
    public void testWithNullFails() {
        assertThrows(NullPointerException.class, () -> IfRange.with(null));
    }

    @Test
    public void testWithEmptyFails() {
        assertThrows(IllegalArgumentException.class, () -> IfRange.with(""));
    }

    @Test
    @Override
    public void testParseStringEmptyFails() {
        assertThrows(IllegalArgumentException.class, () -> IfRange.parse(""));
    }

    @Test
    public void testIsWildcard() {
        this.isWildcardAndCheck(false);
    }

    @Test
    public void testParseInvalidFails() {
        assertThrows(HeaderException.class, () -> IfRange.parse("\"1234567890abcdef"));
    }

    @Test
    public void testParseETag() {
        final ETag etag = ETagValidator.WEAK.setValue("abc");
        this.parseStringAndCheck(etag.toHeaderText(),
                IfRange.with(etag));
    }

    @Test
    public void testParseLastModified() {
        final LocalDateTime lastModified = LocalDateTime.of(2000, 12, 31, 6, 28, 29);
        this.parseStringAndCheck(HttpHeaderName.LAST_MODIFIED.headerText(lastModified),
                IfRange.with(lastModified));
    }

    // ParseStringTesting ........................................................................................

    @Override
    public IfRange<?> parseString(final String text) {
        return IfRange.parse(text);
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
        return false;
    }

    @Override
    public IfRange<?> createHeader() {
        return IfRange.with(LocalDateTime.of(2000, 12, 31, 6, 28, 29));
    }

    @Override
    public IfRange<?> createDifferentHeader() {
        return IfRange.with(LocalDateTime.of(1999, 1, 2, 0, 58, 59));
    }

    @Override
    public Class<IfRange<?>> type() {
        return Cast.to(IfRange.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
