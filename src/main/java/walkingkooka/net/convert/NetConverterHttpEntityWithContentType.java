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
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.TryingShortCircuitingConverter;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;

import java.util.Objects;

/**
 * A {@link Converter} which may be used a guard and is only successful if the {@link HttpEntity} content type is compatible.
 * It is intended to probably be the first {@link Converter} in a chain.
 */
final class NetConverterHttpEntityWithContentType<C extends ConverterContext> implements TryingShortCircuitingConverter<C> {

    static <C extends ConverterContext> NetConverterHttpEntityWithContentType<C> with(final MediaType contentType) {
        return new NetConverterHttpEntityWithContentType<>(
            Objects.requireNonNull(contentType, "contentType")
        );
    }

    private NetConverterHttpEntityWithContentType(final MediaType contentType) {
        this.contentType = contentType.setParameters(MediaType.NO_PARAMETERS);
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final C context) {
        return value instanceof HttpEntity && this.isContentType((HttpEntity) value);
    }

    private boolean isContentType(final HttpEntity httpEntity) {
        return httpEntity.contentType()
            .map(c -> this.contentType.test(c))
            .orElse(false);
    }

    private final MediaType contentType;

    @Override
    public Object tryConvertOrFail(final Object value,
                                   final Class<?> type,
                                   final C context) {
        return value;
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.contentType.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof NetConverterHttpEntityWithContentType &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final NetConverterHttpEntityWithContentType<?> other) {
        return this.contentType.equals(other.contentType);
    }

    @Override
    public String toString() {
        return this.contentType.toString();
    }
}
