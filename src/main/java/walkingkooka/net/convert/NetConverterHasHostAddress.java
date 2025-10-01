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

import walkingkooka.Cast;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.TryingShortCircuitingConverter;
import walkingkooka.net.HasHostAddress;

/**
 * A {@link walkingkooka.convert.Converter} that
 *
 * @param <C>
 */
final class NetConverterHasHostAddress<C extends ConverterContext> extends NetConverter<C> implements TryingShortCircuitingConverter<C> {

    /**
     * Type safe getter.
     */
    static <C extends ConverterContext> NetConverterHasHostAddress<C> instance() {
        return Cast.to(INSTANCE);
    }

    /**
     * Singleton
     */
    private final static NetConverterHasHostAddress<?> INSTANCE = new NetConverterHasHostAddress<>();

    private NetConverterHasHostAddress() {
        super();
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final C context) {
        return value instanceof HasHostAddress &&
            HasHostAddress.class == type;
    }

    @Override
    public Object tryConvertOrFail(final Object value,
                                   final Class<?> type,
                                   final C context) {
        return ((HasHostAddress) value).hostAddress();
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return "has-host-address";
    }
}
