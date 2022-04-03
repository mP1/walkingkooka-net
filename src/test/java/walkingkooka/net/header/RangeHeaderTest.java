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
import walkingkooka.collect.Range;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class RangeHeaderTest extends HeaderTestCase<RangeHeader>
        implements ParseStringTesting<RangeHeader> {

    private final static RangeHeaderUnit UNIT = RangeHeaderUnit.BYTES;

    // with.......................................................

    @Test
    public void testWithNullUnitFails() {
        assertThrows(NullPointerException.class, () -> RangeHeader.with(null, ranges()));
    }

    @Test
    public void testWithNullRangesFails() {
        assertThrows(NullPointerException.class, () -> RangeHeader.with(UNIT, null));
    }

    @Test
    public void testWithEmptyRangesFails() {
        assertThrows(HeaderException.class, () -> RangeHeader.with(UNIT, Lists.empty()));
    }

    @Test
    public void testWithOverlappingRangesFails() {
        assertThrows(HeaderException.class, () -> RangeHeader.with(UNIT, this.rangesWithOverlap()));
    }

    @Test
    public void testWithOverlappingRangesFails2() {
        assertThrows(HeaderException.class, () -> RangeHeader.with(UNIT, this.rangesWithOverlap2()));
    }

    @Test
    public void testWithOverlappingRangesFails3() {
        assertThrows(HeaderException.class, () -> RangeHeader.with(UNIT, this.rangesWithOverlap3()));
    }

    @Test
    public void testWith() {
        final RangeHeader range = this.range();
        this.check(range, UNIT, this.ranges());
    }

    // setUnit.......................................................

    @Test
    public void testSetUnitNullFails() {
        assertThrows(NullPointerException.class, () -> this.range().setUnit(null));
    }

    @Test
    public void testSetUnitNoneFails() {
        assertThrows(IllegalArgumentException.class, () -> this.range().setUnit(RangeHeaderUnit.NONE));
    }

    @Test
    public void testSetUnitSame() {
        final RangeHeader range = this.range();
        assertSame(range, range.setUnit(UNIT));
    }

    // setValue.......................................................

    @Test
    public void testSetValueNullFails() {
        assertThrows(NullPointerException.class, () -> this.range().setValue(null));
    }

    @Test
    public void testSetValueEmptyFails() {
        assertThrows(HeaderException.class, () -> this.range().setValue(Lists.empty()));
    }

    @Test
    public void testSetValueWithOverlapFails() {
        assertThrows(HeaderException.class, () -> this.range().setValue(this.rangesWithOverlap()));
    }

    @Test
    public void testSetValueWithOverlapFails2() {
        assertThrows(HeaderException.class, () -> this.range().setValue(this.rangesWithOverlap2()));
    }

    @Test
    public void testSetValueWithOverlapFails3() {
        assertThrows(HeaderException.class, () -> this.range().setValue(this.rangesWithOverlap3()));
    }

    @Test
    public void testSetValueSame() {
        final RangeHeader range = this.range();
        assertSame(range, range.setValue(this.ranges()));
    }

    @Test
    public void testSetValueDifferent() {
        final RangeHeader range = this.range();
        final List<Range<Long>> value = Lists.of(rangeLte1000());
        final RangeHeader different = range.setValue(value);
        this.check(different, UNIT, value);
    }

    private void check(final RangeHeader range,
                       final RangeHeaderUnit unit,
                       final List<Range<Long>> values) {
        this.checkEquals(unit, range.unit(), "unit");
        this.checkEquals(values, range.value(), "value");
    }

    // isWildcard ..................................................................................................

    @Test
    public void testIsWildcard() {
        this.isWildcardAndCheck(false);
    }

    // parse ...........................................................

    @Test
    public void testParseWithInvalidUnitFails() {
        this.parseStringFails2("invalid=0-100");
    }

    @Test
    public void testParseWithNoneUnitFails() {
        this.parseStringFails2("none=0-100");
    }

    @Test
    public void testParseWithOverlapFails() {
        this.parseStringFails2("bytes=100-150,200-250,225-300");
    }

    @Test
    public void testParseWithOverlapFails2() {
        this.parseStringFails2("bytes=100-150,200-250,225-");
    }

    @Test
    public void testParseWithOverlapFails3() {
        this.parseStringFails2("bytes=-150,200-250,125-175");
    }

    @Test
    public void testParseRangeMissingStartFails() {
        this.parseStringFails2("bytes=-99");
    }

    @Test
    public void testParseRangeMissingStartFails2() {
        this.parseStringFails2("bytes=98-99,-50");
    }

    private void parseStringFails2(final String text) {
        this.parseStringFails(text, HeaderException.class);
    }

    @Test
    public void testParseOpenRange() {
        this.parseStringAndCheck("bytes=123-",
                UNIT,
                this.rangeGte123());
    }

    @Test
    public void testParseClosedRange() {
        this.parseStringAndCheck("bytes=123-456",
                UNIT,
                this.rangeGte123().and(this.rangeLte456()));
    }

    @Test
    public void testParseClosedCommaRangeOpen() {
        this.parseStringAndCheck("bytes=123-456,789-",
                UNIT,
                this.rangeGte123().and(this.rangeLte456()),
                this.rangeGte789());
    }

    @Test
    public void testParseClosedCommaWhitespaceRangeOpen() {
        this.parseStringAndCheck("bytes=123-456, 789-",
                UNIT,
                this.rangeGte123().and(this.rangeLte456()),
                this.rangeGte789());
    }

    @Test
    public void testParseClosedCommaWhitespaceRangeOpen2() {
        this.parseStringAndCheck("bytes=123-456,  789-",
                UNIT,
                this.rangeGte123().and(this.rangeLte456()),
                this.rangeGte789());
    }

    @Test
    public void testParseClosedCommaRangeClosed() {
        this.parseStringAndCheck("bytes=123-456,789-1000",
                UNIT,
                this.rangeGte123().and(this.rangeLte456()),
                this.rangeGte789().and(this.rangeLte1000()));
    }

    @SafeVarargs
    private void parseStringAndCheck(final String text,
                                     final RangeHeaderUnit unit,
                                     final Range<Long>... values) {
        this.parseStringAndCheck(text, RangeHeader.with(unit, Lists.of(values)));
    }

    // ParseStringTesting ........................................................................................

    @Override
    public RangeHeader parseString(final String text) {
        return RangeHeader.parse(text);
    }

    // toHeaderText.......................................................

    @Test
    public void testToHeaderTextOpenRange() {
        toHeaderTextAndCheck("bytes=123-",
                UNIT,
                this.rangeGte123());
    }

    @Test
    public void testToHeaderTextClosedRange() {
        toHeaderTextAndCheck("bytes=123-456",
                UNIT,
                this.rangeGte123().and(this.rangeLte456()));
    }

    @Test
    public void testToHeaderTextClosedRangeOpenRange() {
        toHeaderTextAndCheck("bytes=123-456, 789-",
                UNIT,
                this.rangeGte123().and(this.rangeLte456()),
                this.rangeGte789());
    }

    @Test
    public void testToHeaderTextClosedRangeClosedRange() {
        toHeaderTextAndCheck("bytes=123-456, 789-1000",
                UNIT,
                this.rangeGte123().and(this.rangeLte456()),
                this.rangeGte789().and(this.rangeLte1000()));
    }

    @SafeVarargs
    private void toHeaderTextAndCheck(final String headerText,
                                      final RangeHeaderUnit unit,
                                      final Range<Long>... ranges) {
        this.toHeaderTextAndCheck(this.range(unit, ranges), headerText);
    }

    // HashCodeEqualsDefined ..................................................................................................

    @Test
    public void testEqualsDifferentRanges() {
        this.checkNotEquals(this.range(UNIT, Range.greaterThan(456L)));
    }

    // toString.......................................................

    @Test
    public void testToStringOpenRange() {
        toStringAndCheck("bytes=123-",
                UNIT,
                this.rangeGte123());
    }

    @Test
    public void testToStringClosedRange() {
        toStringAndCheck("bytes=123-456",
                UNIT,
                this.rangeGte123().and(this.rangeLte456()));
    }

    @Test
    public void testToStringClosedRangeOpenRange() {
        toStringAndCheck("bytes=123-456, 789-",
                UNIT,
                this.rangeGte123().and(this.rangeLte456()),
                this.rangeGte789());
    }

    @Test
    public void testToStringClosedRangeClosedRange() {
        toStringAndCheck("bytes=123-456, 789-1000",
                UNIT,
                this.rangeGte123().and(this.rangeLte456()),
                this.rangeGte789().and(this.rangeLte1000()));
    }

    @SafeVarargs
    private void toStringAndCheck(final String toString,
                                  final RangeHeaderUnit unit,
                                  final Range<Long>... ranges) {
        this.toStringAndCheck(this.range(unit, ranges), toString);
    }

    private RangeHeader range() {
        return this.range(UNIT, this.rangeGte123());
    }

    @SafeVarargs
    private RangeHeader range(final RangeHeaderUnit unit,
                              final Range<Long>... ranges) {
        return RangeHeader.with(unit, Lists.of(ranges));
    }

    private List<Range<Long>> ranges() {
        return Lists.of(this.rangeGte123());
    }

    private Range<Long> rangeGte123() {
        return Range.greaterThanEquals(123L);
    }

    private Range<Long> rangeLte456() {
        return Range.lessThanEquals(456L);
    }

    private Range<Long> rangeGte789() {
        return Range.greaterThanEquals(789L);
    }

    private Range<Long> rangeLte1000() {
        return Range.lessThanEquals(1000L);
    }

    private List<Range<Long>> rangesWithOverlap() {
        return Lists.of(
                between(100, 200),
                between(200, 300),
                between(275, 350));
    }

    private List<Range<Long>> rangesWithOverlap2() {
        return Lists.of(
                between(100, 200),
                between(200, 300),
                Range.greaterThanEquals(275L));
    }

    private List<Range<Long>> rangesWithOverlap3() {
        return Lists.of(
                Range.lessThanEquals(100L),
                between(200, 300),
                between(75, 150));
    }

    private Range<Long> between(final long lower, final long upper) {
        return Range.greaterThanEquals(lower).and(Range.lessThanEquals(upper));
    }

    @Override
    public RangeHeader createHeader() {
        return this.range();
    }

    @Override
    public RangeHeader createDifferentHeader() {
        return RangeHeader.with(RangeHeaderUnit.BYTES, Lists.of(Range.greaterThan(999L)));
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
    public Class<RangeHeader> type() {
        return RangeHeader.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
