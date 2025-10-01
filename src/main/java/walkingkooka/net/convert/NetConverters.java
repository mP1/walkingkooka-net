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

import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.Converters;
import walkingkooka.net.header.MediaType;
import walkingkooka.reflect.PublicStaticHelper;

/**
 * A collection of {@link Converter}
 */
public final class NetConverters implements PublicStaticHelper {

    /**
     * {@see NetConverterHasHostAddress}
     */
    public static <C extends ConverterContext> Converter<C> hasHostAddress() {
        return NetConverterHasHostAddress.instance();
    }

    /**
     * {@see NetConverterHttpEntityWithContentType}
     */
    public static <C extends ConverterContext> Converter<C> httpEntityContentType(final MediaType contentType) {
        return NetConverterHttpEntityWithContentType.with(contentType);
    }

    /**
     * A collection of all net converters.
     */
    public static <C extends ConverterContext> Converter<C> net() {
        return Converters.<C>collection(
            Lists.of(
                Converters.characterOrCharSequenceOrHasTextOrStringToCharacterOrCharSequenceOrString(),
                textToHasHostAddress(),
                hasHostAddress(),
                textToEmailAddress(),
                textToHostAddress(),
                textToUrl(),
                textToUrlFragment(),
                textToUrlQueryString()
            )
        ).setToString("net");
    }

    /**
     * {@see NetConverterTextToHasHostAddress}
     */
    public static <C extends ConverterContext> Converter<C> textToHasHostAddress() {
        return NetConverterTextToHasHostAddress.instance();
    }

    /**
     * {@see NetConverterTextToEmailAddress}
     */
    public static <C extends ConverterContext> Converter<C> textToEmailAddress() {
        return NetConverterTextToEmailAddress.instance();
    }

    /**
     * {@see NetConverterTextToHostAddress}
     */
    public static <C extends ConverterContext> Converter<C> textToHostAddress() {
        return NetConverterTextToHostAddress.instance();
    }

    /**
     * {@see NetConverterTextToMailToUrl}
     */
    public static <C extends ConverterContext> Converter<C> textToMailToUrl() {
        return NetConverterTextToMailToUrl.instance();
    }

    /**
     * {@see NetConverterTextToUrl}
     */
    public static <C extends ConverterContext> Converter<C> textToUrl() {
        return NetConverterTextToUrl.instance();
    }

    /**
     * {@see NetConverterTextToUrlFragment}
     */
    public static <C extends ConverterContext> Converter<C> textToUrlFragment() {
        return NetConverterTextToUrlFragment.instance();
    }

    /**
     * {@see NetConverterTextToUrlQueryString}
     */
    public static <C extends ConverterContext> Converter<C> textToUrlQueryString() {
        return NetConverterTextToUrlQueryString.instance();
    }

    /**
     * Stop creation
     */
    private NetConverters() {
        throw new UnsupportedOperationException();
    }
}
