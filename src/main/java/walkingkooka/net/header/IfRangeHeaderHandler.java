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
    IfRange<?> parse0(final String text, final Name name) {
        IfRange<?> parsed;
        try {
            parsed = IfRangeETag.with(ETag.parseOne(text));
        } catch (final HeaderException mustBeLastModified) {
            parsed = IfRangeLastModified.with(DATE_TIME.parse(text, HttpHeaderName.IF_RANGE));
        }
        return parsed;
    }

    final static HeaderHandler<LocalDateTime> DATE_TIME = HeaderHandler.localDateTime();

    @Override
    void check0(final Object value, final Name name) {
        this.checkType(value,
                v -> v instanceof IfRange,
                IfRange.class,
                name);
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
