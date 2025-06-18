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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.predicate.PredicateTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class AcceptTest extends Header2TestCase<Accept, List<MediaType>>
    implements HasQualityFactorSortedValuesTesting,
    ParseStringTesting<Accept>,
    PredicateTesting {

    @Test
    public void testWithDefault() {
        assertSame(
            Accept.DEFAULT,
            Accept.with(
                Accept.ALL_MEDIA_TYPE
            )
        );
    }

    @Test
    public void testWithNullFails() {
        assertThrows(NullPointerException.class, () -> Accept.with(null));
    }

    @Test
    public void testWithEmptyMediaTypesFails() {
        assertThrows(IllegalArgumentException.class, () -> Accept.with(Lists.empty()));
    }

    // parse............................................................................................................

    @Test
    public void testParseStarStar() {
        assertSame(
            Accept.DEFAULT,
            Accept.parse("*/*")
        );
    }

    // HasQualityFactorSortedValuesTesting..............................................................................

    @Test
    public void testQualityFactorSort() {
        final Accept accept = Accept.parse("a/a;q=1.0,b/b;q=0.75");
        this.qualitySortedValuesAndCheck(accept, accept.value());
    }

    @Test
    public void testQualityFactorSort2() {
        this.qualitySortedValuesAndCheck(Accept.parse("a/a;q=0.5,b/b,c/c;q=0.25,d/d;q=1.0"),
            Accept.parse("b/b,d/d;q=1.0,a/a;q=0.5,c/c;q=0.25").value());
    }

    // Predicate........................................................................................................

    @Test
    public void testTestWildcardWildcard() {
        this.testTrue(
            Accept.parse("*/*"),
            MediaType.TEXT_PLAIN
        );
    }

    @Test
    public void testTestIncludesWildcardWildcard() {
        this.testTrue(
            Accept.parse("text/custom,*/*"),
            MediaType.IMAGE_BMP
        );
    }

    @Test
    public void testTestSubTypeWildcardTypeMatched() {
        this.testTrue(
            Accept.parse("text/*"),
            MediaType.TEXT_PLAIN
        );
    }

    @Test
    public void testTestSubTypeWildcardTypeUnmatched() {
        this.testFalse(
            Accept.parse("text/*"),
            MediaType.IMAGE_BMP
        );
    }

    @Test
    public void testTestUnmatched() {
        this.testFalse(
            Accept.parse("text/plain,text/html"),
            MediaType.TEXT_RICHTEXT
        );
    }

    @Test
    public void testTestUnmatched2() {
        this.testFalse(
            Accept.parse("text/plain,image/bmp"),
            MediaType.with("text", "bmp")
        );
    }

    @Test
    public void testTestParametersIgnored() {
        this.testTrue(
            Accept.parse("text/plain;a=1;b=2,text/html;c=3"),
            MediaType.TEXT_PLAIN
        );
    }

    @Test
    public void testTestParametersIgnored2() {
        this.testTrue(
            Accept.parse("text/plain;a=1;b=2,text/html;c=3"),
            MediaType.TEXT_HTML
        );
    }

    @Test
    public void testTestParametersIgnored3() {
        this.testTrue(
            Accept.parse("text/plain;a=1;b=2,text/html;c=3"),
            MediaType.TEXT_HTML.setParameters(
                Maps.of(MediaTypeParameterName.Q, 0.5f)
            )
        );
    }

    // testOrFail.......................................................................................................

    @Test
    public void testTestOrFailWithTextPlainPass() {
        Accept.parse("text/plain,text/html")
            .testOrFail(MediaType.TEXT_PLAIN);
    }

    @Test
    public void testTestOrFailWithTextPlainFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> Accept.parse("text/html,text/json")
                .testOrFail(MediaType.TEXT_PLAIN)
        );

        this.checkEquals(
            "Accept: Got text/plain require text/html, text/json",
            thrown.getMessage()
        );
    }

    @Test
    public void testTestOrFailMessageOmitsParametersFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> Accept.parse("text/html;charset=UTF8, text/json;charset=UTF8;")
                .testOrFail(
                    MediaType.TEXT_PLAIN
                )
        );

        this.checkEquals(
            "Accept: Got text/plain require text/html, text/json",
            thrown.getMessage()
        );
    }

    @Test
    public void testTestOrFailMessageOmitsParametersFails2() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> Accept.parse("text/html, text/json;")
                .testOrFail(
                    MediaType.TEXT_PLAIN.setCharset(CharsetName.UTF_8)
                )
        );

        this.checkEquals(
            "Accept: Got text/plain require text/html, text/json",
            thrown.getMessage()
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHeader(), "image/bmp, image/gif; q=0.5");
    }

    // helpers..........................................................................................................

    @Override
    Accept createHeader(final List<MediaType> value) {
        return Accept.with(value);
    }

    @Override
    List<MediaType> value() {
        return Lists.of(MediaType.IMAGE_BMP, MediaType.IMAGE_GIF.setParameters(Maps.of(MediaTypeParameterName.Q, 0.5f)));
    }

    @Override
    List<MediaType> differentValue() {
        return Lists.of(MediaType.TEXT_PLAIN);
    }

    @Override
    public boolean isMultipart() {
        return false;
    }

    @Override
    public boolean isRequest() {
        return true;
    }

    @Override
    public boolean isResponse() {
        return false;
    }

    // ParseStringTesting ..............................................................................................

    @Override
    public Accept parseString(final String text) {
        return Accept.parse(text);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<Accept> type() {
        return Accept.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
