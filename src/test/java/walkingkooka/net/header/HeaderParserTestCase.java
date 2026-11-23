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

import walkingkooka.InvalidCharacterException;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.PackagePrivateClassTesting;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.CharSequences;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class HeaderParserTestCase<P extends HeaderParser, V> implements PackagePrivateClassTesting<P>,
    ParseStringTesting<V>,
    ToStringTesting,
    TypeNameTesting<P> {

    HeaderParserTestCase() {
        super();
    }

    // parse ...........................................................................................................

    /**
     * Asserts that a {@link InvalidCharacterException} is thrown and the position matches the expected position.
     * The actual message which may include a label is ignored.
     */
    @Override
    public final void parseStringInvalidCharacterFails(final String text,
                                                       final int pos) {
        final HeaderException thrown = assertThrows(
            HeaderException.class,
            () -> this.parseString(text)
        );

        final InvalidCharacterException invalidCharacterException = (InvalidCharacterException) thrown.getCause();

        this.checkEquals(
            pos,
            invalidCharacterException.position()
        );
    }

    final void parseStringCommentFails(final String text,
                                       final int pos) {
        this.parseStringFails(text,
            this.parseStringFailedExpected(
                new CommentHeaderException("Comment present at " + pos + " in " + CharSequences.quoteAndEscape(text))
            )
        );
    }

    final void parseMissingClosingQuoteFails(final String text) {
        this.parseStringFails(
            text,
            HeaderParser.missingClosingQuote(text)
        );
    }

    final void parseStringMissingValueFails(final String text) {
        this.parseStringMissingValueFails(
            text,
            text.length()
        );
    }

    final void parseStringMissingValueFails(final String text,
                                            final int pos) {
        this.parseStringFails(
            text,
            HeaderParser.emptyToken(
                this.valueLabel(),
                pos,
                text
            )
        );
    }

    abstract String valueLabel();

    final void parseStringMissingParameterValueFails(final String text) {
        this.parseStringMissingParameterValueFails(
            text,
            text.length()
        );
    }

    final void parseStringMissingParameterValueFails(final String text,
                                                     final int pos) {
        this.parseStringFails(
            text,
            HeaderParser.missingParameterValue(
                pos,
                text
            )
        );
    }

    final void parseStringFails(final String text,
                                final String message) {
        this.parseStringFails(
            text,
            new HeaderException(message)
        );
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        return new HeaderException(
            expected.getMessage(),
            expected
        );
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> expected) {
        return HeaderException.class;
    }

    static <TT> List<TT> listReadOnlyCheck(final List<TT> list) {
        assertThrows(
            UnsupportedOperationException.class,
            () -> list.remove(0)
        );
        return list;
    }

    // class............................................................................................................

    @Override
    public final String typeNamePrefix() {
        return this instanceof HeaderParserWithParametersTest ?
            HeaderParser.class.getSimpleName() :
            "";
    }

    @Override
    public final String typeNameSuffix() {
        return this instanceof HeaderParserWithParametersTest ?
            "" :
            HeaderParser.class.getSimpleName();
    }
}
