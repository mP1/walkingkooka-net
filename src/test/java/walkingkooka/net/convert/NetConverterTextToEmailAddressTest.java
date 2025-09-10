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
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterTesting2;
import walkingkooka.convert.Converters;
import walkingkooka.convert.FakeConverterContext;
import walkingkooka.net.email.EmailAddress;

public final class NetConverterTextToEmailAddressTest implements ConverterTesting2<NetConverterTextToEmailAddress<FakeConverterContext>, FakeConverterContext> {

    @Test
    public void testConvertStringBuilderToEmailAddress() {
        this.convertAndCheck2(
            new StringBuilder("user@example.com")
        );
    }

    @Test
    public void testConvertStringToEmailAddress() {
        this.convertAndCheck2("user@example.com");
    }

    private <T extends EmailAddress> void convertAndCheck2(final CharSequence email) {
        this.convertAndCheck(
            email,
            EmailAddress.class,
            EmailAddress.parse(email.toString())
        );
    }

    @Override
    public NetConverterTextToEmailAddress<FakeConverterContext> createConverter() {
        return NetConverterTextToEmailAddress.instance();
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

            private final Converter<FakeConverterContext> converter = Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString();
        };
    }

    @Override
    public Class<NetConverterTextToEmailAddress<FakeConverterContext>> type() {
        return Cast.to(NetConverterTextToEmailAddress.class);
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
