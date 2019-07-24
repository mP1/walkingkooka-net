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
import walkingkooka.InvalidCharacterException;
import walkingkooka.InvalidTextLengthException;
import walkingkooka.Value;
import walkingkooka.collect.map.Maps;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CharSequences;

import java.util.Objects;
import java.util.function.Supplier;


/**
 * The multipart boundary.<br>
 * <a href="https://tools.ietf.org/html/rfc2046"></a>
 * <pre>
 * The only mandatory global parameter for the "multipart" media type is
 *    the boundary parameter, which consists of 1 to 70 characters from a
 *    set of characters known to be very robust through mail gateways, and
 *    NOT ending with white space. (If a boundary delimiter line appears to
 *    end with white space, the white space must be presumed to have been
 *    added by a gateway, and must be deleted.)  It is formally specified
 *    by the following BNF:
 *
 *      boundary := 0*69<bchars> bcharsnospace
 *
 *      bchars := bcharsnospace / " "
 *
 *      bcharsnospace := DIGIT / ALPHA / "'" / "(" / ")" /
 *                       "+" / "_" / "," / "-" / "." /
 *                       "/" / ":" / "=" / "?"
 * </pre>
 * <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec19.html"></a>
 */
public final class MediaTypeBoundary implements Value<String>,
        HeaderValue,
        Comparable<MediaTypeBoundary> {

    /**
     * The length of generated boundary's.
     */
    private final static int BOUNDARY_LENGTH = 40;

    /**
     * The characters used in the generated boundary.
     */
    // @VisibleForTesting
    final static byte[] BOUNDARY_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_".getBytes(CharsetName.UTF_8.charset().get());

    /**
     * Generates a unique boundary for the given bytes. The ascii byte form of this boundary is guaranteed to not
     * appear in the given bytes.
     */
    public static MediaTypeBoundary generate(final byte[] body,
                                             final Supplier<Byte> boundaryCharacters) {
        Objects.requireNonNull(body, "body");
        Objects.requireNonNull(boundaryCharacters, "boundaryCharacters");

        return generate0(body.clone(),
                boundaryCharacters,
                BOUNDARY_LENGTH);
    }

    // @VisibleForTesting
    static MediaTypeBoundary generate0(final byte[] body,
                                       final Supplier<Byte> boundaryCharacters,
                                       final int boundaryLength) {

        final int bodyLength = body.length;
        final byte[] boundary = new byte[boundaryLength + PREFIX_LENGTH];
        boundary[0] = '-';
        boundary[1] = '-';

        final int last = bodyLength - boundaryLength + 1;

        Outer:
        for (; ; ) {
            for (int i = 0; i < boundaryLength; i++) {
                boundary[PREFIX_LENGTH + i] = BOUNDARY_CHARACTERS[boundaryCharacters.get() & BOUNDARY_CHARACTERS.length - 1];
            }

            Inner:
            for (int bodyIndex = 0; bodyIndex < last; bodyIndex++) {
                for (int boundaryIndex = 0; boundaryIndex < boundaryLength; boundaryIndex++) {
                    if (body[bodyIndex + boundaryIndex] != boundary[PREFIX_LENGTH + boundaryIndex]) {
                        continue Inner;
                    }
                }
                continue Outer;
            }

            break;
        }

        final char[] chars = new char[boundaryLength];
        for (int i = 0; i < boundaryLength; i++) {
            chars[i] = (char) boundary[PREFIX_LENGTH + i];
        }
        final String text = new String(chars);

        return new MediaTypeBoundary(text, text);
    }

    /**
     * Parses the text into a {@link MediaTypeBoundary}.
     */
    public static MediaTypeBoundary parse(final String value) {
        return MediaTypeBoundaryHeaderValueHandler.INSTANCE.parse(value, MediaTypeParameterName.BOUNDARY);
    }

    /**
     * The max length of a boundary.
     */
    final static int MAX_LENGTH = 70;

    /**
     * Factory that creates a {@link MediaTypeBoundary} after verifying the individual characters.
     */
    public static MediaTypeBoundary with(final String value) {
        CharSequences.failIfNullOrEmpty(value, "value");

        final String trimmed = value.trim();

        boolean requiresDoubleQuotes = false;

        final StringBuilder quoted = new StringBuilder();
        quoted.append(DOUBLE_QUOTE);

        int i = 0;
        for (char c : trimmed.toCharArray()) {
            if (!QUOTED_CHARACTER_PREDICATE.test(c)) {
                throw new InvalidCharacterException(value, i);
            }

            requiresDoubleQuotes = requiresDoubleQuotes | !UNQUOTED_CHARACTER_PREDICATE.test(c);
            quoted.append(c);
            i++;
        }
        quoted.append(DOUBLE_QUOTE);

        return new MediaTypeBoundary(trimmed, requiresDoubleQuotes ?
                quoted.toString() :
                trimmed);
    }

    private final static char DOUBLE_QUOTE = '"';

    /**
     * Only these characters may appear in an unquoted boundary string.
     */
    final static CharPredicate UNQUOTED_CHARACTER_PREDICATE = CharPredicates.rfc2045Token();

    /**
     * <pre>
     *      bcharsnospace := DIGIT / ALPHA / "'" / "(" / ")" /
     *                       "+" / "_" / "," / "-" / "." /
     *                       "/" / ":" / "=" / "?"
     * </pre>
     */
    private final static CharPredicate BOUNDARY_SPECIALS = CharPredicates.any("'()+_,-./:=?");

    /**
     * Matches all valid characters for a media type(mime type) boundary.
     */
    final static CharPredicate QUOTED_CHARACTER_PREDICATE = UNQUOTED_CHARACTER_PREDICATE.or(BOUNDARY_SPECIALS);

    /**
     * Private ctor.
     */
    private MediaTypeBoundary(final String value,
                              final String headerText) {
        super();

        final int length = value.length();
        if (length >= MAX_LENGTH) {
            throw new InvalidTextLengthException("MediaTypeBoundary", value, 0, MAX_LENGTH);
        }
        this.value = value;
        this.headerText = headerText;
    }

    @Override
    public final String value() {
        return this.value;
    }

    private final String value;

    // HasHeaderScope ....................................................................................................

    @Override
    public final boolean isMultipart() {
        return MediaType.IS_MULTIPART;
    }

    @Override
    public final boolean isRequest() {
        return MediaType.IS_REQUEST;
    }

    @Override
    public final boolean isResponse() {
        return MediaType.IS_RESPONSE;
    }

    // headerText.............................................................................

    @Override
    public String toHeaderText() {
        return this.headerText;
    }

    private final String headerText;

    @Override
    public boolean isWildcard() {
        return false;
    }

    // MediaTypeBoundary.............................................................................

    /**
     * Returns the boundary delimiter form that would be used in constructing a multipart message, without the trailing
     * CRLF.
     * <pre>
     * POST /test.html HTTP/1.1
     * Host: example.org
     * Content-Type: multipart/form-data;boundary="boundary"
     *
     * --boundary
     * Content-Disposition: form-data; name="field1"
     * </pre>
     */
    public String multipartBoundaryDelimiter() {
        return PREFIX.concat(this.value);
    }

    private final static String PREFIX = "--";
    private final static int PREFIX_LENGTH = 2;

    /**
     * Creates a {@link MediaType#MULTIPART_BYTE_RANGES} with this boundary parameter.
     */
    public MediaType multipartByteRanges() {
        return MediaType.MULTIPART_BYTE_RANGES.setParameters(Maps.of(MediaTypeParameterName.BOUNDARY, this));
    }

    // Comparable....................................................................................................

    @Override
    public final int compareTo(final MediaTypeBoundary name) {
        return this.value.compareTo(name.value());
    }

    // Object.....................................................................................................

    @Override
    public int hashCode() {
        return this.headerText.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof MediaTypeBoundary &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final MediaTypeBoundary other) {
        return this.value.equals(other.value) &&
                this.headerText.equals(other.headerText);
    }

    /**
     * Returns the value in its raw form.
     */
    @Override
    public String toString() {
        return this.headerText;
    }
}
