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

import walkingkooka.test.ClassTesting2;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.test.TypeNameTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.type.JavaVisibility;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class HeaderValueParserTestCase<P extends HeaderValueParser, V> implements ClassTesting2<P>,
        ParseStringTesting<V>,
        ToStringTesting<P>,
        TypeNameTesting<P> {

    HeaderValueParserTestCase() {
        super();
    }

    // parse ...........................................................................................................

    final void parseStringCommentFails(final String text, final int pos) {
        this.parseStringFails(text,
                this.parseStringFailedExpected(new CommentHeaderValueException("Comment present at " + pos + " in " + CharSequences.quoteAndEscape(text))));
    }

    final void parseMissingClosingQuoteFails(final String text) {
        this.parseStringFails(text, HeaderValueParser.missingClosingQuote(text));
    }

    final void parseStringMissingValueFails(final String text) {
        this.parseStringMissingValueFails(text, text.length());
    }

    final void parseStringMissingValueFails(final String text, final int pos) {
        this.parseStringFails(text,
                HeaderValueParser.emptyToken(this.valueLabel(), pos, text));
    }

    abstract String valueLabel();

    final void parseStringMissingParameterNameFails(final String text, final int pos) {
        this.parseStringFails(text,
                HeaderValueParser.missingParameterName(pos, text));
    }

    final void parseStringMissingParameterValueFails(final String text) {
        this.parseStringMissingParameterValueFails(text, text.length());
    }

    final void parseStringMissingParameterValueFails(final String text, final int pos) {
        this.parseStringFails(text,
                HeaderValueParser.missingParameterValue(pos, text));
    }

    final void parseStringFails(final String text, final String message) {
        this.parseStringFails(text, new HeaderValueException(message));
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        return new HeaderValueException(expected.getMessage(), expected);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> expected) {
        return HeaderValueException.class;
    }

    static <TT> List<TT> listReadOnlyCheck(final List<TT> list) {
        assertThrows(UnsupportedOperationException.class, () -> {
            list.remove(0);
        });
        return list;
    }

    // TypeTesting ....................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    // TypeNameTesting .........................................................................................

    @Override
    public String typeNamePrefix() {
        return "";
    }

    @Override
    public String typeNameSuffix() {
        return HeaderValueParser.class.getSimpleName();
    }
}
