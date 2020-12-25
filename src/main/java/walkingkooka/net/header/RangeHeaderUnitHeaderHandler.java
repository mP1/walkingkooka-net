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

/**
 * A {@link HeaderHandler} that parses a header value into a {@link RangeHeaderUnit >}.
 * This is useful for headers such as {@link HttpHeaderName#ACCEPT_RANGES}.
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept-Ranges"></a>
 * <pre>
 * Accept-Ranges: bytes
 * Accept-Ranges: none
 * </pre>
 */
final class RangeHeaderUnitHeaderHandler extends NonStringHeaderHandler<RangeHeaderUnit> {

    /**
     * Singleton
     */
    final static RangeHeaderUnitHeaderHandler INSTANCE = new RangeHeaderUnitHeaderHandler();

    /**
     * Private ctor use singleton.
     */
    private RangeHeaderUnitHeaderHandler() {
        super();
    }

    @Override
    RangeHeaderUnit parse0(final String text, final Name name) {
        return RangeHeaderUnit.parse(text);
    }

    @Override
    void check0(final Object value, final Name name) {
        this.checkType(value,
                v -> v instanceof RangeHeaderUnit,
                RangeHeaderUnit.class,
                name);
    }

    @Override
    String toText0(final RangeHeaderUnit value, final Name name) {
        return value.toHeaderText();
    }

    @Override
    public String toString() {
        return this.toStringType(RangeHeaderUnit.class);
    }
}
