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
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.header.CharsetName;
import walkingkooka.net.header.MediaType;

public final class NetConverterTextToMediaTypeTest extends NetConverterTextToTestCase<NetConverterTextToMediaType<FakeConverterContext>, FakeConverterContext> {

    @Test
    public void testConvertStringToAbsoluteUrl() {
        this.convertFails(
            "https://example.com/path1",
            AbsoluteUrl.class
        );
    }

    @Test
    public void testConvertStringToEmailAddress() {
        this.convertFails(
            "user@example.com",
            EmailAddress.class
        );
    }

    @Test
    public void testConvertStringPlainTextToMediaType() {
        final MediaType mediaType = MediaType.TEXT_PLAIN;

        this.convertAndCheck(
            mediaType.toString(),
            mediaType
        );
    }

    @Test
    public void testConvertStringPlainTextAndParametersToMediaType() {
        final MediaType mediaType = MediaType.TEXT_PLAIN.setCharset(CharsetName.UTF_8);

        this.convertAndCheck(
            mediaType.toString(),
            mediaType
        );
    }

    @Test
    public void testConvertStringBuilderMultipartFormDataToMediaType() {
        final MediaType mediaType = MediaType.MULTIPART_FORM_DATA;

        this.convertAndCheck(
            mediaType.toString(),
            mediaType
        );
    }

    @Override
    public NetConverterTextToMediaType<FakeConverterContext> createConverter() {
        return NetConverterTextToMediaType.instance();
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
    public Class<NetConverterTextToMediaType<FakeConverterContext>> type() {
        return Cast.to(NetConverterTextToMediaType.class);
    }
}
