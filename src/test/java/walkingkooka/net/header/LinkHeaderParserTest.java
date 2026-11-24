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
import walkingkooka.net.Url;

import java.util.List;

public final class LinkHeaderParserTest extends HeaderParserTestCase<LinkHeaderParser, List<Link>> {

    private final static Link LINK = Link.with(
        Url.parse("/path/file")
    );

    @Test
    public void testParseKeyValueSeparatorFails() {
        this.parseStringInvalidCharacterFails("=");
    }

    @Test
    public void testParseMultiValueSeparatorFails() {
        this.parseStringInvalidCharacterFails(",");
    }

    @Test
    public void testParseSlashFails() {
        this.parseStringInvalidCharacterFails("/");
    }

    @Test
    public void testParseWildcardFails() {
        this.parseStringInvalidCharacterFails("*");
    }

    @Test
    public void testParseMissingValueFails() {
        this.parseStringMissingValueFails(
            "<",
            1
        );
    }

    @Test
    public void testParseInvalidValueFails() {
        this.parseStringInvalidCharacterFails(
            "https://example.com",
            0
        );
    }

    @Test
    public void testParseLinkAbsoluteUrl() {
        this.parseStringAndCheck2(
            "<https://example.com>",
            Link.with(
                Url.parse("https://example.com")
            )
        );
    }

    @Test
    public void testParseLinkRelativeUrl() {
        this.parseStringAndCheck2(
            "</path/file>",
            LINK
        );
    }

    @Test
    public void testParseLinkTypeParameter() {
        this.parseStringAndCheck2(
            "</path/file>; type=text/plain",
            link(
                LinkParameterName.TYPE,
                MediaType.TEXT_PLAIN
            )
        );
    }

    @Test
    public void testParseLinkRelParameter() {
        this.parseStringAndCheck2(
            "</path/file>; rel=prev",
            link(
                LinkParameterName.REL,
                Lists.of(
                    LinkRelation.PREV
                )
            )
        );
    }

    @Test
    public void testParseLinkRelParameterUrlValue() {
        this.parseStringAndCheck2(
            "</path/file>; rel=https://example.com",
            link(
                LinkParameterName.REL,
                Lists.of(
                    LinkRelation.with("https://example.com")
                )
            )
        );
    }

    @Test
    public void testParseLinkRelParameterMultipleValues() {
        this.parseStringAndCheck2(
            "</path/file>; rel=\"prev next\"",
            link(
                LinkParameterName.REL,
                Lists.of(
                    LinkRelation.PREV,
                    LinkRelation.NEXT
                )
            )
        );
    }

    private static <T> Link link(final LinkParameterName<T> parameterName,
                                 final T parameterValue) {
        return LINK.setParameters(
            Maps.of(
                parameterName,
                parameterValue
            )
        );
    }

    private void parseStringAndCheck2(final String text,
                                      final Link... links) {
        this.parseStringAndCheck(
            text,
            Lists.of(links)
        );
    }

    @Override
    List<Link> parseHeader(final String text) {
        return LinkHeaderParser.parseLink(text);
    }

    @Override
    String valueLabel() {
        return "Link value";
    }

    @Override
    public Class<LinkHeaderParser> type() {
        return LinkHeaderParser.class;
    }
}
