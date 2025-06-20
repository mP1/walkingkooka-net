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
import walkingkooka.collect.Range;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.ThrowableTesting;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.CharSequences;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ContentRangeTest extends HeaderTestCase<ContentRange> implements ParseStringTesting<ContentRange>,
    ThrowableTesting {

    private final static RangeHeaderUnit UNIT = RangeHeaderUnit.BYTES;
    private final static Optional<Long> SIZE = Optional.of(789L);

    // with.

    @Test
    public void testWithNullUnitFails() {
        assertThrows(
            NullPointerException.class,
            () -> ContentRange.with(
                null,
                this.range(),
                SIZE
            )
        );
    }

    @Test
    public void testWithNullRangeFails() {
        assertThrows(
            NullPointerException.class,
            () -> ContentRange.with(
                UNIT,
                null,
                SIZE
            )
        );
    }

    @Test
    public void testWithNullSizeFails() {
        assertThrows(
            NullPointerException.class,
            () -> ContentRange.with(
                UNIT,
                this.range(),
                null
            )
        );
    }

    @Test
    public void testWithNegativeRangeLowerFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> ContentRange.with(
                UNIT,
                this.range(-1, 123),
                SIZE
            )
        );
    }

    @Test
    public void testWithRangeLowerExclusiveFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> ContentRange.with(
                UNIT,
                Optional.of(
                    Range.greaterThan(123L)
                ),
                SIZE
            )
        );
    }

    @Test
    public void testWithRangeUpperBeforeRangeLowerFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> ContentRange.with(
                UNIT,
                this.range(10, 9),
                SIZE
            )
        );
    }

    @Test
    public void testWithRangeUpperExclusiveFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> ContentRange.with(
                UNIT,
                Optional.of(Range.greaterThanEquals(123L).and(Range.lessThan(456L))),
                SIZE
            )
        );
    }

    @Test
    public void testWithNegativeSizeFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> ContentRange.with(
                UNIT,
                this.range(),
                Optional.of(-1L)
            )
        );
    }

    @Test
    public void testWith() {
        this.check(
            this.contentRange(),
            UNIT,
            this.range(),
            SIZE
        );
    }

    @Test
    public void testWithNoRange() {
        final Optional<Range<Long>> range = ContentRange.NO_RANGE;
        final ContentRange contentRange = ContentRange.with(
            UNIT,
            range,
            SIZE
        );
        this.check(
            contentRange,
            UNIT,
            range,
            SIZE
        );
    }

    @Test
    public void testWithNoSize() {
        final ContentRange contentRange = ContentRange.with(
            UNIT,
            this.range(),
            ContentRange.NO_SIZE
        );
        this.check(
            contentRange,
            UNIT,
            this.range(),
            ContentRange.NO_SIZE
        );
    }

    // setUnit..........................................................................................................

    @Test
    public void testSetUnitNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.contentRange()
                .setUnit(null)
        );
    }

    @Test
    public void testSetUnitNoneFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.contentRange()
                .setUnit(RangeHeaderUnit.NONE)
        );
    }

    @Test
    public void testSetUnitSame() {
        final ContentRange contentRange = this.contentRange();
        assertSame(contentRange, contentRange.setUnit(UNIT));
    }

    // setRange.........................................................................................................

    @Test
    public void testSetRangeWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.contentRange()
                .setRange(null)
        );
    }

    @Test
    public void testSetRangeWithWildcardFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.contentRange()
                .setRange(
                    Optional.of(Range.all())
                )
        );
    }

    @Test
    public void testSetRangeNegativeLowerBoundsFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.contentRange()
                .setRange(
                    this.range(-1, 123)
                )
        );
    }

    @Test
    public void testSetRangeUpperLessThanLowerBoundsFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.contentRange()
                .setRange(
                    this.range(10, 9)
                )
        );
    }

    @Test
    public void testSetRangeSame() {
        final ContentRange contentRange = this.contentRange();
        assertSame(
            contentRange,
            contentRange.setRange(this.range())
        );
    }

    @Test
    public void testSetRangeDifferent() {
        this.setRangeDifferentAndCheck(
            this.range(999, 1000)
        );
    }

    @Test
    public void testSetRangeDifferentNoRange() {
        this.setRangeDifferentAndCheck(ContentRange.NO_RANGE);
    }

    private void setRangeDifferentAndCheck(final Optional<Range<Long>> range) {
        final ContentRange contentRange = this.contentRange();

        final ContentRange different = contentRange.setRange(range);

        this.check(
            different,
            UNIT,
            range,
            SIZE
        );
        this.check(contentRange);
    }

    // setSize.....................................................................................................

    @Test
    public void testSetSizeWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.contentRange()
                .setSize(null)
        );
    }

    @Test
    public void testSetSizeWithNegativeSizeFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.contentRange()
                .setSize(
                    Optional.of(-1L)
                )
        );
    }

    @Test
    public void testSetSizeSame() {
        final ContentRange contentRange = this.contentRange();
        assertSame(
            contentRange,
            contentRange.setSize(SIZE)
        );
    }

    @Test
    public void testSetSizeDifferent() {
        final ContentRange contentRange = this.contentRange();

        final Optional<Long> size = Optional.of(456L);
        final ContentRange different = contentRange.setSize(size);

        this.check(
            different,
            UNIT,
            this.range(),
            size
        );
        this.check(contentRange);
    }

    @Test
    public void testSetSizeDifferentNoSize() {
        final ContentRange contentRange = this.contentRange();

        final Optional<Long> size = ContentRange.NO_SIZE;
        final ContentRange different = contentRange.setSize(size);

        this.check(
            different,
            UNIT,
            this.range(),
            size
        );
        this.check(contentRange);
    }

    // check ..................................................................................................

    private void check(final ContentRange contentRange) {
        this.check(contentRange, UNIT, this.range(), SIZE);
    }

    private void check(final ContentRange contentRange,
                       final RangeHeaderUnit unit,
                       final Optional<Range<Long>> range,
                       final Optional<Long> size) {
        this.checkEquals(unit, contentRange.unit(), "unit");
        this.checkEquals(range, contentRange.range(), "range");
        this.checkEquals(size, contentRange.size(), "size");
    }

    // check ...........................................................................................................

    @Test
    public void testIsWildcard() {
        this.isWildcardAndCheck(false);
    }

    // length ..........................................................................................................

    @Test
    public void testLengthBytesNone() {
        this.lengthAndCheck(
            "none 0-99/*",
            100L
        );
    }

    @Test
    public void testLengthBytesWildcardSize() {
        this.lengthAndCheckNone("bytes */888");
    }

    @Test
    public void testLengthBytesLowerThruUpper() {
        this.lengthAndCheck(
            "bytes 0-99/888",
            100L
        );
    }

    @Test
    public void testLengthBytesNoneThruWildcard() {
        this.lengthAndCheck(
            "bytes 2-11/888",
            10
        );
    }

    private void lengthAndCheckNone(final String contentRange) {
        this.lengthAndCheck(
            contentRange,
            Optional.empty()
        );
    }

    private void lengthAndCheck(final String contentRange,
                                final long length) {
        this.lengthAndCheck(
            contentRange,
            Optional.of(length)
        );
    }

    private void lengthAndCheck(final String contentRange,
                                final Optional<Long> length) {
        this.checkEquals(
            length,
            ContentRange.parse(contentRange).length(),
            () -> "length of " + CharSequences.quoteAndEscape(contentRange));
    }

    // parseString......................................................................................................

    @Test
    public void testParseStringEmptyFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> ContentRange.parse("")
        );
    }

    @Test
    public void testParseWhitespaceWithinUnitFails() {
        this.parseStringFails(
            " bytes 100-200/*",
            "Unit missing from \" bytes 100-200/*\""
        );
    }

    @Test
    public void testParseUnitUnknownFails() {
        this.parseStringFails(
            "b",
            "Missing unit from \"b\""
        );
    }

    @Test
    public void testParseUnitInvalidFails() {
        this.parseStringFails(
            "bytes-0-100/*",
            '-'
        );
    }

    @Test
    public void testParseUnitLowerBoundInvalidFails() {
        this.parseStringFails(
            "bytes 0/",
            '/'
        );
    }

    @Test
    public void testParseInvalidLowerBoundCharacterFails() {
        this.parseStringFails(
            "bytes ?0-100/*",
            '?'
        );
    }

    @Test
    public void testParseInvalidLowerBoundCharacterFails2() {
        this.parseStringFails(
            "bytes 1?0-100/*",
            '?'
        );
    }

    @Test
    public void testParseMissingRangeSeparatorFails() {
        this.parseStringFails(
            "bytes 100-*/*",
            '*'
        );
    }

    @Test
    public void testParseMissingRangeSeparatorFails2() {
        this.parseStringFails(
            "bytes 100?200/*",
            '?'
        );
    }

    @Test
    public void testParseInvalidUpperBoundCharacterFails() {
        this.parseStringFails(
            "bytes 100-?00/*",
            '?'
        );
    }

    @Test
    public void testParseInvalidUpperBoundCharacterFails2() {
        this.parseStringFails(
            "bytes 100-2?00/*",
            '?'
        );
    }

    @Test
    public void testParseInvalidUpperBoundsFails() {
        this.parseStringFails(
            "bytes 100-99/*",
            "Invalid upper bounds 99 < 100 in \"bytes 100-99/*\""
        );
    }

    @Test
    public void testParseInvalidSizeCharacterFails() {
        this.parseStringFails(
            "bytes 100-200/?*",
            '?'
        );
    }

    @Test
    public void testParseInvalidSizeCharacterFails2() {
        this.parseStringFails(
            "bytes 100-200/1?",
            '?'
        );
    }

    @Test
    public void testParseInvalidSizeCharacterFails3() {
        this.parseStringFails(
            "bytes 100-200/1*",
            '*'
        );
    }

    @Test
    public void testParseInvalidRangeSeparatorFails() {
        this.parseStringFails(
            "bytes *!*",
            '!'
        );
    }

    @Test
    public void testParseMissingRange() {
        this.parseStringAndCheck(
            "bytes */*",
            UNIT,
            ContentRange.NO_RANGE,
            ContentRange.NO_SIZE
        );
    }

    @Test
    public void testParseMissingRangeWithFileSize() {
        this.parseStringAndCheck(
            "bytes */789",
            UNIT,
            ContentRange.NO_RANGE,
            SIZE
        );
    }

    @Test
    public void testParseSizeMissing() {
        this.parseStringAndCheck(
            "bytes 123-456/*",
            UNIT,
            this.range(),
            ContentRange.NO_SIZE
        );
    }

    @Test
    public void testParseSizePresent() {
        this.parseStringAndCheck(
            "bytes 123-456/789",
            UNIT,
            this.range(),
            SIZE
        );
    }

    @Test
    public void testParseSmallRange() {
        this.parseStringAndCheck(
            "bytes 1-2/3",
            UNIT,
            this.range(1, 2),
            Optional.of(3L)
        );
    }

    @Test
    public void testParseTab() {
        this.parseStringAndCheck(
            "bytes\t123-456/789",
            UNIT,
            this.range(),
            SIZE
        );
    }

    private void parseStringFails(final String text,
                                  final char invalid) {
        this.parseStringFails(
            text,
            new InvalidCharacterException(text, text.indexOf(invalid)).getMessage()
        );
    }

    private void parseStringFails(final String text,
                                  final String message) {
        final HeaderException expected = assertThrows(
            HeaderException.class,
            () -> ContentRange.parse(text)
        );
        checkMessage(
            expected,
            message
        );
    }

    private void parseStringAndCheck(final String header,
                                     final RangeHeaderUnit unit,
                                     final Optional<Range<Long>> range,
                                     final Optional<Long> size) {
        this.checkEquals(
            ContentRange.with(unit, range, size),
            ContentRange.parse(header),
            "Incorrect result when parsing " + CharSequences.quote(header)
        );
    }

    @Override
    public ContentRange parseString(final String text) {
        return ContentRange.parse(text);
    }

    // toHeaderText.....................................................................................................

    @Test
    public void testToHeaderTextNoRange() {
        this.toHeaderTextAndCheck(
            "bytes */*",
            UNIT,
            ContentRange.NO_RANGE,
            ContentRange.NO_SIZE
        );
    }


    @Test
    public void testToHeaderTextMissingRange() {
        this.toHeaderTextAndCheck(
            "bytes */789",
            UNIT,
            ContentRange.NO_RANGE,
            SIZE
        );
    }

    @Test
    public void testToHeaderTextMissingFilesize() {
        this.toHeaderTextAndCheck(
            "bytes 123-456/*",
            UNIT,
            this.range(),
            ContentRange.NO_SIZE
        );
    }

    @Test
    public void testToHeaderTextWithFilesize() {
        this.toHeaderTextAndCheck(
            "bytes 123-456/789",
            UNIT,
            this.range(),
            SIZE
        );
    }

    private void toHeaderTextAndCheck(final String headerText,
                                      final RangeHeaderUnit unit,
                                      final Optional<Range<Long>> range,
                                      final Optional<Long> size) {
        this.toHeaderTextAndCheck(
            ContentRange.with(unit, range, size),
            headerText
        );
    }


    // hashCode/equals .................................................................................................

    @Test
    public void testEqualsDifferentRange() {
        this.checkNotEquals(
            this.range(
                UNIT,
                this.range(456, 789),
                SIZE
            )
        );
    }

    @Test
    public void testEqualsDifferentRange2() {
        this.checkNotEquals(
            this.range(
                UNIT,
                ContentRange.NO_RANGE,
                SIZE
            )
        );
    }

    @Test
    public void testEqualsDifferentSize() {
        this.checkNotEquals(
            this.range(
                UNIT,
                this.range(),
                ContentRange.NO_SIZE
            )
        );
    }

    @Test
    public void testEqualsDifferentSize2() {
        this.checkNotEquals(
            this.range(UNIT,
                this.range(),
                Optional.of(456L)
            )
        );
    }

    @Override
    public ContentRange createDifferentHeader() {
        return ContentRange.with(
            UNIT,
            this.range(),
            Optional.of(1L)
        );
    }

    // helpers..........................................................................................................

    private ContentRange contentRange() {
        return ContentRange.with(
            UNIT,
            this.range(),
            SIZE
        );
    }

    private Optional<Range<Long>> range() {
        return range(123, 456);
    }

    private Optional<Range<Long>> range(final long lower,
                                        final long upper) {
        return Optional.of(
            Range.greaterThanEquals(lower)
                .and(
                    Range.lessThanEquals(upper)
                )
        );
    }

    private ContentRange range(final RangeHeaderUnit unit,
                               final Optional<Range<Long>> range,
                               final Optional<Long> size) {
        return ContentRange.with(unit, range, size);
    }

    @Override
    public ContentRange createHeader() {
        return ContentRange.with(UNIT, this.range(), SIZE);
    }

    @Override
    public boolean isMultipart() {
        return true;
    }

    @Override
    public boolean isRequest() {
        return false;
    }

    @Override
    public boolean isResponse() {
        return true;
    }

    // toString.........................................................................................................

    @Test
    public void testToStringTextNoRange() {
        this.toStringAndCheck(
            UNIT,
            ContentRange.NO_RANGE,
            ContentRange.NO_SIZE,
            "bytes */*"
        );
    }

    @Test
    public void testToStringTextMissingFilesize() {
        this.toStringAndCheck(
            UNIT,
            this.range(),
            ContentRange.NO_SIZE,
            "bytes 123-456/*"
        );
    }

    @Test
    public void testToStringTextWithFilesize() {
        this.toStringAndCheck(
            UNIT,
            this.range(),
            SIZE,
            "bytes 123-456/789"
        );
    }

    private void toStringAndCheck(final RangeHeaderUnit unit,
                                  final Optional<Range<Long>> range,
                                  final Optional<Long> size,
                                  final String toString) {
        this.toStringAndCheck(
            ContentRange.with(
                unit,
                range,
                size
            ).toString(),
            toString
        );
    }

    // class............................................................................................................

    @Override
    public Class<ContentRange> type() {
        return ContentRange.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
