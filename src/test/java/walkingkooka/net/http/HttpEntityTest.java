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

package walkingkooka.net.http;

import org.junit.jupiter.api.Test;
import walkingkooka.Binary;
import walkingkooka.Cast;
import walkingkooka.net.header.Accept;
import walkingkooka.net.header.AcceptLanguage;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class HttpEntityTest implements ParseStringTesting<HttpEntity>,
        ClassTesting<HttpEntity> {

    // HttpEntity.EMPTY.................................................................................................

    @Test
    public void testEmptySingleton() {
        assertSame(HttpEntity.EMPTY, HttpEntity.EMPTY);
    }

    @Test
    public void testEmptySetBodyText() {
        final String text = "abc";
        final HttpEntity entity = HttpEntity.EMPTY.setBodyText(text);
        this.checkEquals(text, entity.bodyText());
    }

    // parse............................................................................................................

    @Test
    public void testParseNLMissingCr() {
        this.parseStringFails(
                "\n\r\n",
                new IllegalArgumentException("Got NL expected header name or CR")
        );
    }

    @Test
    public void testParseHeaderMissingCrLf() {
        this.parseStringFails(
                "Header\n",
                new IllegalArgumentException("Got NL expected header name or CR")
        );
    }

    @Test
    public void testParseEmptyHeadersEmptyBody() {
        this.parseStringAndCheck(
                "\r\n\r\n",
                HttpEntity.EMPTY
        );
    }

    @Test
    public void testParseEmptyHeaders() {
        this.parseStringAndCheck(
                "\r\n\r\nBody",
                HttpEntity.EMPTY.setBodyText("Body")
        );
    }

    @Test
    public void testParseHeaderAndEmptyBody() {
        this.parseStringAndCheck(
                "Content-Type: text/plain\r\n" +
                        "\r\n\r\n",
                HttpEntity.EMPTY.setContentType(
                        MediaType.TEXT_PLAIN
                )
        );
    }

    @Test
    public void testParseHeaderWithoutValueAndEmptyBody() {
        this.parseStringAndCheck(
                "X-CustomHeader\r\n" +
                        "\r\n\r\n",
                HttpEntity.EMPTY.addHeader(
                        HttpHeaderName.with("X-CustomHeader"),
                        Cast.to("")
                )
        );
    }

    @Test
    public void testParseTwoHeadersAndEmptyBody() {
        this.parseStringAndCheck(
                "Content-Type: text/plain\r\n" +
                        "Accept: */*\r\n" +
                        "\r\n\r\n",
                HttpEntity.EMPTY.setContentType(
                        MediaType.TEXT_PLAIN
                ).addHeader(
                        HttpHeaderName.ACCEPT,
                        Accept.parse("*/*")
                )
        );
    }

    @Test
    public void testParseSeveralHeadersAndEmptyBody() {
        this.parseStringAndCheck(
                "Content-Type: text/plain\r\n" +
                        "Accept: */*\r\n" +
                        "Accept-Language: EN-AU\r\n" +
                        "\r\n\r\n",
                HttpEntity.EMPTY.setContentType(
                        MediaType.TEXT_PLAIN
                ).addHeader(
                        HttpHeaderName.ACCEPT,
                        Accept.parse("*/*")
                ).addHeader(
                        HttpHeaderName.ACCEPT_LANGUAGE,
                        AcceptLanguage.parse("EN-AU")
                )
        );
    }

    @Test
    public void testParseHeadersAndBody() {
        this.parseStringAndCheck(
                "Content-Type: text/plain\r\n" +
                        "\r\n\r\n" +
                        "Body123",
                HttpEntity.EMPTY.setContentType(
                        MediaType.TEXT_PLAIN
                ).setBodyText("Body123")
        );
    }

    @Test
    public void testParseHeadersAndBody2() {
        this.parseStringAndCheck(
                "Content-Type: text/plain\r\n" +
                        "Accept: */*\r\n" +
                        "\r\n\r\n" +
                        "Body123",
                HttpEntity.EMPTY.setContentType(
                        MediaType.TEXT_PLAIN
                ).addHeader(
                        HttpHeaderName.ACCEPT,
                        Accept.parse("*/*")
                ).setBodyText("Body123")
        );
    }

    @Test
    public void testParseHeaderMissingValueFails() {
        this.parseStringFails(
                "Content-Type\r\n",
                new IllegalArgumentException("Failed to convert \"Content-Type\" value \"\", message: text contains only whitespace=\"\"")
        );
    }

    @Test
    public void testParseHeaderInvalidValueFails() {
        this.parseStringFails(
                "Content-Type: BAD\r\n",
                new IllegalArgumentException("Missing type at 4 in \" BAD\"")
        );
    }

    @Override
    public HttpEntity parseString(final String string) {
        return HttpEntity.parse(
                Binary.with(
                        string.getBytes(
                                Charset.defaultCharset()
                        )
                )
        );
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<HttpEntity> type() {
        return HttpEntity.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
