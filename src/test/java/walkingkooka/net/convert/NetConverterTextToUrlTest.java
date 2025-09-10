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

/*
 * Copyright 2024 Miroslav Pokorny (github.com/mP1)
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
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;

import java.util.function.Function;

public final class NetConverterTextToUrlTest implements ConverterTesting2<NetConverterTextToUrl<FakeConverterContext>, FakeConverterContext> {

    @Test
    public void testConvertStringToUrl() {
        final String url = "https://example.com";

        this.convertStringAndCheck2(
            url,
            Url.class,
            Url.parse(url)
        );
    }

    @Test
    public void testConvertStringBuilderToAbsoluteUrl() {
        final String text = "https://example.com";

        this.convertAndCheck(
            new StringBuilder(text),
            AbsoluteUrl.class,
            Url.parseAbsolute(text)
        );
    }

    @Test
    public void testConvertStringToAbsoluteUrl() {
        this.convertStringAndCheck(
            "https://example.com",
            AbsoluteUrl.class,
            Url::parseAbsolute
        );
    }

    @Test
    public void testConvertStringToRelativeUrl() {
        this.convertStringAndCheck(
            "/relative-url/path2/path3?query",
            RelativeUrl.class,
            Url::parseRelative
        );
    }

    private <T extends Url> void convertStringAndCheck(final String url,
                                                       final Class<T> type,
                                                       final Function<String, T> expected) {
        this.convertStringAndCheck2(
            url,
            type,
            expected.apply(url)
        );
    }

    private <T extends Url> void convertStringAndCheck2(final String url,
                                                        final Class<T> type,
                                                        final T expected) {
        this.convertAndCheck(
            url,
            type,
            expected
        );
    }

    @Override
    public NetConverterTextToUrl<FakeConverterContext> createConverter() {
        return NetConverterTextToUrl.instance();
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
    public Class<NetConverterTextToUrl<FakeConverterContext>> type() {
        return Cast.to(NetConverterTextToUrl.class);
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
