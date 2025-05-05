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
import walkingkooka.collect.list.Lists;
import walkingkooka.net.header.Accept;
import walkingkooka.net.header.AcceptLanguage;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HttpEntityTest implements ParseStringTesting<HttpEntity>,
    ClassTesting<HttpEntity> {

    // HttpEntity.EMPTY.................................................................................................

    @Test
    public void testEmptySetBodyTextWithEmptyString() {
        assertSame(
            HttpEntity.EMPTY.setBodyText(""),
            HttpEntity.EMPTY
        );
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
    public void testParseHeaderAcceptFails() {
        this.parseStringFails(
            "Accept: ???\r\n",
            new IllegalArgumentException("Accept: Invalid character '?' at 1")
        );
    }

    @Test
    public void testParseHeaderAcceptCharsetFails() {
        this.parseStringFails(
            "Accept-Charset: ???\r\n",
            new IllegalArgumentException("Accept-Charset: Invalid character '?' at 1")
        );
    }

    @Test
    public void testParseHeaderAcceptEncodingFails() {
        this.parseStringFails(
            "Accept-Encoding: ???\r\n",
            new IllegalArgumentException("Accept-Encoding: Invalid character '?' at 1")
        );
    }

    @Test
    public void testParseHeaderAcceptLanguageFails() {
        this.parseStringFails(
            "Accept-Language: ???\r\n",
            new IllegalArgumentException("Accept-Language: Invalid character '?' at 1")
        );
    }

    @Test
    public void testParseHeaderAgeFails() {
        this.parseStringFails(
            "Age: ???\r\n",
            new IllegalArgumentException("Age: Invalid number in \" ???\"")
        );
    }

    @Test
    public void testParseHeaderCacheControlFails() {
        this.parseStringFails(
            "Cache-Control: ???\r\n",
            new IllegalArgumentException("Cache-Control: Invalid character '?' at 1")
        );
    }

    @Test
    public void testParseHeaderContentDispositionFails() {
        this.parseStringFails(
            "Content-Disposition: ???\r\n",
            new IllegalArgumentException("Content-Disposition: Invalid character '?' at 1")
        );
    }

    @Test
    public void testParseHeaderContentEncodingFails() {
        this.parseStringFails(
            "Content-ENcoding: ???\r\n",
            new IllegalArgumentException("Content-Encoding: Invalid character '?' at 1")
        );
    }

    @Test
    public void testParseHeaderContentLanguageFails() {
        this.parseStringFails(
            "Content-Language: ???\r\n",
            new IllegalArgumentException("Content-Language: Invalid character '?' at 1")
        );
    }


    @Test
    public void testParseHeaderContentLengthFails() {
        this.parseStringFails(
            "Content-Length: ???\r\n",
            new IllegalArgumentException("Content-Length: Invalid number in \" ???\"")
        );
    }

    @Test
    public void testParseHeaderContentRangeFails() {
        this.parseStringFails(
            "Content-Range: ???\r\n",
            new IllegalArgumentException("Content-Range: Unit missing from \" ???\"")
        );
    }

    @Test
    public void testParseHeaderContentTypeFails() {
        this.parseStringFails(
            "Content-Type: type/?\r\n",
            new IllegalArgumentException("Content-Type: Invalid character '?' at 6")
        );
    }

    @Test
    public void testParseHeaderCookieFails() {
        this.parseStringFails(
            "Cookie: ???\r\n",
            new IllegalArgumentException("Cookie: Invalid character '?' at 0")
        );
    }

    @Test
    public void testParseHeaderDateFails() {
        this.parseStringFails(
            "Date: ???\r\n",
            new IllegalArgumentException("Date: Invalid date in \" ???\"")
        );
    }

    @Test
    public void testParseHeaderEtagFails() {
        this.parseStringFails(
            "Etag: ???\r\n",
            new IllegalArgumentException("ETag: Invalid character '?' at 1")
        );
    }

    @Test
    public void testParseHeaderFromFails() {
        this.parseStringFails(
            "From: ???\r\n",
            new IllegalArgumentException("From: Invalid email \" ???\"")
        );
    }

    @Test
    public void testParseHeaderIfMatchFails() {
        this.parseStringFails(
            "If-Match: ???\r\n",
            new IllegalArgumentException("If-Match: Invalid character '?' at 1")
        );
    }

    @Test
    public void testParseHeaderIfModifiedSinceFails() {
        this.parseStringFails(
            "If-Modified-Since: ???\r\n",
            new IllegalArgumentException("If-Modified-Since: Invalid date in \" ???\"")
        );
    }

    @Test
    public void testParseHeaderIfNoneMatchFails() {
        this.parseStringFails(
            "If-None-Match: ???\r\n",
            new IllegalArgumentException("If-None-Match: Invalid character '?' at 1")
        );
    }

    @Test
    public void testParseHeaderIfRangeFails() {
        this.parseStringFails(
            "If-Range: ???\r\n",
            new IllegalArgumentException("If-Range: Invalid date in \" ???\"")
        );
    }

    @Test
    public void testParseHeaderIfUnmodifiedSinceFails() {
        this.parseStringFails(
            "If-Unmodified-Since: ???\r\n",
            new IllegalArgumentException("If-Unmodified-Since: Invalid date in \" ???\"")
        );
    }

    @Test
    public void testParseHeaderLinkFails() {
        this.parseStringFails(
            "Link: ???\r\n",
            new IllegalArgumentException("Link: Invalid character '?' at 1")
        );
    }

    @Test
    public void testParseHeaderRangeFails() {
        this.parseStringFails(
            "Range: ???\r\n",
            new IllegalArgumentException("Range: Missing unit and '=' from header value \" ???\"")
        );
    }

    @Test
    public void testParseHeaderRefererFails() {
        this.parseStringFails(
            "Referer: ???\r\n",
            new IllegalArgumentException("Referer: no protocol:  ???")
        );
    }

    @Test
    public void testParseHeaderSetCookieFails() {
        this.parseStringFails(
            "Set-Cookie: ???\r\n",
            new IllegalArgumentException("Set-Cookie: Invalid character '?' at 0")
        );
    }

    @Test
    public void testParseEmptyHeadersEmptyBody() {
        this.parseStringAndCheck(
            "\r\n",
            HttpEntity.EMPTY
        );
    }

    @Test
    public void testParseEmptyHeaders() {
        this.parseStringAndCheck(
            "\r\nBody",
            HttpEntity.EMPTY.setBodyText("Body")
        );
    }

    @Test
    public void testParseHeaderAndEmptyBody() {
        this.parseStringAndCheck(
            "Content-Type: text/plain\r\n" +
                "\r\n",
            HttpEntity.EMPTY.setContentType(
                MediaType.TEXT_PLAIN
            )
        );
    }

    @Test
    public void testParseHeaderWithoutValueAndEmptyBody() {
        this.parseStringAndCheck(
            "X-CustomHeader\r\n" +
                "\r\n",
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
                "\r\n",
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
                "\r\n",
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
                "\r\n" +
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
                "\r\n" +
                "\r\n",
            HttpEntity.EMPTY.setContentType(
                MediaType.TEXT_PLAIN
            ).setBodyText("\r\n")
        );
    }

    @Test
    public void testParseHeadersAndBody3() {
        this.parseStringAndCheck(
            "Content-Type: text/plain\r\n" +
                "Accept: */*\r\n" +
                "\r\n" +
                "Body123",
            HttpEntity.EMPTY.setContentType(
                MediaType.TEXT_PLAIN
            ).addHeader(
                HttpHeaderName.ACCEPT,
                Accept.parse("*/*")
            ).setBodyText("Body123")
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

    // multiparts................................................................................................

    @Test
    public void testMultipartsWhenNotMultipartFails() {
        this.extractMultipartsFails(
            "content-type: text/plain\r\n" +
                "\r\n" +
                "Body123",
            "Not multipart, wrong content-type text/plain"
        );
    }

    @Test
    public void testMultipartsWhenPartMissingNextBoundaryFails() {
        this.extractMultipartsFails(
            "content-type: multipart/form-data;boundary=\"boundary123\"\r\n" +
                "\r\n--boundary123\r\n" +
                "content-disposition: form-data; name=\"part1-name\"\r\n" +
                "\r\n" +
                "Part1\r\n",
            "Part 0 missing boundary after 15"
        );
    }

    @Test
    public void testMultipartsWhenPartMissingContentDispositionFails() {
        this.extractMultipartsFails(
            "content-type: multipart/form-data;boundary=\"boundary123\"\r\n" +
                "\r\n--boundary123\r\n" +
                "\r\n" +
                "Part1\r\n" +
                "--boundary123--",
            "Part 0 missing header \"Content-Disposition\""
        );
    }

    @Test
    public void testMultipartsWhenPartWithInvalidHeaderFails() {
        this.extractMultipartsFails(
            "content-type: multipart/form-data;boundary=\"boundary123\"\r\n" +
                "\r\n--boundary123\r\n" +
                "not-content-disposition: hello\r\n" +
                "\r\n" +
                "Part1\r\n" +
                "--boundary123--",
            "Part 0 missing header \"Content-Disposition\""
        );
    }

    @Test
    public void testMultipartsWhenPart() {
        this.multipartsAndCheck(
            "content-type: multipart/form-data;boundary=\"boundary123\"\r\n" +
                "\r\n--boundary123\r\n" +
                "content-disposition: form-data; name=\"part1-name\"\r\n" +
                "\r\n" +
                "Part1" +
                "\r\n--boundary123--",
            "content-disposition: form-data; name=\"part1-name\"\r\n" +
                "\r\n" +
                "Part1"
        );
    }

    @Test
    public void testMultipartsWhenPartIncludesContentType() {
        this.multipartsAndCheck(
            "content-type: multipart/form-data;boundary=\"boundary123\"\r\n" +
                "\r\n--boundary123\r\n" +
                "content-disposition: form-data; name=\"part1-name\"\r\n" +
                "content-type: text/plain\r\n" +
                "\r\n" +
                "Part1" +
                "\r\n--boundary123--",
            "content-disposition: form-data; name=\"part1-name\"\r\n" +
                "content-type: text/plain\r\n" +
                "\r\n" +
                "Part1"
        );
    }

    @Test
    public void testMultipartsWhenPartIncludesContentTransferEncoding() {
        this.multipartsAndCheck(
            "content-type: multipart/form-data;boundary=\"boundary123\"\r\n" +
                "\r\n--boundary123\r\n" +
                "content-disposition: form-data; name=\"part1-name\"\r\n" +
                "content-type: text/plain\r\n" +
                "\r\n" +
                "Part1" +
                "\r\n--boundary123--",
            "content-disposition: form-data; name=\"part1-name\"\r\n" +
                "content-type: text/plain\r\n" +
                "\r\n" +
                "Part1"
        );
    }

    @Test
    public void testMultipartsWhenPartIncludesContentTransferEncodingAndContentType() {
        this.multipartsAndCheck(
            "content-type: multipart/form-data;boundary=\"boundary123\"\r\n" +
                "\r\n--boundary123\r\n" +
                "content-disposition: form-data; name=\"part1-name\"\r\n" +
                "content-Transfer-Encoding: 8BIT\r\n" +
                "content-type: text/plain\r\n" +
                "\r\n" +
                "Part1" +
                "\r\n--boundary123--",
            "content-disposition: form-data; name=\"part1-name\"\r\n" +
                "content-Transfer-Encoding: 8BIT\r\n" +
                "content-type: text/plain\r\n" +
                "\r\n" +
                "Part1"
        );
    }

    @Test
    public void testMultipartsWhenPartWithEmptyBody() {
        this.multipartsAndCheck(
            "content-type: multipart/form-data;boundary=\"boundary123\"\r\n" +
                "\r\n--boundary123\r\n" +
                "content-disposition: form-data; name=\"part1-name\"\r\n" +
                "\r\n" +
                "\r\n--boundary123--",
            "content-disposition: form-data; name=\"part1-name\"\r\n" +
                "\r\n"
        );
    }

    @Test
    public void testMultipartsWhenPreambleAndPart() {
        this.multipartsAndCheck(
            "content-type: multipart/form-data;boundary=\"boundary123\"\r\n" +
                "\r\n" +
                "Preamble123" +
                "\r\n--boundary123\r\n" +
                "content-disposition: form-data; name=\"part1-name\"\r\n" +
                "\r\n" +
                "Part1" +
                "\r\n--boundary123--",
            "content-disposition: form-data; name=\"part1-name\"\r\n" +
                "\r\n" +
                "Part1"
        );
    }

    @Test
    public void testMultipartsWhenPartAndEpilogue() {
        this.multipartsAndCheck(
            "content-type: multipart/form-data;boundary=\"boundary123\"\r\n" +
                "\r\n--boundary123\r\n" +
                "content-disposition: form-data; name=\"part1-name\"\r\n" +
                "\r\n" +
                "Part1" +
                "\r\n--boundary123--\r\n" +
                "Epilogue123",
            "content-disposition: form-data; name=\"part1-name\"\r\n" +
                "\r\n" +
                "Part1"
        );
    }

    @Test
    public void testMultipartsWhenPreamblePartAndEpilogue() {
        this.multipartsAndCheck(
            "content-type: multipart/form-data;boundary=\"boundary123\"\r\n" +
                "\r\n" +
                "Preamble123" +
                "\r\n--boundary123\r\n" +
                "content-disposition: form-data; name=\"part1-name\"\r\n" +
                "\r\n" +
                "Part1" +
                "\r\n--boundary123--\r\n" +
                "Epilogue123",
            "content-disposition: form-data; name=\"part1-name\"\r\n" +
                "\r\n" +
                "Part1"
        );
    }

    @Test
    public void testMultipartsWhenTwoParts() {
        this.multipartsAndCheck(
            "content-type: multipart/form-data;boundary=\"boundary123\"\r\n" +
                "\r\n--boundary123\r\n" +
                "content-disposition: form-data; name=\"part1-name\"\r\n" +
                "\r\n" +
                "Part1" +
                "\r\n--boundary123\r\n" +
                "content-disposition: form-data; name=\"part2-name\"\r\n" +
                "\r\n" +
                "Part2" +
                "\r\n--boundary123--",
            "content-disposition: form-data; name=\"part1-name\"\r\n" +
                "\r\n" +
                "Part1",
            "content-disposition: form-data; name=\"part2-name\"\r\n" +
                "\r\n" +
                "Part2"
        );
    }

    private void extractMultipartsFails(final String entity,
                                        final String expected) {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> httpEntity(entity).multiparts()
        );

        this.checkEquals(
            expected,
            thrown.getMessage(),
            "message"
        );
    }

    private void multipartsAndCheck(final String entity,
                                    final String... parts) {
        this.multipartsAndCheck(
            httpEntity(entity),
            Arrays.stream(parts)
                .map(this::httpEntity)
                .toArray(HttpEntity[]::new)
        );
    }

    private void multipartsAndCheck(final HttpEntity entity,
                                    final HttpEntity... parts) {
        this.multipartsAndCheck(
            entity,
            Lists.of(parts)
        );
    }

    private void multipartsAndCheck(final HttpEntity entity,
                                    final List<HttpEntity> parts) {
        this.checkEquals(
            parts,
            entity.multiparts(),
            entity::toString
        );
    }

    private HttpEntity httpEntity(final String httpEntity) {
        return HttpEntity.parse(
            Binary.with(
                httpEntity.getBytes(Charset.defaultCharset())
            )
        );
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
