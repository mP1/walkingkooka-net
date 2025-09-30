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
import walkingkooka.Either;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.convert.TextToTryingShortCircuitingConverter;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.HasHostAddress;
import walkingkooka.net.HostAddress;
import walkingkooka.net.email.EmailAddress;

/**
 * A {@link Converter} that supports converting a {@link String} to a {@link AbsoluteUrl}, {@link EmailAddress} or {@link HostAddress}
 */
final class NetConverterTextToHasHostAddress<C extends ConverterContext> implements TextToTryingShortCircuitingConverter<C> {

    /**
     * Type safe getter.
     */
    static <C extends ConverterContext> NetConverterTextToHasHostAddress<C> instance() {
        return Cast.to(INSTANCE);
    }

    /**
     * Singleton
     */
    private final static NetConverterTextToHasHostAddress INSTANCE = new NetConverterTextToHasHostAddress<>();

    private NetConverterTextToHasHostAddress() {
        super();
    }

    @Override
    public boolean isTargetType(final Object value,
                                final Class<?> type,
                                final C context) {
        return AbsoluteUrl.class == type ||
            EmailAddress.class == type ||
            HasHostAddress.class == type ||
            HostAddress.class == type;
    }

    @Override
    public Object parseText(final String text,
                            final Class<?> type,
                            final C context) {
        final Object result;

        if (AbsoluteUrl.class == type) {
            result = context.convertOrFail(
                text,
                AbsoluteUrl.class
            );
        } else {
            if (EmailAddress.class == type) {
                result = context.convertOrFail(
                    text,
                    EmailAddress.class
                );
            } else {
                if (HostAddress.class == type) {
                    result = context.convertOrFail(
                        text,
                        HostAddress.class
                    );
                } else {
                    if (HasHostAddress.class == type) {
                        final Either<AbsoluteUrl, String> absoluteUrl = context.convert(
                            text,
                            AbsoluteUrl.class
                        );
                        if (absoluteUrl.isLeft()) {
                            result = absoluteUrl.leftValue();
                        } else {
                            final Either<EmailAddress, String> emailAddress = context.convert(
                                text,
                                EmailAddress.class
                            );
                            if (emailAddress.isLeft()) {
                                result = emailAddress.leftValue();
                            } else {
                                throw new IllegalArgumentException("Invalid text");
                            }
                        }
                    } else {
                        throw new IllegalArgumentException("Invalid type: " + type);
                    }
                }
            }
        }

        return result;
    }

    @Override
    public String toString() {
        return "text-to-has-host-address";
    }
}
