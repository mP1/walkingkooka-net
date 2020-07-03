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

import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept-Charset"></a>
 * <pre>
 * Accept-Charset: <charset>
 *
 * // Multiple types, weighted with the quality value syntax:
 * Accept-Charset: utf-8, iso-8859-1;q=0.5
 * </pre>
 */
public final class AcceptCharset extends HeaderValue2<List<AcceptCharsetValue>>
        implements Predicate<MediaType>,
        HasQualityFactorSortedValues<AcceptCharsetValue> {

    /**
     * Parses a header value that contains one or more charsets.
     */
    public static AcceptCharset parse(final String text) {
        return AcceptCharsetHeaderValueParser.parseAcceptCharset(text);
    }

    /**
     * Factory that creates a new {@link AcceptCharset}
     */
    public static AcceptCharset with(final List<AcceptCharsetValue> values) {
        return new AcceptCharset(nonEmptyImmutableList(values, "charsets"));
    }

    /**
     * Private ctor use factory. Only called directly by factory or {@link AcceptCharsetHeaderValueParser}
     */
    private AcceptCharset(final List<AcceptCharsetValue> values) {
        super(values);
    }

    /**
     * Finds the first system supported {@link Charset}. This assumes the {@link AcceptCharsetValue} have already been
     * sorted by q factors, thus order implies importance.
     */
    public Optional<Charset> charset() {
        return this.value().stream()
                .map(chv -> chv.value().charset())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    @Override
    public String toHeaderText() {
        return HeaderValue.toHeaderTextList(value, SEPARATOR);
    }

    private final static String SEPARATOR = HeaderValue.SEPARATOR.string().concat(" ");


    @Override
    public boolean isWildcard() {
        return false;
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
    boolean canBeEqual(final Object other) {
        return other instanceof AcceptCharset;
    }

    // Predicate .......................................................................................................

    /**
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept-Charset"></a>
     * <pre>
     *  Using content negotiation, the server selects one of the encodings, uses it, and informs the client of its choice
     *  within the Content-Type response header, usually in a charset= parameter. Browsers usually don't send this header,
     *  as the default value for each resource is usually correct and transmitting it would allow fingerprinting.
     * </pre>>
     */
    @Override
    public boolean test(final MediaType contentType) {
        Objects.requireNonNull(contentType, "contentType");

        return MediaTypeParameterName.CHARSET.parameterValue(contentType)
                .map(this::test0)
                .orElse(false);
    }

    private boolean test0(final CharsetName charsetName) {
        return this.value.stream()
                .anyMatch(v -> v.test(charsetName));
    }
}
