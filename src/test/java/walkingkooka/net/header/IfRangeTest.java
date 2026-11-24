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
import walkingkooka.datetime.HasNowTesting;
import walkingkooka.reflect.PublicClassTesting;
import walkingkooka.test.ParseStringTesting;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class IfRangeTest extends HeaderTestCase<IfRange<?>> implements ParseStringTesting<IfRange<?>>,
    PublicClassTesting<IfRange<?>>,
    HasNowTesting {

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> IfRange.with(null)
        );
    }

    @Test
    public void testWithEmptyFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> IfRange.with("")
        );
    }

    @Test
    public void testIsWildcard() {
        this.isWildcardAndCheck(false);
    }

    @Test
    @Override
    public void testParseStringEmptyFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> IfRange.parse("")
        );
    }

    @Test
    public void testParseInvalidFails() {
        assertThrows(
            HeaderException.class,
            () -> IfRange.parse("\"1234567890abcdef")
        );
    }

    @Test
    public void testParseETag() {
        final ETag etag = ETagValidator.WEAK.setValue("abc");

        this.parseStringAndCheck(
            etag.text(),
            IfRange.with(etag)
        );
    }

    @Test
    public void testParseLastModified() {
        final LocalDateTime lastModified = LocalDateTime.of(2000, 12, 31, 6, 28, 29);
        this.parseStringAndCheck(
            HttpHeaderName.LAST_MODIFIED.headerText(lastModified),
            IfRange.with(lastModified)
        );
    }

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
        return IfRange.with(NOW);
    }

    @Override
    public IfRange<?> createDifferentHeader() {
        return IfRange.with(DIFFERENT_NOW);
    }

    @Override
    public Class<IfRange<?>> type() {
        return Cast.to(IfRange.class);
    }
}
