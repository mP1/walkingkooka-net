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

package walkingkooka.net.http.j2cl;

import walkingkooka.Binary;
import walkingkooka.Cast;
import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringBuilderOption;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.header.CharsetName;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HasHeaders;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.LineEnding;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A slightly simplified {@link walkingkooka.net.http.HttpEntity} that removes the {@link Binary} getters and setters.
 */
public final class HttpEntity implements HasHeaders {

    /**
     * The line ending used in http requests/responses.
     */
    private final static LineEnding LINE_ENDING = LineEnding.CRNL;

    /**
     * The separator that follows a header name and comes before a any values.
     */
    public final static CharacterConstant HEADER_NAME_SEPARATOR = CharacterConstant.with(':');

    /**
     * Creates an {@link HttpEntity} adding the content-type and content-length.
     */
    public static HttpEntity text(final MediaType contentType,
                                  final String text) {
        Objects.requireNonNull(contentType, "contentType");
        Objects.requireNonNull(text, "text");


        return new HttpEntity(Maps.of(HttpHeaderName.CONTENT_LENGTH, Long.valueOf(text.length()),
                HttpHeaderName.CONTENT_TYPE, contentType),
                text);
    }

    //https://www.w3.org/International/articles/http-charset/index#:~:text=Documents%20transmitted%20with%20HTTP%20that,is%20ISO%2D8859%2D1.
    public final static Charset DEFAULT_BODY_CHARSET = CharsetName.ISO_8859_1.charset().get();

    /**
     * A constant with no headers.
     */
    public final static Map<HttpHeaderName<?>, Object> NO_HEADERS = Maps.empty();

    /**
     * A {@link HttpEntity} with no headers and no content.
     */
    public final static HttpEntity EMPTY = new HttpEntity(NO_HEADERS, "");

    /**
     * Private ctor
     */
    private HttpEntity(final Map<HttpHeaderName<?>, Object> headers, final String bodyText) {
        super();
        this.headers = headers;
        this.bodyText = bodyText;
    }
    // headers ...................................................................................

    @Override
    public final Map<HttpHeaderName<?>, Object> headers() {
        return this.headers;
    }

    /**
     * Would be setter that returns a {@link HttpEntity} with the given headers creating a new instance if necessary.
     */
    public final HttpEntity setHeaders(final Map<HttpHeaderName<?>, Object> headers) {
        final Map<HttpHeaderName<?>, Object> copy = checkHeaders(headers);

        return this.headers.equals(copy) ?
                this :
                this.replace(headers);
    }

    /**
     * Would be mutator that sets or replaces the content-length if it is wrong or different from the body's actual length
     */
    public HttpEntity setContentLength() {
        return this.addHeader(HttpHeaderName.CONTENT_LENGTH, Long.valueOf(this.bodyText().length()));
    }

    /**
     * Adds the given header from this entity returning a new instance if the header and value are new.
     */
    public <T> HttpEntity addHeader(final HttpHeaderName<T> header, final T value) {
        check(header);
        Objects.requireNonNull(value, "value");

        return value.equals(this.headers.get(header)) ?
                this :
                this.addHeaderAndReplace(header, value);
    }

    private <T> HttpEntity addHeaderAndReplace(final HttpHeaderName<T> header, final T value) {
        final Map<HttpHeaderName<?>, Object> copy = Maps.ordered();
        copy.putAll(this.headers);
        copy.put(header, header.checkValue(value));
        return this.replace(copy);
    }

    /**
     * Removes the given header from this entity returning a new instance if it existed.
     */
    public HttpEntity removeHeader(final HttpHeaderName<?> header) {
        check(header);

        return this.headers.containsKey(header) ?
                this.removeHeaderAndReplace(header) :
                this;
    }

    private HttpEntity removeHeaderAndReplace(final HttpHeaderName<?> header) {
        return this.replace(this.headers()
                .entrySet()
                .stream()
                .filter(h -> !h.getKey().equals(header))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    private static <T> void check(final HttpHeaderName<T> header) {
        Objects.requireNonNull(header, "header");
    }

    private final Map<HttpHeaderName<?>, Object> headers;

    private static Map<HttpHeaderName<?>, Object> checkHeaders(final Map<HttpHeaderName<?>, Object> headers) {
        Objects.requireNonNull(headers, "headers");

        final Map<HttpHeaderName<?>, Object> copy = Maps.ordered();
        for (Entry<HttpHeaderName<?>, Object> nameAndValue : headers.entrySet()) {
            final HttpHeaderName<?> name = nameAndValue.getKey();
            copy.put(name, name.checkValue(nameAndValue.getValue()));
        }
        return Maps.immutable(copy);
    }

    // bodyText ...................................................................................

    public final String bodyText() {
        return this.bodyText;
    }

    /**
     * Would be setter that returns a {@link HttpEntity} with the given body text creating a new instance if necessary.
     */
    public final HttpEntity setBodyText(final String bodyText) {
        checkBodyText(bodyText);

        return this.bodyText.equals(bodyText) ?
                this :
                new HttpEntity(this.headers, bodyText);
    }

    private static String checkBodyText(final String bodyText) {
        return Objects.requireNonNull(bodyText, "bodyText");
    }

    private final String bodyText;

    // replace....................................................................................................

    private HttpEntity replace(final Map<HttpHeaderName<?>, Object> headers) {
        return new HttpEntity(headers, this.bodyText);
    }

    // Object....................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.headers, this.bodyText);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof HttpEntity &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final HttpEntity other) {
        return this.headers.equals(other.headers) &&
                this.bodyText.equals(other.bodyText);
    }

    /**
     * The {@link String} produced looks almost like a http entity, each header will appear on a single, a colon separates
     * the header name and value, and a blank line between headers and body, with the body bytes appearing in hex form.
     */
    @Override
    public String toString() {
        final int globalAndValueLength = 32 * 1024;

        final ToStringBuilder b = ToStringBuilder.empty()
                .disable(ToStringBuilderOption.QUOTE)
                .disable(ToStringBuilderOption.SKIP_IF_DEFAULT_VALUE)
                .globalLength(globalAndValueLength)
                .valueLength(globalAndValueLength);

        final String eol = LINE_ENDING.toString();

        // headers
        b.valueSeparator(eol);
        b.labelSeparator(HEADER_NAME_SEPARATOR.string().concat(" "));
        b.value(this.headers());
        b.append(eol).append(eol);

        // body
        b.append(this.bodyText);

        return b.build();
    }
}
