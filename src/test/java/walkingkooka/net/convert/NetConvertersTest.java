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

package walkingkooka.net.convert;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterTesting;
import walkingkooka.convert.Converters;
import walkingkooka.convert.FakeConverterContext;
import walkingkooka.net.HostAddress;
import walkingkooka.net.Url;
import walkingkooka.net.UrlFragment;
import walkingkooka.net.UrlQueryString;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticHelperTesting;

import java.lang.reflect.Method;
import java.util.function.Function;

public final class NetConvertersTest implements ConverterTesting,
    PublicStaticHelperTesting<NetConverters> {

    @Test
    public void testNetConvertAbsoluteUrlToHostAddress() {
        this.netConvertAndCheck(
            Url.parseAbsolute("http://example.com"),
            HostAddress.with("example.com")
        );
    }

    @Test
    public void testNetConvertEmailAddressToHostAddress() {
        this.netConvertAndCheck(
            EmailAddress.parse("user@example.com"),
            HostAddress.with("example.com")
        );
    }

    @Test
    public void testNetConvertMailToUrlToHostAddress() {
        this.netConvertAndCheck(
            Url.parseMailTo("mailto:user@example.com"),
            HostAddress.with("example.com")
        );
    }

    @Test
    public void testNetConvertCharSequenceToAbsoluteUrl() {
        final String url = "http://example.com/path1?k1=v1#fragment111";

        this.netConvertAndCheck(
            new StringBuilder(url),
            Url.parseAbsolute(url)
        );
    }

    @Test
    public void testNetConvertStringToAbsoluteUrl() {
        this.netConvertAndCheck(
            "http://example.com",
            Url::parseAbsolute
        );
    }

    @Test
    public void testNetConvertStringToEmailAddress() {
        this.netConvertAndCheck(
            "user@example.com",
            EmailAddress::parse
        );
    }

    @Test
    public void testNetConvertStringToMailToUrl() {
        this.netConvertAndCheck(
            "mailto:user@example.com",
            Url::parseMailTo
        );
    }

    @Test
    public void testNetConvertStringToRelativeUrl() {
        this.netConvertAndCheck(
            "/path1/path2",
            Url::parseRelative
        );
    }

    @Test
    public void testNetConvertStringToUrl() {
        this.netConvertAndCheck(
            "http://example.com",
            Url::parse
        );
    }

    @Test
    public void testNetConvertStringToUrlFragment() {
        this.netConvertAndCheck(
            "/fragment1/fragment2",
            UrlFragment::parse
        );
    }

    @Test
    public void testNetConvertStringToUrlQueryString() {
        this.netConvertAndCheck(
            "k1=v1&k2=v2",
            UrlQueryString::parse
        );
    }

    private void netConvertAndCheck(final String value,
                                    final Function<String, Object> expected) {
        this.netConvertAndCheck(
            value,
            expected.apply(value)
        );
    }

    private void netConvertAndCheck(final Object value,
                                    final Object expected) {
        this.convertAndCheck(
            NetConverters.net(),
            value,
            Cast.to(
                expected.getClass()
            ),
            new FakeConverterContext() {

                @Override
                public boolean canConvert(final Object value,
                                          final Class<?> type) {
                    return this.converter.canConvert(
                        value,
                        type,
                        this
                    );
                }

                @Override
                public <T> Either<T, String> convert(final Object value,
                                                     final Class<T> target) {
                    return this.converter.convert(
                        value,
                        target,
                        this
                    );
                }

                private final Converter<FakeConverterContext> converter = Converters.collection(
                    Lists.of(
                        Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString(),
                        Converters.simple(),
                        NetConverters.net()
                    )
                );
            },
            expected
        );
    }

    @Test
    public void testNetToString() {
        this.checkEquals(
            "net",
            NetConverters.net()
                .toString()
        );
    }

    // class............................................................................................................

    @Override
    public Class<NetConverters> type() {
        return NetConverters.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public boolean canHavePublicTypes(final Method method) {
        return false;
    }
}
