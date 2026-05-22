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

package walkingkooka.net.header;

import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberSymbols;

import java.util.Currency;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Provides a single method to provide the {@link MediaType} for this value.
 */
public interface HasContentType {

    /**
     * Tries to get the {@link MediaType content type} for the given object. Note some special cases are tested within
     * such as
     * <ul>
     * <li>{@link Currency}</li>
     * <li>{@link DateTimeSymbols}</li>
     * <li>{@link DecimalNumberSymbols}</li>
     * <li>{@link Locale}</li>
     * </ul>
     */
    static Optional<MediaType> tryContentType(final Object object) {
        Objects.requireNonNull(object, "object");

        Optional<MediaType> contentType;

        if (object instanceof HasContentType) {
            contentType = ((HasContentType) object)
                .contentType();
        } else {
            final MediaType contentTypeOrNull;

            if (object instanceof Currency) {
                contentTypeOrNull = json(Currency.class);
            } else {
                if (object instanceof DateTimeSymbols) {
                    contentTypeOrNull = json(DateTimeSymbols.class);
                } else {
                    if (object instanceof DecimalNumberSymbols) {
                        contentTypeOrNull = json(DecimalNumberSymbols.class);
                    } else {
                        if (object instanceof Locale) {
                            contentTypeOrNull = json(Locale.class);
                        } else {
                            contentTypeOrNull = null;
                        }
                    }
                }
            }

            contentType = Optional.ofNullable(contentTypeOrNull);
        }

        return contentType;
    }

    /**
     * Returns the {@link MediaType} for the given value type.
     */
    static MediaType json(final Class<?> type) {
        Objects.requireNonNull(type, "type");

        return MediaType.APPLICATION_JSON.setSuffix(
            Optional.of(type.getName())
        );
    }

    /**
     * Returns the content type
     */
    Optional<MediaType> contentType();
}
