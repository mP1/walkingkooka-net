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

package walkingkooka.net;

import walkingkooka.InvalidCharacterException;
import walkingkooka.InvalidTextLengthException;
import walkingkooka.NeverError;
import walkingkooka.naming.Name;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * A {@link Name} is a component within a {@link UrlPath}. It is assumed that the {@link String value} is decoded and thus may include invalid
 * characters that would otherwise need encoding such as <code>?</code>.
 */
public final class UrlPathName extends NetName implements Comparable<UrlPathName> {

    /**
     * The maximum length of a {@link UrlPathName}.
     */
    public final static int MAXIMUM_LENGTH = 1024;

    final static String ROOT_STRING = "";

    /**
     * Only used by {@link UrlPath} to note a root path.
     */
    final static UrlPathName ROOT = new UrlPathName(ROOT_STRING);

    /**
     * An empty {@link UrlPathName}. Useful when composing paths in a fluent functional manner.
     */
    public final static UrlPathName EMPTY = ROOT;

    public final static String WILDCARD_STRING = "*";

    /**
     * Only used by {@link UrlPath} to note a wildcard.
     */
    public final static UrlPathName WILDCARD = new UrlPathName(WILDCARD_STRING);

    /**
     * Parses the given text including support for decoding percent encoded characters. Invalid percent encodings are ignored.
     */
    public static UrlPathName parse(final String text) {
        Objects.requireNonNull(text, "text");

        final StringBuilder out = new StringBuilder();

        final int MODE_CHAR = 1;
        final int MODE_FIRST_HEX_DIGIT = 2;
        final int MODE_SECOND_HEX_DIGIT = 3;

        int mode = MODE_CHAR;
        int decodedCharacter = 0;

        // href="https://www.ietf.org/rfc/rfc3986.txt
        int i = 0;

        for (final byte b : text.getBytes(StandardCharsets.UTF_8)) {
            final char c = (char) b;

            switch (mode) {
                case MODE_CHAR:
                    switch (c) {
                        case UrlPath.SEPARATOR_CHAR: // SLASH
                            throw new InvalidCharacterException(
                                text,
                                i
                            );
                        case '-':
                        case '.':
                        case '_':
                        case '~':
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                        case 'G':
                        case 'H':
                        case 'I':
                        case 'J':
                        case 'K':
                        case 'L':
                        case 'M':
                        case 'N':
                        case 'O':
                        case 'P':
                        case 'Q':
                        case 'R':
                        case 'S':
                        case 'T':
                        case 'U':
                        case 'V':
                        case 'W':
                        case 'X':
                        case 'Y':
                        case 'Z':
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                        case 'g':
                        case 'h':
                        case 'i':
                        case 'j':
                        case 'k':
                        case 'l':
                        case 'm':
                        case 'n':
                        case 'o':
                        case 'p':
                        case 'q':
                        case 'r':
                        case 's':
                        case 't':
                        case 'u':
                        case 'v':
                        case 'w':
                        case 'x':
                        case 'y':
                        case 'z':
                            out.append((char) b);
                            break;
                        case '%':
                            mode = MODE_FIRST_HEX_DIGIT;
                            decodedCharacter = 0;
                            break;
                        default:
                            out.append(c);
                            break;
                    }
                    break;
                case MODE_FIRST_HEX_DIGIT:
                    decodedCharacter = Character.digit(
                        c,
                        16
                    );
                    mode = -1 == decodedCharacter ?
                        MODE_CHAR :
                        MODE_SECOND_HEX_DIGIT;
                    break;
                case MODE_SECOND_HEX_DIGIT:
                    final int value = Character.digit(
                        c,
                        16
                    );
                    if (-1 != value) {
                        out.append(
                            (char)
                                (
                                    (decodedCharacter << 4) + value
                                )
                        );
                    }

                    mode = MODE_CHAR;
                    break;
                default:
                    NeverError.unhandledCase(
                        mode,
                        MODE_CHAR, MODE_FIRST_HEX_DIGIT, MODE_SECOND_HEX_DIGIT
                    );
            }

            i++;
        }

        return with(out.toString());
    }

    /**
     * Creates a new valid {@link UrlPathName}.
     */
    public static UrlPathName with(final String name) {
        Objects.requireNonNull(name, "name");

        final UrlPathName urlPathName;

        switch (name) {
            case ROOT_STRING:
                urlPathName = ROOT;
                break;
            case WILDCARD_STRING:
                urlPathName = WILDCARD;
                break;
            default:
                urlPathName = nonConstant(name);
                break;
        }

        return urlPathName;
    }

    private static UrlPathName nonConstant(final String name) {
        CharPredicates.failIfNullOrEmptyOrInitialAndPartFalse(
            name,
            "path",
            CHARS,
            CHARS
        );

        InvalidTextLengthException.throwIfFail(
            "path",
            name,
            1,
            MAXIMUM_LENGTH
        );

        return new UrlPathName(name);
    }

    private final static CharPredicate CHARS = CharPredicates.is(UrlPath.SEPARATOR_CHAR)
        .negate();

    /**
     * Private constructor
     */
    private UrlPathName(final String name) {
        super(name);
    }

    /**
     * Returns true if this name is normalized.
     */
    boolean isNormalized() {
        final String name = this.name;
        return name.length() != 0 &&
            false == name.equals(".") &&
            false == name.equals("..");
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof UrlPathName;
    }

    // Comparable......................................................................................................

    @Override
    public int compareTo(final UrlPathName other) {
        return this.compareTo0(other);
    }

    /**
     * Dumps the {@link String value} adding quotes if necessary.
     */
    @Override
    public String toString() {
        return CharSequences.quoteIfNecessary(this.name).toString();
    }

    // HasCaseSensitivity................................................................................................

    @Override
    public CaseSensitivity caseSensitivity() {
        return CASE_SENSITIVITY;
    }

    public final static CaseSensitivity CASE_SENSITIVITY = CaseSensitivity.SENSITIVE;

    // Serializable......................................................................................................

    private static final long serialVersionUID = 1;

    /**
     * Ensures singleton instance of any {@link UrlPathName#ROOT}.
     */
    private Object readResolve() {
        return this.name.equals(UrlPathName.ROOT.name) ? UrlPathName.ROOT : this;
    }
}
