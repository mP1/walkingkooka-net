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
 * A {@link HeaderValueHandler} that converts a {@link String} into one {@link IfRange}.
 */
final class IfRangeHeaderValueHandler extends NonStringHeaderValueHandler<IfRange<?>> {

    /**
     * Singleton
     */
    final static IfRangeHeaderValueHandler INSTANCE = new IfRangeHeaderValueHandler();

    /**
     * Private ctor use singleton.
     */
    private IfRangeHeaderValueHandler() {
        super();
    }

    @Override
    IfRange<?> parse0(final String text, final Name name) {
        IfRange<?> parsed;
        try {
            parsed = IfRangeETag.with(ETag.parseOne(text));
        } catch (final HeaderValueException mustBeLastModified) {
            parsed = IfRangeLastModified.with(DATE_TIME.parse(text, HttpHeaderName.IF_RANGE));
        }
        return parsed;
    }

    final static HeaderValueHandler<LocalDateTime> DATE_TIME = HeaderValueHandler.localDateTime();

    @Override
    void check0(final Object value, final Name name) {
        this.checkType(value, IfRange.class, name);
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
