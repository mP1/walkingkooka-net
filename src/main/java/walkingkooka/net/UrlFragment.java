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


import walkingkooka.Value;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Objects;

/**
 * The fragment within a {@link Url}.
 */
public final class UrlFragment implements Value<String> {

    /**
     * An empty or absent fragment.
     */
    public final static UrlFragment EMPTY = new UrlFragment("");

    /**
     * Parses the given text typically from a URL into a {@link UrlFragment}.
     */
    public static UrlFragment parse(final String value) throws NullPointerException, IllegalArgumentException {
        try {
            return with(
                    URLDecoder.decode(
                            value,
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

    public UrlFragment append(final UrlFragment fragment) {
        Objects.requireNonNull(fragment, "fragment");

        return this.isEmpty() ?
                fragment :
                fragment.isEmpty() ?
                        this :
                        with(this.value + fragment.value);
    }

    private boolean isEmpty() {
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

    @Override
    public String toString() {
        try {
            return URLEncoder.encode(
                    this.value,
                    UTF8
            );
        } catch (final UnsupportedEncodingException cause) {
            throw new Error(cause);
        }
    }

    private final static String UTF8 = "UTF-8";

    void toString0(final StringBuilder b) {
        if (!this.value.isEmpty()) {
            b.append(Url.FRAGMENT_START.character());
            b.append(this.toString());
        }
    }
}
