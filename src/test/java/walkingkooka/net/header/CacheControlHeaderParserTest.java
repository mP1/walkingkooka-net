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

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;

import java.util.Optional;

public final class CacheControlHeaderParserTest extends HeaderParserTestCase<CacheControlHeaderParser,
    CacheControl> {

    private static final Optional<Long> MAX_AGE = Optional.of(123L);
    private static final Optional<Long> MAX_STALE = Optional.of(456L);

    @Test
    public void testParseWildcardFails() {
        this.parseStringInvalidCharacterFails("*");
    }

    @Test
    public void testParseParameterSeparatorFails() {
        this.parseStringInvalidCharacterFails(";");
    }

    @Test
    public void testParseKeyValueSeparatorFails() {
        this.parseStringInvalidCharacterFails("=");
    }

    @Test
    public void testParseSlashFails() {
        this.parseStringInvalidCharacterFails("/");
    }

    @Test
    public void testParseValueSeparatorFails() {
        this.parseStringInvalidCharacterFails(",");
    }

    @Test
    public void testParseDirective() {
        this.parseStringAndCheck2("A", "A");
    }

    @Test
    public void testParseDirectiveParameterSeparatorFails() {
        this.parseStringMissingParameterValueFails("A=");
    }

    @Test
    public void testParseDirectiveParameterSeparatorWhitespaceFails() {
        this.parseStringInvalidCharacterFails("A= ");
    }

    @Test
    public void testParseDirectiveWhitespaceFails() {
        this.parseStringInvalidCharacterFails("A ");
    }

    @Test
    public void testParseDirectiveValueSeparatorFails() {
        this.parseStringInvalidCharacterFails("A,");
    }

    @Test
    public void testParseDirectiveValueSeparatorSpaceFails() {
        this.parseStringInvalidCharacterFails("A, ");
    }

    @Test
    public void testParseDirectiveKeyValueSeparatorUnclosedQuoteFails() {
        this.parseMissingClosingQuoteFails("A=\"");
    }

    @Test
    public void testParseDirectiveKeyValueSeparatorUnclosedQuoteFails2() {
        this.parseMissingClosingQuoteFails("A=\"BCD");
    }

    @Test
    public void testParseDirectiveKeyValueSeparatorParameterNonNumericValueFails() {
        this.parseStringInvalidCharacterFails("A=B");
    }

    @Test
    public void testParseDirectiveKeyValueSeparatorParameterNonNumericValueFails2() {
        this.parseStringInvalidCharacterFails(
            "A=BCD",
            'B'
        );
    }

    @Test
    public void testParseDirectiveKeyValueSeparatorParameterNumericValue() {
        this.parseStringAndCheck2(
            "A=1",
            "A",
            1L
        );
    }

    @Test
    public void testParseDirectiveKeyValueSeparatorParameterNumericValue2() {
        this.parseStringAndCheck2(
            "A=123",
            "A",
            123L
        );
    }

    @Test
    public void testParseDirectiveKeyValueSeparatorParameterQuoteValueQuote() {
        this.parseStringAndCheck2(
            "A=\"B\"",
            "A",
            "B"
        );
    }

    @Test
    public void testParseDirectiveKeyValueSeparatorParameterQuoteValueQuote2() {
        this.parseStringAndCheck2(
            "A=\"BCD\"",
            "A",
            "BCD"
        );
    }

    @Test
    public void testParseDirectiveKeyValueSeparatorParameterQuoteValueQuote3() {
        this.parseStringAndCheck2(
            "A=\"1\"",
            "A",
            "1"
        );
    }

    @Test
    public void testParseDirectiveKeyValueSeparatorParameterQuoteValueQuoteValueFails() {
        final String text = "A=\"1\"\"2\"";
        this.parseStringInvalidCharacterFails(
            text,
            text.indexOf('2') - 1
        );
    }

    @Test
    public void testParseDirectiveCommentKeyValueSeparatorParameterNumericValue() {
        this.parseStringAndCheck2(
            "(abc)A=1",
            "A",
            1L
        );
    }

    // max-age..........................................................................................................

    @Test
    public void testParseMaxAgeWithoutSecondsFails() {
        this.parseStringMissingParameterValueFails("max-age");
    }

    @Test
    public void testParseMaxAgeWithoutSecondsSeparatorFails() {
        this.parseStringInvalidCharacterFails("max-age,");
    }

    @Test
    public void testParseMaxAgeWithoutSecondsSpaceSeparatorFails() {
        this.parseStringInvalidCharacterFails("max-age ");
    }

    @Test
    public void testParseMaxAgeWithoutSecondsTabSeparatorFails() {
        this.parseStringInvalidCharacterFails("max-age\t");
    }

    @Test
    public void testParseMaxAgeInvalidCharacterFails() {
        this.parseStringInvalidCharacterFails("max-age=!");
    }

    @Test
    public void testParseMaxAgeInvalidCharacterFails2() {
        this.parseStringInvalidCharacterFails("max-age=1!");
    }

    @Test
    public void testParseMaxAgeWithSeconds() {
        this.parseStringAndCheck2(
            "max-age=1",
            "max-age",
            1L
        );
    }

    @Test
    public void testParseMaxAgeWithSeconds2() {
        this.parseStringAndCheck2(
            "max-age=23",
            "max-age",
            23L
        );
    }

    // max-stale........................................................................................................

    @Test
    public void testParseMaxStaleWithoutSeconds() {
        this.parseStringAndCheck2("max-stale", "max-stale");
    }

    @Test
    public void testParseMaxStaleWithoutSecondsSeparatorFails() {
        this.parseStringInvalidCharacterFails("max-stale,");
    }

    @Test
    public void testParseMaxStaleWithoutSecondsSpace() {
        this.parseStringInvalidCharacterFails("max-stale ");
    }

    @Test
    public void testParseMaxStaleWithoutSecondsTab() {
        this.parseStringInvalidCharacterFails("max-stale\t");
    }

    @Test
    public void testParseMaxStaleInvalidCharacterFails() {
        this.parseStringInvalidCharacterFails("max-stale=!");
    }

    @Test
    public void testParseMaxStaleInvalidCharacterFails2() {
        this.parseStringInvalidCharacterFails("max-stale=1!");
    }

    @Test
    public void testParseMaxStaleWithSeconds() {
        this.parseStringAndCheck2(
            "max-stale=1",
            "max-stale",
            1L
        );
    }

    @Test
    public void testParseMaxStaleWithSeconds2() {
        this.parseStringAndCheck2(
            "max-stale=23",
            "max-stale",
            23L
        );
    }

    // must-revalidate..................................................................................................

    @Test
    public void testParseMustRevalidateWithoutSecondsSpaceFails() {
        this.parseStringInvalidCharacterFails("must-revalidate ");
    }

    @Test
    public void testParseMustRevalidateWithoutSecondsTabFails() {
        this.parseStringInvalidCharacterFails("must-revalidate\t");
    }

    @Test
    public void testParseMustRevalidateParameterSeparatorFails() {
        this.parseStringMissingParameterValueFails("must-revalidate=");
    }

    @Test
    public void testParseMustRevalidateSeparatorFails() {
        this.parseStringInvalidCharacterFails("must-revalidate,");
    }

    @Test
    public void testParseMustRevalidate() {
        this.parseStringAndCheck2(
            "must-revalidate",
            "must-revalidate"
        );
    }

    // no-cache.........................................................................................................

    @Test
    public void testParseNoCacheWithoutSecondsSpaceFails() {
        this.parseStringInvalidCharacterFails("no-cache ");
    }

    @Test
    public void testParseNoCacheWithoutSecondsTabFails() {
        this.parseStringInvalidCharacterFails("no-cache\t");
    }

    @Test
    public void testParseNoCacheParameterSeparatorFails() {
        this.parseStringMissingParameterValueFails("no-cache=");
    }

    @Test
    public void testParseNoCacheSeparatorFails() {
        this.parseStringInvalidCharacterFails("no-cache,");
    }

    @Test
    public void testParseNoCache() {
        this.parseStringAndCheck2(
            "no-cache",
            "no-cache"
        );
    }

    // no-store.........................................................................................................

    @Test
    public void testParseNoStoreWithoutSecondsSpaceFails() {
        this.parseStringInvalidCharacterFails("no-store ");
    }

    @Test
    public void testParseNoStoreWithoutSecondsTabFails() {
        this.parseStringInvalidCharacterFails("no-store\t");
    }

    @Test
    public void testParseNoStoreParameterSeparatorFails() {
        this.parseStringMissingParameterValueFails("no-store=");
    }

    @Test
    public void testParseNoStoreSeparatorFails() {
        this.parseStringInvalidCharacterFails("no-store,");
    }

    @Test
    public void testParseNoStore() {
        this.parseStringAndCheck2("no-store", "no-store");
    }

    // no-transform.....................................................................................................

    @Test
    public void testParseNoTransformWithoutSecondsSpaceFails() {
        this.parseStringInvalidCharacterFails("no-transform ");
    }

    @Test
    public void testParseNoTransformWithoutSecondsTabFails() {
        this.parseStringInvalidCharacterFails("no-transform\t");
    }

    @Test
    public void testParseNoTransformParameterSeparatorFails() {
        this.parseStringMissingParameterValueFails("no-transform=");
    }

    @Test
    public void testParseNoTransformSeparatorFails() {
        this.parseStringInvalidCharacterFails("no-transform,");
    }

    @Test
    public void testParseNoTransform() {
        this.parseStringAndCheck2(
            "no-transform",
            "no-transform"
        );
    }

    // public...........................................................................................................

    @Test
    public void testParsePublicWithoutSecondsSpaceFails() {
        this.parseStringInvalidCharacterFails("public ");
    }

    @Test
    public void testParsePublicWithoutSecondsTabFails() {
        this.parseStringInvalidCharacterFails("public\t");
    }

    @Test
    public void testParsePublicParameterSeparatorFails() {
        this.parseStringMissingParameterValueFails("public=");
    }

    @Test
    public void testParsePublicSeparatorFails() {
        this.parseStringInvalidCharacterFails("public,");
    }

    @Test
    public void testParsePublic() {
        this.parseStringAndCheck2(
            "public",
            "public"
        );
    }

    // private..........................................................................................................

    @Test
    public void testParsePrivateWithoutSecondsSpaceFails() {
        this.parseStringInvalidCharacterFails("private ");
    }

    @Test
    public void testParsePrivateWithoutSecondsTabFails() {
        this.parseStringInvalidCharacterFails("private\t");
    }

    @Test
    public void testParsePrivateParameterSeparatorFails() {
        this.parseStringMissingParameterValueFails("private=");
    }

    @Test
    public void testParsePrivateSeparatorFails() {
        this.parseStringInvalidCharacterFails("private,");
    }

    @Test
    public void testParsePrivate() {
        this.parseStringAndCheck2(
            "private",
            "private"
        );
    }

    // proxy-revalidate.................................................................................................

    @Test
    public void testParseProxyRevalidateWithoutSecondsSpaceFails() {
        this.parseStringInvalidCharacterFails("proxy-revalidate ");
    }

    @Test
    public void testParseProxyRevalidateWithoutSecondsTabFails() {
        this.parseStringInvalidCharacterFails("proxy-revalidate\t");
    }

    @Test
    public void testParseProxyRevalidateParameterSeparatorFails() {
        this.parseStringMissingParameterValueFails("proxy-revalidate=");
    }

    @Test
    public void testParseProxyRevalidateSeparatorFails() {
        this.parseStringInvalidCharacterFails("proxy-revalidate,");
    }

    @Test
    public void testParseProxyRevalidate() {
        this.parseStringAndCheck2(
            "proxy-revalidate",
            "proxy-revalidate"
        );
    }

    // s-maxage.........................................................................................................

    @Test
    public void testParseSmaxAgeWithoutSecondsFails() {
        this.parseStringMissingParameterValueFails("s-maxage", 8);
    }

    @Test
    public void testParseSmaxAgeWithoutSecondsSeparatorFails() {
        this.parseStringInvalidCharacterFails("s-maxage,");
    }

    @Test
    public void testParseSmaxAgeWithoutSecondsSpaceFails() {
        this.parseStringInvalidCharacterFails("s-maxage ");
    }

    @Test
    public void testParseSmaxAgeWithoutSecondsTabFails() {
        this.parseStringInvalidCharacterFails("s-maxage\t");
    }

    @Test
    public void testParseSmaxAgeInvalidCharacterFails() {
        this.parseStringInvalidCharacterFails("s-maxage=!");
    }

    @Test
    public void testParseSmaxAgeInvalidCharacterFails2() {
        this.parseStringInvalidCharacterFails("s-maxage=1!");
    }

    @Test
    public void testParseSmaxAgeWithSeconds() {
        this.parseStringAndCheck2(
            "s-maxage=1",
            "s-maxage",
            1L
        );
    }

    @Test
    public void testParseSmaxAgeWithSeconds2() {
        this.parseStringAndCheck2(
            "s-maxage=23",
            "s-maxage",
            23L
        );
    }

    // custom ..........................................................................................................

    @Test
    public void testParseCustomQuotedValues() {
        this.parseStringAndCheck2(
            "custom=\"abc\"",
            "custom",
            "abc"
        );
    }

    @Test
    public void testParseCustomUnquotedValuesFails() {
        this.parseStringInvalidCharacterFails(
            "custom=abc",
            'a'
        );
    }

    @Test
    public void testParseImmutable() {
        this.parseStringAndCheck2(
            "immutable",
            "immutable"
        );
    }

    @Test
    public void testParseStaleWhileRevalidateNumber() {
        this.parseStringAndCheck2(
            "stale-while-revalidate=123",
            "stale-while-revalidate",
            123L
        );
    }

    @Test
    public void testParseStaleIfError() {
        this.parseStringAndCheck2(
            "stale-if-error=456",
            "stale-if-error",
            456L
        );
    }

    // several .........................................................................................................

    @Test
    public void testParseMaxAgeSeparatorNoCache() {
        this.parseStringAndCheck3(
            "max-age=123,no-cache",
            CacheControlDirectiveName.MAX_AGE.setParameter(MAX_AGE),
            CacheControlDirective.NO_CACHE
        );
    }

    @Test
    public void testParseMaxAgeSpaceNoCache() {
        this.parseStringAndCheck3(
            "max-age=123, no-cache",
            CacheControlDirectiveName.MAX_AGE.setParameter(MAX_AGE),
            CacheControlDirective.NO_CACHE
        );
    }

    @Test
    public void testParseMaxAgeTabNoCache() {
        this.parseStringAndCheck3(
            "max-age=123,\tno-cache",
            CacheControlDirectiveName.MAX_AGE.setParameter(MAX_AGE),
            CacheControlDirective.NO_CACHE
        );
    }

    @Test
    public void testParseMaxAgeSeparatorMaxStale() {
        this.parseStringAndCheck3(
            "max-age=123,max-stale=456",
            CacheControlDirectiveName.MAX_AGE.setParameter(MAX_AGE),
            CacheControlDirectiveName.MAX_STALE.setParameter(MAX_STALE)
        );
    }

    @Test
    public void testParseNoCacheSeparatorNoStoreSeparatorNoTransform() {
        this.parseStringAndCheck3(
            "no-cache,no-store,no-transform",
            CacheControlDirective.NO_CACHE,
            CacheControlDirective.NO_STORE,
            CacheControlDirective.NO_TRANSFORM
        );
    }

    @Test
    public void testParseNoCacheSeparatorSpaceNoStoreSeparatorSpaceNoTransform() {
        this.parseStringAndCheck3(
            "no-cache, no-store, no-transform",
            CacheControlDirective.NO_CACHE,
            CacheControlDirective.NO_STORE,
            CacheControlDirective.NO_TRANSFORM
        );
    }

    @Test
    public void testParseNoCacheSeparatorTabNoStoreSeparatorTabNoTransform() {
        this.parseStringAndCheck3(
            "no-cache, no-store, no-transform",
            CacheControlDirective.NO_CACHE,
            CacheControlDirective.NO_STORE,
            CacheControlDirective.NO_TRANSFORM
        );
    }

    @Test
    public void testParseNoCacheSeparatorSpaceTabSpaceTabNoStoreSeparatorNoTransform() {
        this.parseStringAndCheck3(
            "no-cache, \t \tno-store,no-transform",
            CacheControlDirective.NO_CACHE,
            CacheControlDirective.NO_STORE,
            CacheControlDirective.NO_TRANSFORM
        );
    }

    @Test
    public void testParseNoCacheSeparatorSpaceCrNlNoStoreSeparatorNoTransform() {
        this.parseStringAndCheck3(
            "no-cache,\r\n no-store,no-transform",
            CacheControlDirective.NO_CACHE,
            CacheControlDirective.NO_STORE,
            CacheControlDirective.NO_TRANSFORM
        );
    }

    @Test
    public void testParseExtensionQuotedParameterSeparatorNoTransform() {
        this.parseStringAndCheck3(
            "extension=\"abc\",no-transform",
            CacheControlDirectiveName.with("extension")
                .setParameter(
                    Cast.to(
                        Optional.of("abc")
                    )
                ),
            CacheControlDirective.NO_TRANSFORM
        );
    }

    // helpers .........................................................................................................

    private void parseStringAndCheck2(final String text,
                                      final String directive) {
        this.parseStringAndCheck3(
            text,
            CacheControlDirective.with(
                CacheControlDirectiveName.with(directive),
                Optional.empty()
            )
        );
    }

    private void parseStringAndCheck2(final String text,
                                      final String directive,
                                      final Object value) {
        this.parseStringAndCheck3(
            text,
            CacheControlDirective.with(
                Cast.to(
                    CacheControlDirectiveName.with(directive)
                ),
                Optional.of(value)
            )
        );
    }

    private void parseStringAndCheck3(final String text,
                                      final CacheControlDirective<?>... directives) {
        this.parseStringAndCheck(
            text,
            CacheControl.with(
                Lists.of(directives)
            )
        );
    }

    @Override
    public CacheControl parseString(final String text) {
        return CacheControlHeaderParser.parseCacheControl(text);
    }

    @Override
    String valueLabel() {
        return CacheControlHeaderParser.DIRECTIVE;
    }

    @Override
    public Class<CacheControlHeaderParser> type() {
        return CacheControlHeaderParser.class;
    }
}
