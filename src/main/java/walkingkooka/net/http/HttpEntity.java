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

package walkingkooka.net.http;

import javaemul.internal.annotations.GwtIncompatible;
import walkingkooka.Binary;
import walkingkooka.CanBeEmpty;
import walkingkooka.Cast;
import walkingkooka.collect.Range;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.header.CharsetName;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.server.WebFile;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.HasText;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

/**
 * A http entity containing headers and body. Note that the content-length is not automatically updated in any factory or setter method.
 */
public abstract class HttpEntity implements HasHeaders,
        CanBeEmpty,
        HasText,
        walkingkooka.UsesToStringBuilder {

    /**
     * {@link Binary} with no body or bytes.
     */
    public final static Binary NO_BODY = Binary.EMPTY;

    /**
     * The separator that follows a header name and comes before a any values.
     */
    public final static CharacterConstant HEADER_NAME_SEPARATOR = CharacterConstant.with(':');

    //https://www.w3.org/International/articles/http-charset/index#:~:text=Documents%20transmitted%20with%20HTTP%20that,is%20ISO%2D8859%2D1.
    public final static Charset DEFAULT_BODY_CHARSET = CharsetName.ISO_8859_1.charset().get();

    /**
     * A constant with no headers.
     */
    public final static Map<HttpHeaderName<?>, List<?>> NO_HEADERS = Maps.empty();

    /**
     * Internal constant
     */
    final static Map<HttpHeaderName<?>, HttpEntityHeaderList> NO_HEADERS2 = Maps.empty();

    /**
     * A {@link HttpEntity} with no headers and no body.
     */
    public final static HttpEntity EMPTY = HttpEntityEmpty.instance();

    /**
     * Returns a {@link HttpEntity} filled with the {@link Throwable} stack trace.
     */
    public static HttpEntity dumpStackTrace(final Throwable thrown) {
        return HttpEntityStackTrace.dumpStackTrace(thrown);
    }

    /**
     * Package private ctor to limit sub classing
     */
    HttpEntity() {
        super();
    }
    // headers ...................................................................................

    @Override
    public final Map<HttpHeaderName<?>, List<?>> headers() {
        return Cast.to(this.headers2());
    }

    abstract Map<HttpHeaderName<?>, HttpEntityHeaderList> headers2();

    /**
     * Would be setter that returns a {@link HttpEntity} with the given headers creating a new instance if necessary.
     */
    public final <T> HttpEntity setHeaders(final Map<HttpHeaderName<?>, List<?>> headers) {
        final Map<HttpHeaderName<?>, HttpEntityHeaderList> copy = checkHeaders(headers);

        return this.headers().equals(copy) ?
                this :
                this.setHeaders0(copy);
    }

    abstract HttpEntity setHeaders0(final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers);

    /**
     * Getter that returns the content length
     */
    public abstract long contentLength();

    /**
     * Would be mutator that sets or replaces the content-length if it is wrong or different from the body's actual length
     */
    public final HttpEntity setContentLength() {
        return this.setHeader0(HttpHeaderName.CONTENT_LENGTH, HttpEntityHeaderList.one(HttpHeaderName.CONTENT_LENGTH, Long.valueOf(this.contentLength())));
    }

    /**
     * Returns the content-type if one is present.
     */
    public abstract Optional<MediaType> contentType();

    /**
     * Sets the content-type replacing any existing value as necessary.
     */
    public final HttpEntity setContentType(final MediaType contentType) {
        Objects.requireNonNull(contentType, "contentType");

        return this.setHeader(
                HttpHeaderName.CONTENT_TYPE,
                Lists.of(contentType)
        );
    }

    /**
     * Sets one or multiple values, replacing any previous or if the list is empty removes the header.
     */
    public final <T> HttpEntity setHeader(final HttpHeaderName<T> header, final List<T> values) {
        checkHeader(header);

        // will return null to indicate values is empty and should be removed
        final HttpEntityHeaderList copy = HttpEntityHeaderList.copy(header, values);

        return null != copy ?
                this.setHeader0(header, copy) :
                this.remove0(header);
    }

    abstract <T> HttpEntity setHeader0(final HttpHeaderName<T> header, final HttpEntityHeaderList value);

    /**
     * Adds the given header from this entity returning a new instance if the header and value are new.
     */
    public final <T> HttpEntity addHeader(final HttpHeaderName<T> header, final T value) {
        checkHeader(header);

        return this.addHeader0(header, value);
    }

    abstract <T> HttpEntity addHeader0(final HttpHeaderName<T> header, final T value);

    /**
     * Removes the given header from this entity returning a new instance if it existed.
     */
    public final HttpEntity removeHeader(final HttpHeaderName<?> header) {
        checkHeader(header);
        return this.remove0(header);
    }

    abstract HttpEntity remove0(final HttpHeaderName<?> header);

    private static <T> void checkHeader(final HttpHeaderName<T> header) {
        Objects.requireNonNull(header, "header");
    }

    /**
     * While checking also make a defensive copy of the given {@link Map}.
     */
    static Map<HttpHeaderName<?>, HttpEntityHeaderList> checkHeaders(final Map<HttpHeaderName<?>, List<?>> headers) {
        Objects.requireNonNull(headers, "headers");

        final Map<HttpHeaderName<?>, HttpEntityHeaderList> copy = Maps.ordered();

        for (final Entry<HttpHeaderName<?>, List<?>> nameAndValues : headers.entrySet()) {
            final HttpHeaderName<?> header = nameAndValues.getKey();

            copy.put(header, HttpEntityHeaderList.copy(header, Cast.to(nameAndValues.getValue())));
        }
        return Maps.immutable(copy);
    }

    final Charset charset() {
        return this.charset(HttpEntity.DEFAULT_BODY_CHARSET);
    }

    // body ...................................................................................

    /**
     * Returns the body of the {@link HttpEntity} in binary form.
     */
    public abstract Binary body();

    /**
     * Would be setter that returns a {@link HttpEntity} with the given body creating a new instance if necessary.
     */
    public final HttpEntity setBody(final Binary body) {
        checkBody(body);

        final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers = this.headers2();
        return body.size() == 0L && headers.isEmpty() ?
                EMPTY :
                body.equals(this.body()) ?
                        this :
                        this.replace(headers, body);
    }

    // will effectively be removed because setBody is marked as @GwtIncompatible
    static Binary checkBody(final Binary body) {
        return Objects.requireNonNull(body, "body");
    }

    // bodyText ........................................................................................................

    /**
     * Returns the body as text using the {@link HttpHeaderName#CONTENT_TYPE} if present.
     */
    public abstract String bodyText();

    /**
     * Would be setter that returns a {@link HttpEntity} with the given body text creating a new instance if necessary.
     */
    public final HttpEntity setBodyText(final String bodyText) {
        Objects.requireNonNull(bodyText, "bodyText");

        return this.setBodyText0(bodyText);
    }

    abstract HttpEntity setBodyText0(final String bodyText);

    /**
     * Copies the content of the {@link WebFile} into this {@link HttpEntity}
     */
    public final HttpEntity setBody(final WebFile file,
                                    final Charset defaultCharset) {
        Objects.requireNonNull(file, "file");
        return HttpEntityInterop.setBody(this, file, defaultCharset);
    }

    // isEmpty.........................................................................................................

    /**
     * Returns true if this entity is empty, without headers and without any body.
     */
    @Override
    public final boolean isEmpty() {
        return this instanceof HttpEntityEmpty;
    }

    // extractRange ...................................................................................

    /**
     * Extracts the desired range returning an entity with the selected bytes creating a new instance if necessary.
     */
    @GwtIncompatible
    public final HttpEntity extractRange(final Range<Long> range) {
        return this.setBody(this.body().extract(range));
    }

    // replace....................................................................................................

    abstract HttpEntity replace(final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers);

    abstract HttpEntity replace(final Map<HttpHeaderName<?>, HttpEntityHeaderList> headers,
                                final Binary body);

    // HasText..........................................................................................................

    @Override
    public final String text() {
        return this.bodyText();
    }

    // Object...........................................................................................................

    @Override
    public final int hashCode() {
        return Objects.hash(this.headers(), this.body());
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
                other instanceof HttpEntity &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final HttpEntity other) {
        return this.headers().equals(other.headers()) &&
                HttpEntityInterop.equalsBody(this, other);
    }

    public abstract String toString();
}
