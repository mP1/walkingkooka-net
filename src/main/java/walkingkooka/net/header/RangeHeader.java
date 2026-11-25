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

import walkingkooka.Cast;
import walkingkooka.HasValue;
import walkingkooka.collect.Range;
import walkingkooka.collect.RangeBound;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.CharSequences;
import walkingkooka.text.CharacterConstant;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents the value of a Range header. Note the unit is not validate except that it must not be empty.
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Range"></a>
 * <pre>
 * The Range HTTP request header indicates the part of a document that the server should return.
 * Several parts can be requested with one Range header at once, and the server may send back these ranges in a multipart
 * document. If the server sends back ranges, it uses the 206 Partial Content for the response. If the ranges are invalid,
 * the server returns the 416 Range Not Satisfiable error. The server can also ignore the Range header and return the whole
 * document with a 200 status code.
 * ...
 * Range: <unit>=<range-start>-
 * Range: <unit>=<range-start>-<range-end>
 * Range: <unit>=<range-start>-<range-end>, <range-start>-<range-end>
 * Range: <unit>=<range-start>-<range-end>, <range-start>-<range-end>, <range-start>-<range-end>
 * ...
 * <unit>
 * The unit in which ranges are specified. This is usually bytes.
 * <range-start>
 * An integer in the given unit indicating the beginning of the request range.
 * <range-end>
 * An integer in the given unit indicating the end of the requested range. This value is optional and, if omitted, the end of the document is taken as the end of the range.
 * ...
 * Range: bytes=200-1000, 2000-6576, 19000-
 * </pre>
 */
public final class RangeHeader implements Header,
    HasValue<List<Range<Long>>> {

    public final static CharacterConstant BETWEEN = CharacterConstant.with('-');

    /**
     * Parsers a header value.
     */
    public static RangeHeader parse(final String header) {
        CharSequences.failIfNullOrEmpty(
            header,
            "Range"
        );

        final int equalsAfterUnit = header.indexOf('=');
        if (-1 == equalsAfterUnit) {
            throw new IllegalArgumentException("Missing unit");
        }

        final List<Range<Long>> ranges = Arrays.stream(header.substring(equalsAfterUnit + 1).split(SEPARATOR.string()))
            .map(RangeHeader::range)
            .collect(Collectors.toList());

        final RangeHeaderUnit unit = RangeHeaderUnit.parse(header.substring(0, equalsAfterUnit));
        unit.rangeCheck();
        overlappingRangesCheck(ranges);

        return new RangeHeader(unit, ranges);
    }

    private static Range<Long> range(final String value) {
        final int dash = value.indexOf(BETWEEN.character());
        if (-1 == dash) {
            throw new IllegalArgumentException(
                "Invalid Range: Missing " + CharSequences.quoteIfChars(BETWEEN.character()));
        }

        if (0 == dash) {
            throw new IllegalArgumentException("Invalid Range: Missing start");
        }

        final Range<Long> from = Range.greaterThanEquals(Long.parseLong(value.substring(0, dash).trim()));
        final String to = value.substring(dash + 1);
        return to.isEmpty() ?
            from :
            from.and(Range.lessThanEquals(Long.parseLong(to)));
    }

    /**
     * Factory that creates a new {@link RangeHeader}
     */
    public static RangeHeader with(final RangeHeaderUnit unit,
                                   final List<Range<Long>> ranges) {
        return new RangeHeader(
            checkUnit(unit),
            copyAndOverlappingRangesCheck(ranges)
        );
    }

    /**
     * Private ctor use factory.
     */
    private RangeHeader(final RangeHeaderUnit unit,
                        final List<Range<Long>> ranges) {
        super();
        this.unit = unit;
        this.ranges = ranges;
    }

    // unit ............................................................................................................

    public RangeHeaderUnit unit() {
        return this.unit;
    }

    public RangeHeader setUnit(final RangeHeaderUnit unit) {
        return this.unit.equals(unit) ?
            this :
            new RangeHeader(
                checkUnit(unit),
                this.ranges
            );
    }

    private final RangeHeaderUnit unit;

    private static RangeHeaderUnit checkUnit(final RangeHeaderUnit unit) {
        Objects.requireNonNull(unit, "unit");

        unit.rangeCheck();
        return unit;
    }

    // value ...........................................................................................................

    @Override
    public List<Range<Long>> value() {
        return this.ranges;
    }

    public RangeHeader setValue(final List<Range<Long>> value) {
        final List<Range<Long>> copy = copyAndOverlappingRangesCheck(value);
        return this.ranges.equals(copy) ?
            this :
            new RangeHeader(
                this.unit,
                copy
            );
    }

    private final List<Range<Long>> ranges;

    /**
     * The charsets of ranges must not be empty, contain nulls or overlapping ranges.
     */
    private static List<Range<Long>> copyAndOverlappingRangesCheck(final List<Range<Long>> ranges) {
        Objects.requireNonNull(ranges, "ranges");

        final List<Range<Long>> copy = Lists.immutable(ranges);

        overlappingRangesCheck(copy);

        return copy;
    }

    private static void overlappingRangesCheck(final List<Range<Long>> ranges) {
        final int count = ranges.size();
        if (0 == count) {
            throw new IllegalArgumentException("Missing range");
        }
        final int last = count - 1;
        for (int i = 0; i < last; i++) {
            final Range<Long> range = ranges.get(i);

            for (int j = i + 1; j < count; j++) {
                rangeOverlapCheck(
                    range,
                    ranges.get(j)
                );
            }
        }
    }

    private static void rangeOverlapCheck(final Range<Long> left,
                                          final Range<Long> right) {
        if (left.isOverlapping(right)) {
            throw new IllegalArgumentException("Overlap between " + formatRange(left) + " and " + formatRange(right));
        }
    }

    // Header...........................................................................................................

    @Override
    public String text() {
        return this.toString();
    }

    @Override
    public boolean isWildcard() {
        return false;
    }

    // HasHeaderScope ..................................................................................................

    final static boolean IS_MULTIPART = false;

    @Override
    public boolean isMultipart() {
        return IS_MULTIPART;
    }

    final static boolean IS_REQUEST = true;

    @Override
    public boolean isRequest() {
        return IS_REQUEST;
    }

    final static boolean IS_RESPONSE = true;

    @Override
    public boolean isResponse() {
        return IS_RESPONSE;
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.unit,
            this.ranges
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof RangeHeader &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final RangeHeader other) {
        return this.unit.equals(other.unit) &
            this.ranges.equals(other.ranges);
    }

    @Override
    public String toString() {
        return this.unit.text() + PARAMETER_NAME_VALUE_SEPARATOR.character() + this.ranges.stream()
            .map(RangeHeader::formatRange)
            .collect(Collectors.joining(SEPARATOR + " "));
    }

    /**
     * Formats a {@link Range} to match the header format.
     */
    private static String formatRange(final Range<Long> range) {
        final StringBuilder b = new StringBuilder();
        b.append(
            range.lowerBound()
                .value()
                .orElse(0L)
        );
        b.append(BETWEEN);

        final RangeBound<Long> upper = range.upperBound();
        if (!upper.isAll()) {
            b.append(upper.value().get());
        }
        return b.toString();
    }
}
