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
import walkingkooka.naming.Name;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.text.CharSequences;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Base handler that provides support for converting header text to values and back.
 */
abstract class HeaderHandler<T> {

    /**
     * {@see AbsoluteUrlHeaderHandler}
     */
    static HeaderHandler<AbsoluteUrl> absoluteUrl() {
        return AbsoluteUrlHeaderHandler.INSTANCE;
    }

    /**
     * {@see AcceptHeaderHandler}
     */
    static HeaderHandler<Accept> accept() {
        return AcceptHeaderHandler.INSTANCE;
    }

    /**
     * {@see AcceptCharsetHeaderHandler}
     */
    static HeaderHandler<AcceptCharset> acceptCharset() {
        return AcceptCharsetHeaderHandler.INSTANCE;
    }

    /**
     * {@see AcceptEncodingHeaderHandler}
     */
    static HeaderHandler<AcceptEncoding> acceptEncoding() {
        return AcceptEncodingHeaderHandler.INSTANCE;
    }

    /**
     * {@see AcceptLanguageHeaderHandler}
     */
    static HeaderHandler<AcceptLanguage> acceptLanguage() {
        return AcceptLanguageHeaderHandler.INSTANCE;
    }

    /**
     * {@see CacheControlHeaderHandler}
     */
    static HeaderHandler<CacheControl> cacheControl() {
        return CacheControlHeaderHandler.INSTANCE;
    }

    /**
     * {@see ContentEncodingHeaderHandler}
     */
    static HeaderHandler<ContentEncoding> contentEncoding() {
        return ContentEncodingHeaderHandler.INSTANCE;
    }

    /**
     * {@see CharsetNameHeaderHandler}
     */
    static HeaderHandler<CharsetName> charsetName() {
        return CharsetNameHeaderHandler.INSTANCE;
    }

    /**
     * {@see ClientCookieListHeaderHandler}
     */
    static HeaderHandler<List<ClientCookie>> clientCookieList() {
        return ClientCookieListHeaderHandler.INSTANCE;
    }

    /**
     * {@see ContentDispositionHeaderHandler}
     */
    static HeaderHandler<ContentDisposition> contentDisposition() {
        return ContentDispositionHeaderHandler.INSTANCE;
    }

    /**
     * {@see ContentDispositionFileNameEncodedHeaderHandler}
     */
    static HeaderHandler<ContentDispositionFileName> contentDispositionFilename() {
        return ContentDispositionFileNameEncodedHeaderHandler.INSTANCE;
    }

    /**
     * {@see ContentLanguageHeaderHandler}
     */
    static HeaderHandler<ContentLanguage> contentLanguage() {
        return ContentLanguageHeaderHandler.INSTANCE;
    }

    /**
     * {@see ContentRangeHeaderHandler}
     */
    static HeaderHandler<ContentRange> contentRange() {
        return ContentRangeHeaderHandler.INSTANCE;
    }

    /**
     * {@see EmailAddressHeaderHandler}
     */
    static HeaderHandler<EmailAddress> emailAddress() {
        return EmailAddressHeaderHandler.INSTANCE;
    }

    /**
     * {@see EncodedTextHeaderHandler}
     */
    static HeaderHandler<EncodedText> encodedText() {
        return EncodedTextHeaderHandler.INSTANCE;
    }

    /**
     * {@see ETagHeaderHandler}
     */
    static HeaderHandler<ETag> eTag() {
        return ETagHeaderHandler.INSTANCE;
    }

    /**
     * {@see ETagListHeaderHandler}
     */
    static HeaderHandler<List<ETag>> eTagList() {
        return ETagListHeaderHandler.INSTANCE;
    }

    /**
     * {@see HttpHeaderNameListHeaderHandler}
     */
    static HeaderHandler<List<HttpHeaderName<?>>> httpHeaderNameList() {
        return HttpHeaderNameListHeaderHandler.INSTANCE;
    }

