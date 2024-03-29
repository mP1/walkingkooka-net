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

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class UrlTest implements ClassTesting2<Url>,
        ParseStringTesting<Url> {

    // EMPTY RELATIVE URL...............................................................................................

    @Test
    public void testEmptyRelativeUrl() {
        final RelativeUrl relativeUrl = Url.EMPTY_RELATIVE_URL;
        this.checkEquals("", relativeUrl.toString());
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
        final DataUrl url = Url.data(Optional.of(MediaType.TEXT_PLAIN), Binary.with("abc123".getBytes(Charset.defaultCharset())));

        this.parseStringAndCheck(url.value(), Url.parseData(url.value()));
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
                        MediaType.TEXT_PLAIN),
                Binary.with("abc123".getBytes(Charset.defaultCharset())
                )
        );

        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> Url.parseAbsoluteOrRelative(url.value())
        );

        this.checkEquals(
                "unknown protocol: data",
                thrown.getMessage()
        );
    }

    @Test
    public void testParseAbsoluteOrRelativeUrlWithAbsolute() {
        final String text = "https://example.com/path1";

        this.parseAbsoluteOrRelativeUrlAndCheck(
                text,
                Url.parseAbsolute(text)
        );
    }

    @Test
    public void testParseAbsoluteOrRelativeUrlWithRelative() {
        final String text = "/path/path2";

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
