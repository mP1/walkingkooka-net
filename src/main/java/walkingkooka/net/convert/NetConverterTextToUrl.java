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

import walkingkooka.Cast;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.TextToTryingShortCircuitingConverter;
import walkingkooka.net.Url;

/**
 * A {@link Converter} that supports converting a {@link String} to one of the {@link Url} sub-classes.
 */
final class NetConverterTextToUrl<C extends ConverterContext> implements TextToTryingShortCircuitingConverter<C> {

    /**
     * Type safe getter.
     */
    static <C extends ConverterContext> NetConverterTextToUrl<C> instance() {
        return Cast.to(INSTANCE);
    }

    /**
     * Singleton
     */
    private final static NetConverterTextToUrl INSTANCE = new NetConverterTextToUrl();

    private NetConverterTextToUrl() {
        super();
    }

    @Override
    public boolean isTargetType(final Object value,
                                final Class<?> type,
                                final C context) {
        return Url.isClass(type);
    }

    @Override
    public Object parseText(final String text,
                            final Class<?> type,
                            final C context) {
        return Url.parseAsUrl(
            text,
            Cast.to(type)
        );
    }

    @Override
    public String toString() {
        return "text-to-url";
    }
}
