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
import walkingkooka.UsesToStringBuilder;
import walkingkooka.collect.Range;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.header.CharsetName;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.server.WebFile;
import walkingkooka.text.CharSequences;
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
        UsesToStringBuilder {

    /**
     * {@link Binary} with no body or bytes.
     */
    public final static Binary NO_BODY = Binary.EMPTY;

    private final static byte HEADER_SEPARATOR_BYTE = ':';

    /**
     * The separator that follows a header name and comes before a any values.
     */
    public final static CharacterConstant HEADER_NAME_SEPARATOR = CharacterConstant.with(
            (char) HEADER_SEPARATOR_BYTE
    );

    //https://www.w3.org/International/articles/http-charset/index#:~:text=Documents%20transmitted%20with%20HTTP%20that,is%20ISO%2D8859%2D1.
    public final static Charset DEFAULT_BODY_CHARSET = CharsetName.ISO_8859_1.charset().get();

    /**
     * A constant with no headers.
     */
    public final static Map<HttpHeaderName<?>, List<?>> NO_HEADERS = Maps.empty();

    /**
     * Internal constant
     */
    final static Map<HttpHeaderName<?>, HttpEntityHeaderList> NO_HEADERS_HTTP_ENTITY_HEADER_LIST = Maps.empty();

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

    private final static byte CR = '\r';

    private final static byte LF = '\n';

    /**
     * Parses a binary as if it were a request with headers and an optional body.
     * <br>
     * https://datatracker.ietf.org/doc/html/rfc7230
     * <pre>
     * HeaderName COLON HeaderValue CRLF
     * CRLF
     * optional Body
     * </pre>
     */
    public static HttpEntity parse(final Binary binary) {
        Objects.requireNonNull(binary, "binary");

        HttpEntity httpEntity = EMPTY;

        final byte[] bytes = binary.value();
        final int length = bytes.length;

        // parse headers
        final StringBuilder headerName = new StringBuilder();
        final StringBuilder headerValue = new StringBuilder();

        final int MODE_HEADER_NAME_OR_CR = 0;
        final int MODE_HEADER_VALUE_OR_CR = 1;
        final int MODE_CRLF = 2;
        final int MODE_BODY = 3;

        int mode = MODE_HEADER_NAME_OR_CR;

        HeadersLoop:
        for (int i = 0; i < length; i++) {
            final byte b = bytes[i];

            switch (mode) {
                case MODE_HEADER_NAME_OR_CR:
                    switch (b) {
                        case CR:
                            mode = MODE_CRLF; // empty line ?
                            break;
                        case LF:
                            throw new IllegalArgumentException("Got NL expected header name or CR");
                        case HEADER_SEPARATOR_BYTE:
                            mode = MODE_HEADER_VALUE_OR_CR;
                            break;
                        default:
                            headerName.append((char) b);
                            break;
                    }
                    break;
                case MODE_HEADER_VALUE_OR_CR:
                    switch (b) {
                        case CR:
                            mode = MODE_CRLF; // empty line ?
                            break;
                        case LF:
                            throw new IllegalArgumentException("Got NL expected header value or CR");
                        default:
                            headerValue.append((char) b);
                            break;
                    }
                    break;
                case MODE_CRLF:
                    switch (b) {
                        case CR:
                            throw new IllegalArgumentException("Got NL expected header value or CR");
                        case LF:
                            mode = MODE_HEADER_NAME_OR_CR; // empty line ?
                            // header line is empty must be end of headers.
                            if (headerName.length() + headerValue.length() == 0) {
                                httpEntity = httpEntity.setBody(
                                        binary.extract(
                                                Range.greaterThanEquals(
                                                        Long.valueOf(1 + i) // CR
                                                )
                                        )
                                );
                                mode = MODE_BODY;
                                break HeadersLoop;
                            }

                            final HttpHeaderName<?> httpHeaderName = HttpHeaderName.with(
                                    CharSequences.trimRight(
                                            headerName
                                    ).toString()
                            );

                            final Object httpHeaderValue;

                            try {
                                httpHeaderValue = httpHeaderName.parse(
                                        headerValue.toString()
                                );
                            } catch (final RuntimeException cause) {
                                throw new IllegalArgumentException(
                                        cause.getMessage(),
                                        cause
                                );
                            }

                            httpEntity = httpEntity.addHeader(
                                    httpHeaderName,
                                    Cast.to(httpHeaderValue)
                            );
                            headerName.setLength(0);
                            headerValue.setLength(0);
                            break;
                        default:
                            throw new IllegalArgumentException("Got " + CharSequences.quoteAndEscape((char) b) + " expected NL");
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Bad mode=" + mode);
            }
        }

        switch (mode) {
            case MODE_HEADER_NAME_OR_CR:
            case MODE_HEADER_VALUE_OR_CR:
                throw new IllegalArgumentException("Header missing CR");
            case MODE_CRLF:
                throw new IllegalArgumentException("Header CR missing NL");
            case MODE_BODY:
                break;
            default:
                throw new IllegalArgumentException("Bad mode=" + mode);
        }

        return httpEntity;
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

    // multipart........................................................................................................

    /**
     * Only returns true if the content-type is multipart/form-data
     */
    public final boolean isMultipartFormData() {
        return MediaType.MULTIPART_FORM_DATA.test(
                this.contentType()
                        .orElse(null)
        );
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
