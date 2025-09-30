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
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterTesting2;
import walkingkooka.convert.Converters;
import walkingkooka.convert.FakeConverterContext;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.HasHostAddress;
import walkingkooka.net.HostAddress;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;

public class NetConverterTextToHasHostAddressTest implements ConverterTesting2<NetConverterTextToHasHostAddress<FakeConverterContext>, FakeConverterContext>,
    ToStringTesting<NetConverterTextToHasHostAddress<FakeConverterContext>> {

    @Test
    public void testConvertNullToAbsoluteUrlFails() {
        this.convertFails(
            null,
            AbsoluteUrl.class
        );
    }

    @Test
    public void testConvertNullToEmailAddressFails() {
        this.convertFails(
            null,
            EmailAddress.class
        );
    }

    @Test
    public void testConvertNullToHasHostAddressFails() {
        this.convertFails(
            null,
            HasHostAddress.class
        );
    }

    @Test
    public void testConvertNullToHostAddress() {
        this.convertFails(
            null,
            HostAddress.class
        );
    }

    @Test
    public void testConvertTextToAbsoluteUrl() {
        final String text = "https://example.com/path1";

        this.convertAndCheck(
            text,
            Url.parseAbsolute(text)
        );
    }

    @Test
    public void testConvertTextToEmailAddress() {
        final String text = "user@example.com";

        this.convertAndCheck(
            text,
            EmailAddress.parse(text)
        );
    }

    @Test
    public void testConvertTextWithAbsoluteUrlToHasHostAddress() {
        final String text = "https://example.com";

        this.convertAndCheck(
            text,
            AbsoluteUrl.class,
            Url.parseAbsolute(text)
        );
    }

    @Test
    public void testConvertTextWithEmailAddressToHasHostAddress() {
        final String text = "user@example.com";

        this.convertAndCheck(
            text,
            HasHostAddress.class,
            EmailAddress.parse(text)
        );
    }

    @Test
    public void testConvertTextToHostAddress() {
        final String text = "example.com";

        this.convertAndCheck(
            text,
            HostAddress.with(text)
        );
    }

    @Test
    public void testConvertTextWithRelativeUrlToHasHostAddressFails() {
        this.convertFails(
            "/path1?query1=value1",
            HasHostAddress.class
        );
    }

    @Test
    public void testConvertTextToRelativeUrlFails() {
        this.convertFails(
            "/path1?query1=value1",
            RelativeUrl.class
        );
    }

    @Test
    public void testConvertTextToUrlFails() {
        this.convertFails(
            "/path1?query1=value1",
            Url.class
        );
    }

    @Override
    public NetConverterTextToHasHostAddress<FakeConverterContext> createConverter() {
        return NetConverterTextToHasHostAddress.instance();
    }

    @Override
    public FakeConverterContext createContext() {
        return new FakeConverterContext() {

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
                    NetConverters.textToEmailAddress(),
                    NetConverters.textToHostAddress(),
                    NetConverters.textToUrl()
                )
            );
        };
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            NetConverterTextToHasHostAddress.instance(),
            "text-to-has-host-address"
        );
    }

    // class............................................................................................................

    @Override
    public Class<NetConverterTextToHasHostAddress<FakeConverterContext>> type() {
        return Cast.to(NetConverterTextToHasHostAddress.class);
    }

    @Override
    public String typeNamePrefix() {
        return "NetConverter";
    }

    @Override
    public String typeNameSuffix() {
        return "";
    }
}
