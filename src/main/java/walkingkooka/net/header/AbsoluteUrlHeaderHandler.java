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
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;

/**
 * A {@link HeaderHandler} that parses a header value into a {@link AbsoluteUrl}.
 * This is useful for headers such as {@link HttpHeaderName#REFERER}.
 */
final class AbsoluteUrlHeaderHandler extends NonStringHeaderHandler<AbsoluteUrl> {

    /**
     * Singleton
     */
    final static AbsoluteUrlHeaderHandler INSTANCE = new AbsoluteUrlHeaderHandler();

    /**
     * Private ctor use singleton.
     */
    private AbsoluteUrlHeaderHandler() {
        super();
    }

    @Override
    AbsoluteUrl parse0(final String text) {
        return Url.parseAbsolute(text);
    }

    @Override
    void checkNonNull(final Object value) {
        this.checkType(value,
            v -> v instanceof AbsoluteUrl,
            AbsoluteUrl.class
        );
    }

    @Override
    String toText0(final AbsoluteUrl value, final Name name) {
        return value.value();
    }

    @Override
    public String toString() {
        return toStringType(AbsoluteUrl.class);
    }
}
