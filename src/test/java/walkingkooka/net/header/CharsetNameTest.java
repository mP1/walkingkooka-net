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
import walkingkooka.naming.NameTesting2;
import walkingkooka.test.ClassTesting2;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.type.JavaVisibility;

import java.nio.charset.Charset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class CharsetNameTest implements ClassTesting2<CharsetName>,
        NameTesting2<CharsetName, CharsetName> {

    @Test
    public void testWithUtfDash8() {
        final String text = "utf-8";
        this.check(CharsetName.with(text), text, text, false);
    }

    @Test
    public void testWithUtf8() {
        final String text = "utf8";
        this.check(CharsetName.with(text), text, text, false);
    }

    /**
     * Content-Type: text/html; charset=ISO-8859-4
     */
    @Test
    public void testWithIso8859_4() {
        final String text = "ISO-8859-4";
        this.check(CharsetName.with(text), text, text, false);
    }

    @Test
    public void testWildcard() {
        this.check(CharsetName.WILDCARD_CHARSET,
                "*",
                CharsetName.NO_CHARSET,
                true);
    }

    private void check(final CharsetName charsetName,
                       final String value,
                       final String charset,
                       final boolean wildcard) {
        this.check(charsetName, value, Charset.forName(charset), wildcard);
    }

    private void check(final CharsetName charsetName,
                       final String value,
                       final Charset charset,
                       final boolean wildcard) {
        this.check(charsetName, value, Optional.of(charset), wildcard);
    }

    private void check(final CharsetName charsetName,
                       final String value,
                       final Optional<Charset> charset,
                       final boolean wildcard) {
        this.checkValue(charsetName, value);
        assertEquals(charset, charsetName.charset(), "charset");
        assertEquals(wildcard, charsetName.isWildcard(), "wildcard");
    }

    private void checkValue(final CharsetName charsetName, final String value) {
        assertTrue(value.equalsIgnoreCase(charsetName.value()),
                "value charsetName=" + charsetName + " value=" + value);
    }

    @Test
    public void testConstantUtf8() {
        this.constantAndCheck("UTF8");
    }

    @Test
    public void testConstantUtf8b() {
        this.constantAndCheck("utf8", "UTF8");
    }

    @Test
    public void testConstantUtfDash8() {
        this.constantAndCheck("UTF-8");
    }

    @Test
    public void testConstantUtfDash8Aliases() {
        final Charset charset = Charset.forName("UTF-8");
        charset.aliases().
                forEach(this::constantAndCheck);
    }

    private void constantAndCheck(final String name) {
        constantAndCheck(name, name);
    }

    private void constantAndCheck(final String name1, final String name2) {
        assertSame(CharsetName.with(name1), CharsetName.with(name2));
    }

    @Test
    public void testSetParameters() {
        final CharsetHeaderValue headerValue = CharsetName.UTF_8
                .setParameters(CharsetHeaderValue.NO_PARAMETERS);
        assertEquals(CharsetName.UTF_8, headerValue.value(), "charset");
        assertEquals(CharsetHeaderValue.NO_PARAMETERS, headerValue.parameters(), "parameters");
    }

    @Test
    public void testArraySort() {
        final CharsetName wildcard = CharsetName.WILDCARD_CHARSET;
        final CharsetName utf8 = CharsetName.UTF_8;
        final CharsetName unsupported = CharsetName.with("unsupported");

        this.compareToArraySortAndCheck(utf8, unsupported, wildcard,
                wildcard, unsupported, utf8);
    }

    @Override
    public CharsetName createName(final String name) {
        return CharsetName.with(name);
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.INSENSITIVE;
    }

    @Override
    public String nameText() {
        return "UTF-8";
    }

    @Override
    public String differentNameText() {
        return "UTF-16";
    }

    @Override
    public String nameTextLess() {
        return "UTF-16";
    }

    @Override
    public int minLength() {
        return 1;
    }

    @Override
    public int maxLength() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String possibleValidChars(final int position) {
        return 0 == position ?
                ASCII_LETTERS_DIGITS :
                ASCII_LETTERS_DIGITS + "-+.:_";
    }

    @Override
    public String possibleInvalidChars(final int position) {
        return CONTROL + BYTE_NON_ASCII;
    }

    @Override
    public Class<CharsetName> type() {
        return CharsetName.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
