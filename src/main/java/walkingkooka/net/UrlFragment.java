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


import walkingkooka.CanBeEmpty;
import walkingkooka.Value;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Objects;

/**
 * The fragment within a {@link Url}.
 * <pre>
 * https://datatracker.ietf.org/doc/html/rfc3986
 * </pre>
 */
public final class UrlFragment implements Value<String>,
        CanBeEmpty,
        Comparable<UrlFragment> {

    /**
     * An empty or absent fragment.
     */
    public final static UrlFragment EMPTY = new UrlFragment("");

    /**
     * An empty or absent fragment.
     */
    public final static UrlFragment SLASH = new UrlFragment("/");

    /**
     * Parses the given text typically from a URL into a {@link UrlFragment}.
     */
    public static UrlFragment parse(final String value) throws NullPointerException, IllegalArgumentException {
        try {
            return with(
                    URLDecoder.decode(
                            value.replace("+", ENCODED_PLUS_SIGN),
                            UTF8
                    )
            );
        } catch (final UnsupportedEncodingException cause) {
            throw new Error(cause);
        }
    }

    /**
     * Factory that creates a {@link UrlFragment}.
     */
    public static UrlFragment with(final String value) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(value, "value");

        return value.isEmpty() ?
                EMPTY :
                new UrlFragment(value);
    }

    /**
     * Private constructor use factory
     */
    private UrlFragment(final String value) {
        super();
        this.value = value;
    }

    /**
     * Appends slash then the give {@link UrlFragment} providing it is not empty and returns the result.
     */
    public UrlFragment appendSlashThen(final UrlFragment fragment) {
        Objects.requireNonNull(fragment, "fragment");

        return this.isEmpty() ?
                fragment :
                fragment.isEmpty() ?
                        this :
                        new UrlFragment(
                                this.value +
                                        "/" +
                                        fragment.value
                        );
    }

    /**
     * Appends the give {@link UrlFragment} providing it is not empty and returns the result.
     */
    public UrlFragment append(final UrlFragment fragment) {
        Objects.requireNonNull(fragment, "fragment");

        return this.isEmpty() ?
                fragment :
                fragment.isEmpty() ?
                        this :
                        new UrlFragment(
                                this.value +
                                        fragment.value
                        );
    }

    /**
     * Returns true if this fragment is empty.
     */
    @Override
    public boolean isEmpty() {
        return this.value().isEmpty();
    }

    /**
     * Returns the fragment in its original form.
     */
    @Override
    public String value() {
        return this.value;
    }

    private final String value;

    // Object

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return (this == other) ||
                other instanceof UrlFragment && this.equals0((UrlFragment) other);
    }

    private boolean equals0(final UrlFragment other) {
        return this.value.equals(other.value);
    }

    // *    unreserved    = ALPHA / DIGIT / "-" / "." / "_" / "~"
    // *    reserved      = gen-delims / sub-delims
    // *    gen-delims    = ":" / "/" / "?" / "#" / "[" / "]" / "@"
    // *    sub-delims    = "!" / "$" / "&" / "'" / "(" / ")"
    // *                  / "*" / "+" / "," / ";" / "="

    // https://en.wikipedia.org/wiki/Uniform_Resource_Identifier#Syntax
    //
    // The scheme- or implementation-specific reserved character + may be used in the scheme, userinfo, host, path, query, and fragment,
    // and the scheme- or implementation-specific reserved characters !, $, &, ', (, ), *, ,, ;, and = may be used in the
    // userinfo, host, path, query, and fragment. Additionally, the generic reserved character : may be used in the userinfo,
    // path, query and fragment, the generic reserved characters @ and / may be used in the path, query and fragment,
    // and the generic reserved character ? may be used in the query and fragment.

    @Override
    public String toString() {
        try {
            return URLEncoder.encode(
                            this.value,
                            UTF8
                    )
                    .replace(ENCODED_SPACE, "%20")
                    //
                    .replace(ENCODED_PLUS_SIGN, "+")
                    //
                    .replace(ENCODED_EXCLAIMATION, "!")
                    .replace(ENCODED_DOLLAR_SIGN, "$")
                    .replace(ENCODED_AMPERSAND, "&")
                    .replace(ENCODED_SINGLE_QUOTE, "'")
                    .replace(ENCODED_OPEN_PARENS, "(")
                    .replace(ENCODED_CLOSE_PARENS, ")")
                    .replace(ENCODED_STAR, "*")
                    .replace(ENCODED_COMMA, ",")
                    .replace(ENCODED_SEMI_COLON, ";")
                    .replace(ENCODED_EQUALS_SIGN, "=")
                    //
                    .replace(ENCODED_COLON, ":")
                    //
                    .replace(ENCODED_AT_SIGN, "@")
                    .replace(ENCODED_SLASH, "/")
                    //
                    .replace(ENCODED_QUESTION_MARK, "?")
                    //
                    .replace(ENCODED_BRACKET_OPEN, "[")
                    .replace(ENCODED_BRACKET_CLOSE, "]");
        } catch (final UnsupportedEncodingException cause) {
            throw new Error(cause);
        }
    }

    // The scheme- or implementation-specific reserved character + may be used in the scheme, userinfo, host, path, query, and fragment,

    private final static String ENCODED_PLUS_SIGN = URLEncoder.encode("+");

    // and the scheme- or implementation-specific reserved characters !, $, &, ', (, ), *, ,, ;, and = may be used in the
    // userinfo, host, path, query, and fragment.
    private final static String ENCODED_EXCLAIMATION = URLEncoder.encode("!");

    private final static String ENCODED_DOLLAR_SIGN = URLEncoder.encode("$");

    private final static String ENCODED_AMPERSAND = URLEncoder.encode("&");

    private final static String ENCODED_SINGLE_QUOTE = URLEncoder.encode("'");

    private final static String ENCODED_OPEN_PARENS = URLEncoder.encode("(");

    private final static String ENCODED_CLOSE_PARENS = URLEncoder.encode(")");

    private final static String ENCODED_STAR = URLEncoder.encode("*");

    private final static String ENCODED_COMMA = URLEncoder.encode(",");

    private final static String ENCODED_SEMI_COLON = URLEncoder.encode(";");

    private final static String ENCODED_EQUALS_SIGN = URLEncoder.encode("=");

    // The generic reserved character : may be used in the userinfo, path, query and fragment

    private final static String ENCODED_COLON = URLEncoder.encode(":");

    // the generic reserved characters @ and / may be used in the path, query and fragment,
    private final static String ENCODED_AT_SIGN = URLEncoder.encode("@");

    private final static String ENCODED_SLASH = URLEncoder.encode("/");

    // and the generic reserved character ? may be used in the query and fragment.

    private final static String ENCODED_QUESTION_MARK = URLEncoder.encode("?");

    // additional characters
    private final static String ENCODED_BRACKET_OPEN = URLEncoder.encode("[");
    private final static String ENCODED_BRACKET_CLOSE = URLEncoder.encode("]");

    private final static String ENCODED_SPACE = URLEncoder.encode(" ");

    private final static String UTF8 = "UTF-8";

    void toString0(final StringBuilder b) {
        if (!this.value.isEmpty()) {
            b.append(Url.FRAGMENT_START.character());
            b.append(this.toString());
        }
    }

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final UrlFragment other) {
        return this.value.compareTo(other.value);
    }
}
