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
import walkingkooka.convert.Converters;
import walkingkooka.convert.FakeConverterContext;
import walkingkooka.net.Url;
import walkingkooka.net.UrlFragment;

public final class NetConverterTextToUrlFragmentTest extends NetConverterTestCase<NetConverterTextToUrlFragment<FakeConverterContext>, FakeConverterContext> {

    @Test
    public void testConvertStringToUrlFails() {
        this.convertFails(
            "https://example.com#fragment123",
            Url.class
        );
    }

    @Test
    public void testConvertStringToUrlFragment() {
        final UrlFragment fragment = UrlFragment.parse("/fragment1/fragment2");

        this.convertAndCheck(
            fragment.text(),
            fragment
        );
    }

    @Override
    public NetConverterTextToUrlFragment<FakeConverterContext> createConverter() {
        return NetConverterTextToUrlFragment.instance();
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
                return converter.convert(
                    value,
                    target,
                    this
                );
            }

            private final Converter<FakeConverterContext> converter = Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString();
        };
    }

    @Override
    public Class<NetConverterTextToUrlFragment<FakeConverterContext>> type() {
        return Cast.to(NetConverterTextToUrlFragment.class);
    }
}
