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
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;

/**
 * A {@link HeaderHandler} that parses a header value into a {@link RelativeUrl}.
 * This is useful for headers such as {@link HttpHeaderName#CONTENT_LOCATION}.
 */
final class RelativeUrlHeaderHandler extends NonStringHeaderHandler<RelativeUrl> {

    /**
     * Singleton
     */
    final static RelativeUrlHeaderHandler INSTANCE = new RelativeUrlHeaderHandler();

    /**
     * Private ctor use singleton.
     */
    private RelativeUrlHeaderHandler() {
        super();
    }

    @Override
    RelativeUrl parse0(final String text) {
        return Url.parseRelative(text);
    }

    @Override
    void checkNonNull(final Object value) {
        this.checkType(value,
            v -> v instanceof RelativeUrl,
            RelativeUrl.class
        );
    }

    @Override
    String toText0(final RelativeUrl value, final Name name) {
        return value.toString();
    }

    @Override
    public String toString() {
        return toStringType(RelativeUrl.class);
    }
}
