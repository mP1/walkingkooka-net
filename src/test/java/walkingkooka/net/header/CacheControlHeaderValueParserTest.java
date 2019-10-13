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

public final class CacheControlHeaderValueParserTest extends HeaderValueParserTestCase<CacheControlHeaderValueParser,
        CacheControl> {

    @Test
    public void testWildcardFails() {
        this.parseStringInvalidCharacterFails("*");
    }

    @Test
    public final void testParameterSeparatorFails() {
        this.parseStringInvalidCharacterFails(";");
    }

    @Test
    public final void testKeyValueSeparatorFails() {
        this.parseStringInvalidCharacterFails("=");
    }

    @Test
    public void testSlashFails() {
        this.parseStringInvalidCharacterFails("/");
    }

    @Test
    public void testValueSeparatorFails() {
        this.parseStringInvalidCharacterFails(",");
    }

    @Test
    public void testDirective() {
        this.parseStringAndCheck2("A", "A");
    }

    @Test
    public void testDirectiveParameterSeparatorFails() {
        this.parseStringMissingParameterValueFails("A=");
    }

    @Test
    public void testDirectiveParameterSeparatorWhitespaceFails() {
        this.parseStringInvalidCharacterFails("A= ");
    }

    @Test
    public void testDirectiveWhitespaceFails() {
        this.parseStringInvalidCharacterFails("A ");
    }

    @Test
    public void testDirectiveValueSeparatorFails() {
        this.parseStringInvalidCharacterFails("A,");
    }

    @Test
    public void testDirectiveValueSeparatorSpaceFails() {
        this.parseStringInvalidCharacterFails("A, ");
    }

    @Test
    public void testDirectiveKeyValueSeparatorUnclosedQuoteFails() {
        this.parseMissingClosingQuoteFails("A=\"");
    }

    @Test
    public void testDirectiveKeyValueSeparatorUnclosedQuoteFails2() {
        this.parseMissingClosingQuoteFails("A=\"BCD");
    }

    @Test
    public void testDirectiveKeyValueSeparatorParameterNonNumericValueFails() {
        this.parseStringInvalidCharacterFails("A=B");
    }

    @Test
    public void testDirectiveKeyValueSeparatorParameterNonNumericValueFails2() {
        this.parseStringInvalidCharacterFails("A=BCD", 'B');
    }

    @Test
    public void testDirectiveKeyValueSeparatorParameterNumericValue() {
        this.parseStringAndCheck2("A=1", "A", 1L);
    }

    @Test
    public void testDirectiveKeyValueSeparatorParameterNumericValue2() {
        this.parseStringAndCheck2("A=123", "A", 123L);
    }

    @Test
    public void testDirectiveKeyValueSeparatorParameterQuoteValueQuote() {
        this.parseStringAndCheck2("A=\"B\"", "A", "B");
    }

    @Test
    public void testDirectiveKeyValueSeparatorParameterQuoteValueQuote2() {
        this.parseStringAndCheck2("A=\"BCD\"", "A", "BCD");
    }

    @Test
    public void testDirectiveKeyValueSeparatorParameterQuoteValueQuote3() {
        this.parseStringAndCheck2("A=\"1\"", "A", "1");
    }

    @Test
    public void testDirectiveKeyValueSeparatorParameterQuoteValueQuoteValueFails() {
        final String text = "A=\"1\"\"2\"";
        this.parseStringInvalidCharacterFails(text, text.indexOf('2') - 1);
    }

    @Test
    public void testDirectiveCommentKeyValueSeparatorParameterNumericValue() {
        this.parseStringAndCheck2("(abc)A=1", "A", 1L);
    }

    // max-age.....................................................

    @Test
    public void testMaxAgeWithoutSecondsFails() {
        this.parseStringMissingParameterValueFails("max-age");
    }

    @Test
    public void testMaxAgeWithoutSecondsSeparatorFails() {
        this.parseStringInvalidCharacterFails("max-age,");
    }

    @Test
    public void testMaxAgeWithoutSecondsSpaceSeparatorFails() {
        this.parseStringInvalidCharacterFails("max-age ");
    }

    @Test
    public void testMaxAgeWithoutSecondsTabSeparatorFails() {
        this.parseStringInvalidCharacterFails("max-age\t");
    }

    @Test
    public void testMaxAgeInvalidCharacterFails() {
        this.parseStringInvalidCharacterFails("max-age=!");
    }

    @Test
    public void testMaxAgeInvalidCharacterFails2() {
        this.parseStringInvalidCharacterFails("max-age=1!");
    }

    @Test
    public void testMaxAgeWithSeconds() {
        this.parseStringAndCheck2("max-age=1", "max-age", 1L);
    }

    @Test
    public void testMaxAgeWithSeconds2() {
        this.parseStringAndCheck2("max-age=23", "max-age", 23L);
    }

    // max-stale.....................................................

    @Test
    public void testMaxStaleWithoutSeconds() {
        this.parseStringAndCheck2("max-stale", "max-stale");
    }

    @Test
    public void testMaxStaleWithoutSecondsSeparatorFails() {
        this.parseStringInvalidCharacterFails("max-stale,");
    }

    @Test
    public void testMaxStaleWithoutSecondsSpace() {
        this.parseStringInvalidCharacterFails("max-stale ");
    }

    @Test
    public void testMaxStaleWithoutSecondsTab() {
        this.parseStringInvalidCharacterFails("max-stale\t");
    }

    @Test
    public void testMaxStaleInvalidCharacterFails() {
        this.parseStringInvalidCharacterFails("max-stale=!");
    }

    @Test
    public void testMaxStaleInvalidCharacterFails2() {
        this.parseStringInvalidCharacterFails("max-stale=1!");
    }

    @Test
    public void testMaxStaleWithSeconds() {
        this.parseStringAndCheck2("max-stale=1", "max-stale", 1L);
    }

    @Test
    public void testMaxStaleWithSeconds2() {
        this.parseStringAndCheck2("max-stale=23", "max-stale", 23L);
    }

    // must-revalidate.....................................................

    @Test
    public void testMustRevalidateWithoutSecondsSpaceFails() {
        this.parseStringInvalidCharacterFails("must-revalidate ");
    }

    @Test
    public void testMustRevalidateWithoutSecondsTabFails() {
        this.parseStringInvalidCharacterFails("must-revalidate\t");
    }

    @Test
    public void testMustRevalidateParameterSeparatorFails() {
        this.parseStringMissingParameterValueFails("must-revalidate=");
    }

    @Test
    public void testMustRevalidateSeparatorFails() {
        this.parseStringInvalidCharacterFails("must-revalidate,");
    }

    @Test
    public void testMustRevalidate() {
        this.parseStringAndCheck2("must-revalidate", "must-revalidate");
    }

    // no-cache.....................................................

    @Test
    public void testNoCacheWithoutSecondsSpaceFails() {
        this.parseStringInvalidCharacterFails("no-cache ");
    }

    @Test
    public void testNoCacheWithoutSecondsTabFails() {
        this.parseStringInvalidCharacterFails("no-cache\t");
    }

    @Test
    public void testNoCacheParameterSeparatorFails() {
        this.parseStringMissingParameterValueFails("no-cache=");
    }

    @Test
    public void testNoCacheSeparatorFails() {
        this.parseStringInvalidCharacterFails("no-cache,");
    }

    @Test
    public void testNoCache() {
        this.parseStringAndCheck2("no-cache", "no-cache");
    }

    // no-store.....................................................

    @Test
    public void testNoStoreWithoutSecondsSpaceFails() {
        this.parseStringInvalidCharacterFails("no-store ");
    }

    @Test
    public void testNoStoreWithoutSecondsTabFails() {
        this.parseStringInvalidCharacterFails("no-store\t");
    }

    @Test
    public void testNoStoreParameterSeparatorFails() {
        this.parseStringMissingParameterValueFails("no-store=");
    }

    @Test
    public void testNoStoreSeparatorFails() {
        this.parseStringInvalidCharacterFails("no-store,");
    }

    @Test
    public void testNoStore() {
        this.parseStringAndCheck2("no-store", "no-store");
    }

    // no-transform.....................................................

    @Test
    public void testNoTransformWithoutSecondsSpaceFails() {
        this.parseStringInvalidCharacterFails("no-transform ");
    }

    @Test
    public void testNoTransformWithoutSecondsTabFails() {
        this.parseStringInvalidCharacterFails("no-transform\t");
    }

    @Test
    public void testNoTransformParameterSeparatorFails() {
        this.parseStringMissingParameterValueFails("no-transform=");
    }

    @Test
    public void testNoTransformSeparatorFails() {
        this.parseStringInvalidCharacterFails("no-transform,");
    }

    @Test
    public void testNoTransform() {
        this.parseStringAndCheck2("no-transform", "no-transform");
    }

    // public.....................................................

    @Test
    public void testPublicWithoutSecondsSpaceFails() {
        this.parseStringInvalidCharacterFails("public ");
    }

    @Test
    public void testPublicWithoutSecondsTabFails() {
        this.parseStringInvalidCharacterFails("public\t");
    }

    @Test
    public void testPublicParameterSeparatorFails() {
        this.parseStringMissingParameterValueFails("public=");
    }

    @Test
    public void testPublicSeparatorFails() {
        this.parseStringInvalidCharacterFails("public,");
    }

    @Test
    public void testPublic() {
        this.parseStringAndCheck2("public", "public");
    }

    // private.....................................................

    @Test
    public void testPrivateWithoutSecondsSpaceFails() {
        this.parseStringInvalidCharacterFails("private ");
    }

    @Test
    public void testPrivateWithoutSecondsTabFails() {
        this.parseStringInvalidCharacterFails("private\t");
    }

    @Test
    public void testPrivateParameterSeparatorFails() {
        this.parseStringMissingParameterValueFails("private=");
    }

    @Test
    public void testPrivateSeparatorFails() {
        this.parseStringInvalidCharacterFails("private,");
    }

    @Test
    public void testPrivate() {
        this.parseStringAndCheck2("private", "private");
    }

    // proxy-revalidate.....................................................

    @Test
    public void testProxyRevalidateWithoutSecondsSpaceFails() {
        this.parseStringInvalidCharacterFails("proxy-revalidate ");
    }

    @Test
    public void testProxyRevalidateWithoutSecondsTabFails() {
        this.parseStringInvalidCharacterFails("proxy-revalidate\t");
    }

    @Test
    public void testProxyRevalidateParameterSeparatorFails() {
        this.parseStringMissingParameterValueFails("proxy-revalidate=");
    }

    @Test
    public void testProxyRevalidateSeparatorFails() {
        this.parseStringInvalidCharacterFails("proxy-revalidate,");
    }

    @Test
    public void testProxyRevalidate() {
        this.parseStringAndCheck2("proxy-revalidate", "proxy-revalidate");
    }

    // s-maxage.....................................................

    @Test
    public void testSmaxAgeWithoutSecondsFails() {
        this.parseStringMissingParameterValueFails("s-maxage", 8);
    }

    @Test
    public void testSmaxAgeWithoutSecondsSeparatorFails() {
        this.parseStringInvalidCharacterFails("s-maxage,");
    }

    @Test
    public void testSmaxAgeWithoutSecondsSpaceFails() {
        this.parseStringInvalidCharacterFails("s-maxage ");
    }

    @Test
    public void testSmaxAgeWithoutSecondsTabFails() {
        this.parseStringInvalidCharacterFails("s-maxage\t");
    }

    @Test
    public void testSmaxAgeInvalidCharacterFails() {
        this.parseStringInvalidCharacterFails("s-maxage=!");
    }

    @Test
    public void testSmaxAgeInvalidCharacterFails2() {
        this.parseStringInvalidCharacterFails("s-maxage=1!");
    }

    @Test
    public void testSmaxAgeWithSeconds() {
        this.parseStringAndCheck2("s-maxage=1", "s-maxage", 1L);
    }

    @Test
    public void testSmaxAgeWithSeconds2() {
        this.parseStringAndCheck2("s-maxage=23", "s-maxage", 23L);
    }

    // custom .....................................................................

    @Test
    public void testCustomQuotedValues() {
        this.parseStringAndCheck2("custom=\"abc\"",
                "custom",
                "abc");
    }

    @Test
    public void testCustomUnquotedValuesFails() {
        this.parseStringInvalidCharacterFails("custom=abc", 'a');
    }

    @Test
    public void testImmutable() {
        this.parseStringAndCheck2("immutable", "immutable");
    }

    @Test
    public void testStaleWhileRevalidateNumber() {
        this.parseStringAndCheck2("stale-while-revalidate=123", "stale-while-revalidate", 123L);
    }

    @Test
    public void testStaleIfError() {
        this.parseStringAndCheck2("stale-if-error=456", "stale-if-error", 456L);
    }

    // several ....................................................................

    @Test
    public void testMaxAgeSeparatorNoCache() {
        this.parseStringAndCheck3("max-age=123,no-cache",
                CacheControlDirectiveName.MAX_AGE.setParameter(Optional.of(123L)),
                CacheControlDirective.NO_CACHE);
    }

    @Test
    public void testMaxAgeSpaceNoCache() {
        this.parseStringAndCheck3("max-age=123, no-cache",
                CacheControlDirectiveName.MAX_AGE.setParameter(Optional.of(123L)),
                CacheControlDirective.NO_CACHE);
    }

    @Test
    public void testMaxAgeTabNoCache() {
        this.parseStringAndCheck3("max-age=123,\tno-cache",
                CacheControlDirectiveName.MAX_AGE.setParameter(Optional.of(123L)),
                CacheControlDirective.NO_CACHE);
    }

    @Test
    public void testMaxAgeSeparatorMaxStale() {
        this.parseStringAndCheck3("max-age=123,max-stale=456",
                CacheControlDirectiveName.MAX_AGE.setParameter(Optional.of(123L)),
                CacheControlDirectiveName.MAX_STALE.setParameter(Optional.of(456L)));
    }

    @Test
    public void testNoCacheSeparatorNoStoreSeparatorNoTransform() {
        this.parseStringAndCheck3("no-cache,no-store,no-transform",
                CacheControlDirective.NO_CACHE,
                CacheControlDirective.NO_STORE,
                CacheControlDirective.NO_TRANSFORM);
    }

    @Test
    public void testNoCacheSeparatorSpaceNoStoreSeparatorSpaceNoTransform() {
        this.parseStringAndCheck3("no-cache, no-store, no-transform",
                CacheControlDirective.NO_CACHE,
                CacheControlDirective.NO_STORE,
                CacheControlDirective.NO_TRANSFORM);
    }

    @Test
    public void testNoCacheSeparatorTabNoStoreSeparatorTabNoTransform() {
        this.parseStringAndCheck3("no-cache, no-store, no-transform",
                CacheControlDirective.NO_CACHE,
                CacheControlDirective.NO_STORE,
                CacheControlDirective.NO_TRANSFORM);
    }

    @Test
    public void testNoCacheSeparatorSpaceTabSpaceTabNoStoreSeparatorNoTransform() {
        this.parseStringAndCheck3("no-cache, \t \tno-store,no-transform",
                CacheControlDirective.NO_CACHE,
                CacheControlDirective.NO_STORE,
                CacheControlDirective.NO_TRANSFORM);
    }

    @Test
    public void testNoCacheSeparatorSpaceCrNlNoStoreSeparatorNoTransform() {
        this.parseStringAndCheck3("no-cache,\r\n no-store,no-transform",
                CacheControlDirective.NO_CACHE,
                CacheControlDirective.NO_STORE,
                CacheControlDirective.NO_TRANSFORM);
    }

    @Test
    public void testExtensionQuotedParameterSeparatorNoTransform() {
        this.parseStringAndCheck3("extension=\"abc\",no-transform",
                CacheControlDirectiveName.with("extension").setParameter(Cast.to(Optional.of("abc"))),
                CacheControlDirective.NO_TRANSFORM);
    }

    // helpers .........................................................................................................

    private void parseStringAndCheck2(final String text,
                                final String directive) {
        this.parseStringAndCheck3(text,
                CacheControlDirective.with(CacheControlDirectiveName.with(directive), Optional.empty()));
    }

    private void parseStringAndCheck2(final String text,
                                final String directive,
                                final Object value) {
        this.parseStringAndCheck3(text,
                CacheControlDirective.with(Cast.to(CacheControlDirectiveName.with(directive)), Optional.of(value)));
    }

    private void parseStringAndCheck3(final String text,
                                      final CacheControlDirective<?>... directives) {
        this.parseStringAndCheck(text, CacheControl.with(Lists.of(directives)));
    }

    @Override
    public CacheControl parseString(final String text) {
        return CacheControlHeaderValueParser.parseCacheControl(text);
    }

    @Override
    String valueLabel() {
        return CacheControlHeaderValueParser.DIRECTIVE;
    }

    @Override
    public Class<CacheControlHeaderValueParser> type() {
        return CacheControlHeaderValueParser.class;
    }
}