    /**
     * {@see IfRangeHeaderHandler}
     */
    static HeaderHandler<IfRange<?>> ifRange() {
        return IfRangeHeaderHandler.INSTANCE;
    }

    /**
     * {@see LanguageNameHeaderHandler}
     */
    static HeaderHandler<LanguageName> languageName() {
        return LanguageNameHeaderHandler.INSTANCE;
    }

    /**
     * {@see LinkHeaderHandler}
     */
    static HeaderHandler<List<Link>> link() {
        return LinkHeaderHandler.INSTANCE;
    }

    /**
     * {@see LocalDateTimeHeaderHandler}
     */
    static HeaderHandler<LocalDateTime> localDateTime() {
        return LocalDateTimeHeaderHandler.INSTANCE;
    }

    /**
     * {@see LongHeaderHandler}
     */
    static HeaderHandler<Long> longHandler() {
        return LongHeaderHandler.INSTANCE;
    }

    /**
     * {@see MediaTypeHeaderHandler}
     */
    static HeaderHandler<MediaType> mediaType() {
        return MediaTypeHeaderHandler.INSTANCE;
    }

    /**
     * {@see HttpMethodHeaderHandler}
     */
    static HeaderHandler<HttpMethod> method() {
        return HttpMethodHeaderHandler.INSTANCE;
    }

    /**
     * {@see HttpMethodListHeaderHandler}
     */
    static HeaderHandler<List<HttpMethod>> methodList() {
        return HttpMethodListHeaderHandler.INSTANCE;
    }

    /**
     * {@see OffsetDateTimeHeaderHandler}
     */
    static HeaderHandler<OffsetDateTime> offsetDateTime() {
        return OffsetDateTimeHeaderHandler.INSTANCE;
    }

    /**
     * {@see QuotedUnquotedStringHeaderHandler}
     */
    static HeaderHandler<String> quotedUnquotedString(final CharPredicate quotedPredicate,
                                                      final boolean supportBackslashEscaping,
                                                      final CharPredicate unquotedPredicate) {
        return QuotedUnquotedStringHeaderHandler.with(quotedPredicate, supportBackslashEscaping, unquotedPredicate);
    }

    /**
     * {@see QuotedStringHeaderHandler}
     */
    static HeaderHandler<String> quoted(final CharPredicate predicate,
                                        final boolean supportBackslashEscaping) {
        return QuotedStringHeaderHandler.with(predicate, supportBackslashEscaping);
    }

    /**
     * {@see QualityFactorHeaderHandler}
     */
    static HeaderHandler<Float> qualityFactor() {
        return QualityFactorHeaderHandler.INSTANCE;
    }

    /**
     * {@see RangeHeaderHeaderHandler}
     */
    static HeaderHandler<RangeHeader> range() {
        return RangeHeaderHeaderHandler.INSTANCE;
    }

    /**
     * {@see RangeHeaderUnitHeaderHandler}
     */
    static HeaderHandler<RangeHeaderUnit> rangeUnit() {
        return RangeHeaderUnitHeaderHandler.INSTANCE;
    }

    /**
     * {@see RelativeUrlHeaderHandler}
     */
    static HeaderHandler<RelativeUrl> relativeUrl() {
        return RelativeUrlHeaderHandler.INSTANCE;
    }

    /**
     * {@see LinkRelationHeaderHandler}
     */
    static HeaderHandler<List<LinkRelation<?>>> relation() {
        return LinkRelationHeaderHandler.INSTANCE;
    }

    /**
     * {@see ServerCookieHeaderHandler}
     */
    static HeaderHandler<ServerCookie> serverCookie() {
        return ServerCookieHeaderHandler.INSTANCE;
    }

    /**
     * {@see UnalteredStringHeaderHandler}
     */
    static HeaderHandler<String> string() {
        return UnalteredStringHeaderHandler.INSTANCE;
    }

