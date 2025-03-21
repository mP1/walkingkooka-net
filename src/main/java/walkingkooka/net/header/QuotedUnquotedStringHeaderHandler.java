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
import walkingkooka.predicate.character.CharPredicate;

/**
 * A {@link HeaderHandler} that if necessary adds quotes and escape/encodes such characters.
 * It assumes any {@link String} to be parsed have had any double quotes removed, and adds them
 * when necessary.
 * <br>
 * <a href="https://mimesniff.spec.whatwg.org/#parsing-a-mime-type">mime type</a>
 * <a href="https://fetch.spec.whatwg.org/#collect-an-http-quoted-string">Quoted string</a>
 */
final class QuotedUnquotedStringHeaderHandler extends StringHeaderHandler {

    static QuotedUnquotedStringHeaderHandler with(final CharPredicate quotedPredicate,
                                                  final boolean supportBackslashEscaping,
                                                  final CharPredicate unquotedPredicate) {
        return new QuotedUnquotedStringHeaderHandler(
            HeaderHandler.quoted(quotedPredicate, supportBackslashEscaping),
            HeaderHandler.unquoted(unquotedPredicate));
    }

    /**
     * Private ctor use singleton.
     */
    private QuotedUnquotedStringHeaderHandler(final HeaderHandler<String> quoted,
                                              final HeaderHandler<String> unquoted) {
        super();
        this.quoted = quoted;
        this.unquoted = unquoted;
    }

    /**
     * If the text begins witn a double quote and then selects either the quoted or unquoted handler to parseValue.
     */
    @Override
    String parse0(final String text) {
        final HeaderHandler<String> handler = text.isEmpty() || text.charAt(0) != '"' ?
            this.unquoted :
            this.quoted;
        return handler.parse(text);
    }

    @Override
    String toText0(final String value, final Name name) {
        String text;
        try {
            text = this.unquoted.toText(value, name);
        } catch (final HeaderException tryQuoted) {
            text = this.quoted.toText(value, name);
        }
        return text;
    }

    private final HeaderHandler<String> quoted;

    private final HeaderHandler<String> unquoted;

    @Override
    public String toString() {
        return "String";
    }
}
