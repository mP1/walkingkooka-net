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

import walkingkooka.collect.list.Lists;
import walkingkooka.net.HasQualityFactor;
import walkingkooka.predicate.character.CharPredicate;

import java.util.List;

/**
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept-Charset"></a>
 * <pre>
 * Accept-Charset: <charset>
 *
 * // Multiple types, weighted with the quality value syntax:
 * Accept-Charset: utf-8, iso-8859-1;q=0.5
 * </pre>
 */
final class AcceptCharsetHeaderParser extends HeaderParserWithParameters<AcceptCharsetValue, AcceptCharsetValueParameterName<?>> {

    static AcceptCharset parseAcceptCharset(final String text) {
        final AcceptCharsetHeaderParser parser = new AcceptCharsetHeaderParser(text);
        parser.parse();
        parser.charsets.sort(HasQualityFactor.qualityFactorDescendingComparator());
        return AcceptCharset.with(parser.charsets);
    }

    private AcceptCharsetHeaderParser(final String text) {
        super(text);
    }

    @Override
    AcceptCharsetValue wildcardValue() {
        this.position++;
        return AcceptCharsetValue.WILDCARD_VALUE;
    }

    @Override
    AcceptCharsetValue value() {
        return AcceptCharsetValue.with(this.charsetName());
    }

    private CharsetName charsetName() {
        if (!this.hasMoreCharacters()) {
            this.missingValue();
        }

        final char initial = this.character();
        if (!CharsetName.INITIAL_CHAR_PREDICATE.test(initial)) {
            this.failInvalidCharacter();
        }
        this.position++;

        return CharsetName.with(initial + this.token(CharsetName.PART_CHAR_PREDICATE));
    }

    // @VisibleForTesting
    final static String CHARSET = "charset";

    @Override
    boolean allowMultipleValues() {
        return true;
    }

    @Override
    AcceptCharsetValueParameterName<?> parameterName() {
        return this.parameterName(PARAMETER_NAME, AcceptCharsetValueParameterName::with);
    }

    private final static CharPredicate PARAMETER_NAME = RFC2045TOKEN;

    @Override
    String quotedParameterValue(final AcceptCharsetValueParameterName<?> parameterName) {
        return this.quotedText(QUOTED_PARAMETER_VALUE, ESCAPING_SUPPORTED);
    }

    final static CharPredicate QUOTED_PARAMETER_VALUE = ASCII;

    @Override
    String unquotedParameterValue(final AcceptCharsetValueParameterName<?> parameterName) {
        return this.token(UNQUOTED_PARAMETER_VALUE);
    }

    final static CharPredicate UNQUOTED_PARAMETER_VALUE = RFC2045TOKEN;

    @Override
    void valueComplete(final AcceptCharsetValue value) {
        this.charsets.add(value);
    }

    @Override
    void missingValue() {
        this.failMissingValue(CHARSET);
    }

    /**
     * Aggregates all charsets.
     */
    private final List<AcceptCharsetValue> charsets = Lists.array();
}
