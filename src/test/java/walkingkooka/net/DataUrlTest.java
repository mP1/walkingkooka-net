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
import walkingkooka.net.header.CharsetName;
import walkingkooka.net.header.MediaType;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.visit.Visiting;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class DataUrlTest extends UrlTestCase<DataUrl> {

    @Test
    public void testWithNullMediaTypeFails() {
        assertThrows(
            NullPointerException.class,
            () -> DataUrl.with(
                null,
                true, // base64
                this.binary()
            )
        );
    }

    @Test
    public void testWithMediaTypeWithParametersFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> DataUrl.with(
                Optional.of(
                    this.mediaType()
                        .setCharset(CharsetName.UTF_8)
                ),
                true, // base64
                this.binary()
            )
        );
    }

    @Test
    public void testWithNullBinaryFails() {
        assertThrows(
            NullPointerException.class,
            () -> DataUrl.with(
                Optional.of(
                    this.mediaType()
                ),
                true, // base64
                null
            )
        );
    }

    // ParseTesting.....................................................................................................

    @Test
    public void testParseNonDataUrlFails() {
        this.parseStringFails("https://example.com", IllegalArgumentException.class);
    }

    @Test
    public void testParseNonBase64Fails() {
        this.parseStringFails("data:text/plain;unsupported,XYZ123", IllegalArgumentException.class);
    }

    @Test
    public void testParseMissingBinaryFails() {
        this.parseStringFails("data:text/plain;base64", IllegalArgumentException.class);
    }

    @Test
    public void testParseEmptyData() {
        this.parseStringAndCheck(
            "data:,",
            DataUrl.with(
                Optional.empty(),
                false, // base64=false
                Binary.EMPTY
            )
        );
    }

    // https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/Data_URLs
    @Test
    public void testParseEmptyContentType() {
        this.parseStringAndCheck(
            "data:,Hello%2C%20World%21",
            DataUrl.with(
                Optional.empty(),
                false, // base64=false
                Binary.with(
                    "Hello, World!".getBytes(StandardCharsets.UTF_8)
                )
            )
        );
    }

    @Test
    public void testParseWithContentTypeBase64AndEncoded() {
        this.parseStringAndCheck("data:text/plain;base64,YWJjMTIz", this.createUrl());
    }

    @Test
    public void testParseWithContentTypeAndEncoded() {
        this.parseStringAndCheck(
            "data:text/plain,Hello%2C%20World%21",
            DataUrl.with(
                Optional.of(
                    MediaType.TEXT_PLAIN
                ),
                false, // base64=true
                Binary.with(
                    "Hello, World!".getBytes(StandardCharsets.UTF_8)
                )
            ),
            "Hello%2C%20World%21"
        );
    }

    @Test
    public void testParseWithoutContentType() {
        this.parseStringAndCheck(
            "data:;base64,YWJjMTIz",
            DataUrl.with(
                Optional.empty(),
                true, // base64=true
                this.binary()
            ),
            "YWJjMTIz"
        );
    }

    @Test
    public void testParseMdnPlainTextExample() {
        this.parseStringAndCheck(
            "data:text/plain;base64,SGVsbG8sIFdvcmxkIQ==",
            DataUrl.with(
                Optional.of(
                    MediaType.TEXT_PLAIN
                ),
                true, // base64=true
                Binary.with(
                    "Hello, World!".getBytes(StandardCharsets.UTF_8)
                )
            ),
            "SGVsbG8sIFdvcmxkIQ=="
        );
    }

    @Test
    public void testParseMdnPlainHtmlExample() {
        this.parseStringAndCheck(
            "data:text/html,%3Ch1%3EHello%2C%20World%21%3C%2Fh1%3E",
            DataUrl.with(
                Optional.of(
                    MediaType.TEXT_HTML
                ),
                false, // base64=false
                Binary.with(
                    "<h1>Hello, World!</h1>".getBytes(StandardCharsets.UTF_8)
                )
            ),
            "%3Ch1%3EHello%2C%20World%21%3C%2Fh1%3E"
        );
    }

    @Test
    public void testParseMdnJavascriptExample() {
        this.parseStringAndCheck(
            "data:text/html,%3Cscript%3Ealert%28%27hi%27%29%3B%3C%2Fscript%3E",
            DataUrl.with(
                Optional.of(
                    MediaType.TEXT_HTML
                ),
                false, // base64=false
                Binary.with(
                    "<script>alert('hi');</script>".getBytes(StandardCharsets.UTF_8)
                )
            ),
            "%3Cscript%3Ealert%28%27hi%27%29%3B%3C%2Fscript%3E"
        );
    }

    @Test
    public void testParseCachesUrl() {
        final String url = "data:text/html,%3Cscript%3Ealert%28%27hi%27%29%3B%3C%2Fscript%3E";
        final DataUrl dataUrl = DataUrl.parseData(url);

        assertSame(
            url,
            dataUrl.value()
        );
    }

    @Override
    public DataUrl parseString(final String text) {
        return DataUrl.parseData0(text);
    }

    private DataUrl parseStringAndCheck(final String text,
                                        final DataUrl expected,
                                        final String data) {
        final DataUrl dataUrl = this.parseStringAndCheck(
            text,
            expected
        );
        this.dataAndCheck(
            dataUrl,
            data
        );

        return dataUrl;
    }

    private void dataAndCheck(final DataUrl dataUrl,
                              final String expected) {
        this.checkEquals(
            expected,
            dataUrl.data(),
            dataUrl::toString
        );
    }

    // UrlVisitor......................................................................................................

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final DataUrl url = this.createUrl();

        new FakeUrlVisitor() {
            @Override
            protected Visiting startVisit(final Url u) {
                assertSame(url, u);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Url u) {
                assertSame(url, u);
                b.append("2");
            }

            @Override
            protected void visit(final DataUrl u) {
                assertSame(url, u);
                b.append("5");
            }
        }.accept(url);
        this.checkEquals("152", b.toString());
    }

    // DataUrlTest......................................................................................................

    @Override
    DataUrl createUrl() {
        return DataUrl.with(
            Optional.of(
                this.mediaType()
            ),
            true, // base64
            this.binary()
        );
    }

    private MediaType mediaType() {
        return MediaType.TEXT_PLAIN;
    }

    private Binary binary() {
        return Binary.with("abc123".getBytes(Charset.defaultCharset()));
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createUrl(), "data:text/plain;base64,YWJjMTIz");
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<DataUrl> type() {
        return DataUrl.class;
    }

    // JsonNodeMarshallTesting .........................................................................................

    @Override
    public DataUrl unmarshall(final JsonNode node,
                              final JsonNodeUnmarshallContext context) {
        return Url.unmarshallData(node, context);
    }
}
