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

import walkingkooka.InvalidTextLengthException;
import walkingkooka.naming.Name;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;

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
