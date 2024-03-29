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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class DataUrlTest extends UrlTestCase<DataUrl> {

    @Test
    public void testWithNullMediaTypeFails() {
        assertThrows(NullPointerException.class, () -> DataUrl.with(null, this.binary()));
    }

    @Test
    public void testWithMediaTypeWithParametersFails() {
        assertThrows(IllegalArgumentException.class, () -> DataUrl.with(Optional.of(this.mediaType().setCharset(CharsetName.UTF_8)), this.binary()));
    }

    @Test
    public void testWithNullBinaryFails() {
        assertThrows(NullPointerException.class, () -> DataUrl.with(Optional.of(this.mediaType()), null));
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
    public void testParseWithContentTypeBase64AndEncoded() {
        this.parseStringAndCheck("data:text/plain;base64,YWJjMTIz", this.createUrl());
    }

    @Test
    public void testParseWithContentTypeAndEncoded() {
        this.parseStringAndCheck("data:text/plain,YWJjMTIz", this.createUrl());
    }

    @Test
    public void testParseWithoutContentType() {
        this.parseStringAndCheck("data:;base64,YWJjMTIz", DataUrl.with(Optional.empty(), this.binary()));
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

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createUrl(), "data:text/plain;base64,YWJjMTIz");
    }

    @Override
    DataUrl createUrl() {
        return DataUrl.with(Optional.of(this.mediaType()), this.binary());
    }

    private MediaType mediaType() {
        return MediaType.TEXT_PLAIN;
    }

    private Binary binary() {
        return Binary.with("abc123".getBytes(Charset.defaultCharset()));
    }

    // JsonNodeMarshallTesting .........................................................................................

    @Override
    public DataUrl unmarshall(final JsonNode node,
                              final JsonNodeUnmarshallContext context) {
        return Url.unmarshallData(node, context);
    }

    // ParseStringTesting...............................................................................................

    @Override
    public DataUrl parseString(final String text) {
        return DataUrl.parseData0(text);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<DataUrl> type() {
        return DataUrl.class;
    }
}
