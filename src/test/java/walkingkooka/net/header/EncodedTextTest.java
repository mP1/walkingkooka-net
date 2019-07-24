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
import walkingkooka.type.JavaVisibility;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class EncodedTextTest extends HeaderValueTestCase<EncodedText> {

    @Test
    public void testWithNullCharsetFails() {
        assertThrows(NullPointerException.class, () -> {
            EncodedText.with(null, EncodedText.NO_LANGUAGE, "");
        });
    }

    @Test
    public void testWithWildcardCharsetFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            EncodedText.with(CharsetName.WILDCARD_CHARSET, EncodedText.NO_LANGUAGE, "");
        });
    }

    @Test
    public void testWithNullLanguageFails() {
        assertThrows(NullPointerException.class, () -> {
            EncodedText.with(this.charset(), null, "");
        });
    }

    @Test
    public void testWith() {
        this.check(this.createHeaderValue(), this.charset(), this.language(), this.value());
    }

    @Test
    public void testWithEmptyText() {
        final CharsetName charset = this.charset();
        final Optional<LanguageName> language = this.language();
        final String text = "";

        this.check(EncodedText.with(charset, language, text),
                charset,
                language,
                text);
    }

    @Test
    public void testWithPath() {
        final CharsetName charset = this.charset();
        final Optional<LanguageName> language = this.language();
        final String text = "/path/filename.txt";

        this.check(EncodedText.with(charset, language, text),
                charset,
                language,
                text);
    }

    @Test
    public void testWithNoLanguage() {
        final CharsetName charset = this.charset();
        final Optional<LanguageName> language = EncodedText.NO_LANGUAGE;
        final String text = this.value();

        this.check(EncodedText.with(charset, language, text),
                charset,
                language,
                text);
    }

    private void check(final EncodedText encodedText,
                       final CharsetName charset,
                       final Optional<LanguageName> language,
                       final String value) {
        assertEquals(charset, encodedText.charset(), "charset");
        assertEquals(language, encodedText.language(), "language");
        assertEquals(value, encodedText.value(), "value");
    }

    @Test
    public void testHeaderText() {
        this.toHeaderTextAndCheck("UTF-8'en'abc123");
    }

    @Test
    public void testHeaderTextWithoutLanguage() {
        this.toHeaderTextAndCheck(EncodedText.with(this.charset(),
                EncodedText.NO_LANGUAGE,
                "abc123"),
                "UTF-8''abc123");
    }

    @Test
    public void testHeaderTextEncodesPercent() {
        this.toHeaderTextAndCheck2("abc%", "UTF-8'en'abc%25");
    }

    @Test
    public void testHeaderTextEncodesWhitespace() {
        this.toHeaderTextAndCheck2("abc\t\r\n ", "UTF-8'en'abc%09%0d%0a%20");
    }

    private void toHeaderTextAndCheck2(final String value, final String headerText) {
        this.toHeaderTextAndCheck(this.createHeaderValue(value), headerText);
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
        this.checkNotEquals(EncodedText.with(this.charset(),
                Optional.of(LanguageName.with("fr")),
                this.value()));
    }

    @Test
    public void testEqualsDifferentLanguage2() {
        this.checkNotEquals(EncodedText.with(this.charset(),
                EncodedText.NO_LANGUAGE,
                this.value()));
    }

    @Test
    public void testEqualsDifferentValue() {
        this.checkNotEquals(EncodedText.with(this.charset(),
                this.language(),
                "different"));
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHeaderValue(), "UTF-8 en \"abc123\"");
    }

    // helpers..........................................................................................................

    @Override
    public EncodedText createHeaderValue() {
        return this.createHeaderValue(this.value());
    }

    private EncodedText createHeaderValue(final String value) {
        return EncodedText.with(this.charset(), this.language(), value);
    }

    private CharsetName charset() {
        return CharsetName.UTF_8;
    }

    private Optional<LanguageName> language() {
        return Optional.of(LanguageName.with("en"));
    }

    private String value() {
        return "abc123";
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

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
