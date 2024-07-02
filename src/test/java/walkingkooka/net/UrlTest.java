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

package walkingkooka.net;

import org.junit.jupiter.api.Test;
import walkingkooka.Binary;
import walkingkooka.net.header.MediaType;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.CharSequences;

import java.nio.charset.Charset;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class UrlTest implements ClassTesting2<Url>,
        ParseStringTesting<Url> {

    // EMPTY RELATIVE URL...............................................................................................

    @Test
    public void testEmptyRelativeUrl() {
        final RelativeUrl relativeUrl = Url.EMPTY_RELATIVE_URL;
        this.checkEquals("", relativeUrl.toString());
    }

    // isClass..........................................................................................................

    @Test
    public void testIsClassWithNull() {
        this.isClassAndCheck(
                null,
                false
        );
    }

    @Test
    public void testIsClassWithNonUrlClass() {
        this.isClassAndCheck(
                String.class,
                false
        );
    }

    @Test
    public void testIsClassWithUrl() {
        this.isClassAndCheck(
                Url.class,
                true
        );
    }

    @Test
    public void testIsClassWithAbsoluteUrl() {
        this.isClassAndCheck(
                AbsoluteUrl.class,
                true
        );
    }

    @Test
    public void testIsClassWithDataUrl() {
        this.isClassAndCheck(
                DataUrl.class,
                true
        );
    }

    @Test
    public void testIsClassWithMailToUrl() {
        this.isClassAndCheck(
                MailToUrl.class,
                true
        );
    }

    @Test
    public void testIsClassWithRelativeUrl() {
        this.isClassAndCheck(
                RelativeUrl.class,
                true
        );
    }

    private void isClassAndCheck(final Class<?> type,
                                 final boolean expected) {
        this.checkEquals(
                expected,
                Url.isUrl(type),
                () -> null != type ? type.getName() : null
        );
    }

    // parseUrl.........................................................................................................

    @Test
    public void testParseAsUrlWithNullUrlFails() {
        assertThrows(
                NullPointerException.class,
                () -> Url.parseAsUrl(
                        null,
                        Url.class
                )
        );
    }

    @Test
    public void testParseAsUrlWithNullTypeFails() {
        assertThrows(
                NullPointerException.class,
                () -> Url.parseAsUrl(
                        "",
                        null
                )
        );
    }

    @Test
    public void testParseAsUrlWitWrongUrlFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Url.parseAsUrl(
                        "https://example.com",
                        RelativeUrl.class
                )
        );
    }

    @Test
    public void testParseAsUrlWithAbsoluteUrlAndUrlType() {
        final String url = "https://example.com";

        this.parseAsUrlAndCheck2(
                url,
                Url.class,
                Url.parseAbsolute(url)
        );
    }

    @Test
    public void testParseAsUrlWithRelativeUrlAndUrlType() {
        final String url = "/relative-url/path";

        this.parseAsUrlAndCheck2(
                url,
                Url.class,
                Url.parseRelative(url)
        );
    }

    @Test
    public void testParseAsUrlWithAbsoluteUrl() {
        this.parseAsUrlAndCheck(
                "https://example.com",
                AbsoluteUrl.class,
                Url::parseAbsolute
        );
    }

    @Test
    public void testParseAsUrlWithDataUrl() {
        this.parseAsUrlAndCheck(
                "data:,Hello%2C%20World%21",
                DataUrl.class,
                Url::parseData
        );
    }

    @Test
    public void testParseAsUrlWithMailToUrl() {
        this.parseAsUrlAndCheck(
                "mailto:me@example.com",
                MailToUrl.class,
                Url::parseMailTo
        );
    }

    @Test
    public void testParseAsUrlWithRelativeUrl() {
        this.parseAsUrlAndCheck(
                "/path2",
                RelativeUrl.class,
                Url::parseRelative
        );
    }

    private <T extends Url> void parseAsUrlAndCheck(final String url,
                                                    final Class<T> type,
                                                    final Function<String, T> expected) {
        this.parseAsUrlAndCheck2(
                url,
                type,
                expected.apply(url)
        );
    }

    private <T extends Url> void parseAsUrlAndCheck2(final String url,
                                                     final Class<T> type,
                                                     final T expected) {
        this.checkEquals(
                expected,
                Url.parseAsUrl(
                        url,
                        type
                )
        );
    }

    // parseXXX.........................................................................................................

    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testParseAbsoluteUrl() {
        final String text = "https://example.com";

        this.parseStringAndCheck(text, Url.parseAbsolute(text));
    }

    @Test
    public void testParseDataUrl() {
        final DataUrl url = Url.data(
                Optional.of(MediaType.TEXT_PLAIN),
                true, // base64
                Binary.with(
                        "abc123".getBytes(Charset.defaultCharset())
                )
        );

        this.parseStringAndCheck(
                url.value(),
                Url.parseData(url.value())
        );
    }

    @Test
    public void testParseMailToUrl() {
        final MailToUrl url = Url.parseMailTo("mailto:hello@example.com?subject=Subject123&body=body123");

        this.parseStringAndCheck(
                url.value(),
                url
        );
    }

    @Test
    public void testParseRelativeUrl() {
        final String text = "/path123?query456";

        this.parseStringAndCheck(text, Url.parseRelative(text));
    }

    @Test
    public void testParseRelativeUrlEmpty() {
        final String text = "";

        this.parseStringAndCheck(text, Url.parseRelative(text));
    }

    // parseAbsoluteOrRelative..........................................................................................

    @Test
    public void testParseAbsoluteOrRelativeUrlWithDataFails() {
        final DataUrl url = Url.data(
                Optional.of(
                        MediaType.TEXT_PLAIN
                ),
                true, // base64
                Binary.with(
                        "abc123".getBytes(Charset.defaultCharset())
                )
        );

        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> Url.parseAbsoluteOrRelative(url.value())
        );

        this.checkEquals(
                "Unknown protocol \"data:text/plain;base64,YWJjMTIz\"",
                thrown.getMessage()
        );
    }

    @Test
    public void testParseAbsoluteOrRelativeUrlWithAbsolute() {
        final String text = "https://example.com";

        this.parseAbsoluteOrRelativeUrlAndCheck(
                text,
                Url.parseAbsolute(text)
        );
    }

    @Test
    public void testParseAbsoluteOrRelativeUrlWithAbsoluteFragment() {
        final String text = "https://example.com#fragment123";

        this.parseAbsoluteOrRelativeUrlAndCheck(
                text,
                Url.parseAbsolute(text)
        );
    }

    @Test
    public void testParseAbsoluteOrRelativeUrlWithAbsolutePath() {
        final String text = "https://example.com/path1";

        this.parseAbsoluteOrRelativeUrlAndCheck(
                text,
                Url.parseAbsolute(text)
        );
    }

    @Test
    public void testParseAbsoluteOrRelativeUrlWithAbsoluteQueryString() {
        final String text = "https://example.com?query123";

        this.parseAbsoluteOrRelativeUrlAndCheck(
                text,
                Url.parseAbsolute(text)
        );
    }

    @Test
    public void testParseAbsoluteOrRelativeUrlWithRelativeEmpty() {
        final String text = "";

        this.parseAbsoluteOrRelativeUrlAndCheck(
                text,
                Url.parseRelative(text)
        );
    }

    @Test
    public void testParseAbsoluteOrRelativeUrlWithRelativeFragment() {
        final String text = "#fragment123";

        this.parseAbsoluteOrRelativeUrlAndCheck(
                text,
                Url.parseRelative(text)
        );
    }

    @Test
    public void testParseAbsoluteOrRelativeUrlWithRelativePath() {
        final String text = "/path/path2";

        this.parseAbsoluteOrRelativeUrlAndCheck(
                text,
                Url.parseRelative(text)
        );
    }

    @Test
    public void testParseAbsoluteOrRelativeUrlWithRelativeQueryString() {
        final String text = "?param=value";

        this.parseAbsoluteOrRelativeUrlAndCheck(
                text,
                Url.parseRelative(text)
        );
    }

    private void parseAbsoluteOrRelativeUrlAndCheck(final String url,
                                                    final AbsoluteOrRelativeUrl expected) {
        this.checkEquals(
                expected,
                Url.parseAbsoluteOrRelative(url),
                () -> "parseAbsoluteOrRelative " + CharSequences.quoteAndEscape(url)
        );
    }

    // ClassTesting ...................................................................................................

    @Override
    public Class<Url> type() {
        return Url.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // ParseStringTesting ........................................................................................

    @Override
    public Url parseString(final String text) {
        return Url.parse(text);
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        return expected;
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> expected) {
        return expected;
    }

}
