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

import walkingkooka.predicate.character.CharPredicate;

/**
 * A {@link HeaderHandler} that handles string values using a {@link CharPredicate}.
 */
abstract class QuotedOrUnquotedStringHeaderHandler extends StringHeaderHandler {

    /**
     * Package private to limit sub classing.
     */
    QuotedOrUnquotedStringHeaderHandler(final CharPredicate predicate) {
        super();
        this.predicate = predicate;
    }

    final CharPredicate predicate;

    @Override
    public String toString() {
        return this.predicate.toString();
    }
}
