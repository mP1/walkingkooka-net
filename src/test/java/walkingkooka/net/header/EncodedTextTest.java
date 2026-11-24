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

import org.junit.jupiter.api.Test;
import walkingkooka.HasCharsetTesting;
import walkingkooka.reflect.PublicClassTesting;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class EncodedTextTest extends HeaderTestCase<EncodedText>
    implements PublicClassTesting<EncodedText>,
    HasCharsetTesting {

    private final static CharsetName CHARSET_NAME = CharsetName.UTF_8;

    @Test
    public void testWithNullCharsetFails() {
        assertThrows(
            NullPointerException.class,
            () -> EncodedText.with(
                null,
                EncodedText.NO_LANGUAGE,
                ""
            )
        );
    }

    @Test
    public void testWithWildcardCharsetFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> EncodedText.with(
                CharsetName.WILDCARD_CHARSET,
                EncodedText.NO_LANGUAGE,
                ""
            )
        );
    }

    @Test
    public void testWithNullLanguageFails() {
        assertThrows(
            NullPointerException.class,
            () -> EncodedText.with(
                CHARSET_NAME,
                null,
                ""
            )
        );
    }

    @Test
    public void testWith() {
        this.check(
            this.createHeader(),
            CHARSET_NAME,
            this.language(),
            this.value()
        );
    }

    @Test
    public void testWithEmptyText() {
        final Optional<LanguageName> language = this.language();
        final String text = "";

        this.check(
            EncodedText.with(
                CHARSET_NAME,
                language,
                text
            ),
            CHARSET_NAME,
            language,
            text
        );
    }

    @Test
    public void testWithPath() {
        final CharsetName charset = CHARSET_NAME;
        final Optional<LanguageName> language = this.language();
        final String text = "/path/filename.txt";

        this.check(
            EncodedText.with(
                charset,
                language,
                text
            ),
            charset,
            language,
            text
        );
    }

    @Test
    public void testWithNoLanguage() {
        final Optional<LanguageName> language = EncodedText.NO_LANGUAGE;
        final String text = this.value();

        this.check(
            EncodedText.with(
                CHARSET_NAME,
                language,
                text
            ),
            CHARSET_NAME,
            language,
            text
        );
    }

    private void check(final EncodedText encodedText,
                       final CharsetName charset,
                       final Optional<LanguageName> language,
                       final String value) {
        this.checkEquals(
            charset,
            encodedText.charset(),
            "charset"
        );
        this.checkEquals(
            language,
            encodedText.language(),
            "language"
        );
        this.valueAndCheck(
            encodedText,
            value
        );
    }

    // HasText..........................................................................................................

    @Test
    public void testText() {
        this.textAndCheck("UTF-8'en'abc123");
    }

    @Test
    public void testTextWithoutLanguage() {
        this.textAndCheck(
            EncodedText.with(
                CHARSET_NAME,
                EncodedText.NO_LANGUAGE,
                "abc123"
            ),
            "UTF-8''abc123"
        );
    }

    @Test
    public void testTextEncodesPercent() {
        this.textAndCheck2(
            "abc%",
            "UTF-8'en'abc%25"
        );
    }

    @Test
    public void testTextEncodesWhitespace() {
        this.textAndCheck2(
            "abc\t\r\n ",
            "UTF-8'en'abc%09%0d%0a%20"
        );
    }

    private void textAndCheck2(final String value,
                               final String expected) {
        this.textAndCheck(
            this.createHeader(value),
            expected
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentCharset() {
        this.checkNotEquals(EncodedText.with(CharsetName.UTF_16,
            this.language(),
            this.value()));
    }

    @Test
    public void testEqualsDifferentLanguage() {
        this.checkNotEquals(
            EncodedText.with(CHARSET_NAME,
                Optional.of(
                    LanguageName.with("fr")
                ),
                this.value()
            )
        );
    }

    @Test
    public void testEqualsDifferentLanguage2() {
        this.checkNotEquals(
            EncodedText.with(
                CHARSET_NAME,
                EncodedText.NO_LANGUAGE,
                this.value()
            )
        );
    }

    @Test
    public void testEqualsDifferentValue() {
        this.checkNotEquals(
            EncodedText.with(
                CHARSET_NAME,
                this.language(),
                "different"
            )
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createHeader(),
            "UTF-8 en \"abc123\""
        );
    }

    // helpers..........................................................................................................

    @Override
    public EncodedText createHeader() {
        return this.createHeader(this.value());
    }

    private EncodedText createHeader(final String value) {
        return EncodedText.with(
            CHARSET_NAME,
            this.language(),
            value
        );
    }

    private Optional<LanguageName> language() {
        return Optional.of(
            LanguageName.with("en")
        );
    }

    private String value() {
        return "abc123";
    }

    @Override
    public EncodedText createDifferentHeader() {
        return EncodedText.with(
            CHARSET_NAME,
            Optional.empty(),
            "different"
        );
    }

    @Override
    public boolean isMultipart() {
        return true;
    }

    @Override
    public boolean isRequest() {
        return true;
    }

    @Override
    public boolean isResponse() {
        return true;
    }

    @Override
    public Class<EncodedText> type() {
        return EncodedText.class;
    }
}
