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

import walkingkooka.naming.Name;
import walkingkooka.text.CharSequences;

import java.time.LocalDateTime;

/**
 * A {@link HeaderHandler} that converts a {@link String} into one {@link IfRange}.
 */
final class IfRangeHeaderHandler extends NonStringHeaderHandler<IfRange<?>> {

    /**
     * Singleton
     */
    final static IfRangeHeaderHandler INSTANCE = new IfRangeHeaderHandler();

    /**
     * Private ctor use singleton.
     */
    private IfRangeHeaderHandler() {
        super();
    }

    @Override
    IfRange<?> parse0(final String text) {
        IfRange<?> parsed;
        try {
            parsed = IfRangeETag.with(ETag.parseOne(text));
        } catch (final HeaderException mustBeLastModified) {
            final LocalDateTime date;

            try {
                date = DATE_TIME.parse(
                    text
                );
            } catch (final HeaderException cause) {
                throw new IllegalArgumentException("Invalid date in " + CharSequences.quoteAndEscape(text));
            }
            parsed = IfRangeLastModified.with(
                date
            );
        }
        return parsed;
    }

    final static HeaderHandler<LocalDateTime> DATE_TIME = HeaderHandler.localDateTime();

    @Override
    void checkNonNull(final Object value) {
        this.checkType(value,
            v -> v instanceof IfRange,
            IfRange.class
        );
    }

    @Override
    String toText0(final IfRange<?> value, final Name name) {
        return value.toHeaderText();
    }

    @Override
    public String toString() {
        return this.toStringType(IfRange.class);
    }
}