    /**
     * {@see UnquotedStringHeaderHandler}
     */
    static HeaderHandler<String> unquoted(final CharPredicate predicate) {
        return UnquotedStringHeaderHandler.with(predicate);
    }

    /**
     * {@see UrlHeaderHandler}
     */
    static HeaderHandler<Url> url() {
        return UrlHeaderHandler.INSTANCE;
    }

    /**
     * Separator to be used when producing the text form from a list of header values.
     */
    final static String SEPARATOR = Header.SEPARATOR.string().concat(" ");

    /**
     * Package private to limit sub classing.
     */
    HeaderHandler() {
        super();
    }

    // parse ....................................................................................................

    /**
     * The entry point that accepts a value and tries to parse it.
     */
    final T parse(final String text) {
        Objects.requireNonNull(text, "text");

        try {
            return this.check(
                    this.parse0(text)
            );
        } catch (final HeaderException cause) {
            throw cause;
        } catch (final RuntimeException cause) {
            throw new HeaderException(
                    cause.getMessage(),
                    cause
            );
        }
    }

    /**
     * Sub classes parse the {@link String} value.
     */
    abstract T parse0(final String text) throws HeaderException;

    // checkValue...........................................................

    final T check(final Object value) {
        Objects.requireNonNull(value, "value");
        this.checkNonNull(value);
        return Cast.to(value);
    }

    abstract void checkNonNull(final Object value);

    /**
     * Checks the type of the given value and throws a {@link HeaderException} if this test fails.
     */
    final void checkType(final Object value,
                         final Predicate<Object> instanceofTester,
                         final Class<?> type) {
        if (false == instanceofTester.test(value)) {
            throw new HeaderException(
                    invalidTypeNameMessage(
                            value,
                            type.getSimpleName()
                    )
            );
        }
    }

    // VisibleForTesting
    final static String PACKAGE = "walkingkooka.net.header";

    /**
     * Checks the type of the given value and throws a {@link HeaderException} if this test fails.
     */
    final void checkListOfType(final Object value,
                               final Predicate<Object> instanceofTester,
                               final Class<?> type) {
        if (!(value instanceof List)) {
            throw new HeaderException(invalidTypeNameMessage(value, "List of " + type.getSimpleName()));
        }

        final List<?> list = Cast.to(value);
        for (Object element : list) {
            if (null == element) {
                throw new HeaderException("null found");
            }
            if (!instanceofTester.test(element)) {
                throw new HeaderException(
                        "Invalid element " +
                                CharSequences.quoteIfChars(element) +
                                " is not a " +
                                type.getSimpleName()
                );
            }
        }
    }

    private static String invalidTypeNameMessage(final Object value,
                                                 final String requiredType) {
        return "Invalid value type got " + value + " required " + requiredType;
    }

    // toText ....................................................................................................

    /**
     * Accepts a typed value and formats it into a http response header string.
     */
    final String toText(final T value, final Name name) {
        try {
            return this.toText0(value, name);
        } catch (final HeaderException cause) {
            throw cause;
        } catch (final RuntimeException cause) {
            throw new HeaderException("Failed to convert " + CharSequences.quote(name.value()) +
                    " value " + CharSequences.quoteIfChars(value) +
                    " to text, message: " + cause.getMessage(),
                    cause);
        }
    }

    abstract String toText0(final T value, final Name name);

    // httpHeaderNameCast ....................................................................................................

    /**
     * Casts this {@link HttpHeaderName} only if it is already giving {@link String} values.
     */
    abstract HttpHeaderName<String> httpHeaderNameCast(final HttpHeaderName<?> headerName);

    // Object ..........................................................................................

    @Override
    abstract public String toString();

    final String toStringType(final Class<?> type) {
        return type.getSimpleName();
    }

    final String toStringListOf(final Class<?> type) {
        return "List<" + type.getSimpleName() + ">";
    }
}
