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

    @Test
    public void testKeyValueSeparatorFails() {
        this.parseStringInvalidCharacterFails("=");
    }

    @Test
    public void testMultiValueSeparatorFails() {
        this.parseStringInvalidCharacterFails(",");
    }

    @Test
    public void testSlashFails() {
        this.parseStringInvalidCharacterFails("/");
    }

    @Test
    public void testWildcardFails() {
        this.parseStringInvalidCharacterFails("*");
    }

    @Test
    public void testMissingValueFails() {
        this.parseStringMissingValueFails("<", 1);
    }

    @Test
    public void testInvalidValueFails() {
        this.parseStringInvalidCharacterFails("https://example.com", 0);
    }

    @Test
    public void testLinkAbsoluteUrl() {
        this.parseStringAndCheck2("<https://example.com>",
                Link.with(Url.parse("https://example.com")));
    }

    @Test
    public void testLinkRelativeUrl() {
        this.parseStringAndCheck2("</path/file>",
                this.link());
    }

    @Test
    public void testLinkTypeParameter() {
        this.parseStringAndCheck2("</path/file>; type=text/plain",
                this.link(LinkParameterName.TYPE, MediaType.TEXT_PLAIN));
    }

    @Test
    public void testLinkRelParameter() {
        this.parseStringAndCheck2("</path/file>; rel=prev",
                this.link(LinkParameterName.REL, Lists.of(LinkRelation.PREV)));
    }

    @Test
    public void testLinkRelParameterUrlValue() {
        this.parseStringAndCheck2("</path/file>; rel=https://example.com",
                this.link(LinkParameterName.REL, Lists.of(LinkRelation.with("https://example.com"))));
    }

    @Test
    public void testLinkRelParameterMultipleValues() {
        this.parseStringAndCheck2("</path/file>; rel=\"prev next\"",
                this.link(LinkParameterName.REL, Lists.of(LinkRelation.PREV, LinkRelation.NEXT)));
    }

    private Link link() {
        return Link.with(Url.parse("/path/file"));
    }

    private <T> Link link(final LinkParameterName<T> parameterName, final T parameterValue) {
        return this.link().setParameters(Maps.of(parameterName, parameterValue));
    }

    private void parseStringAndCheck2(final String text, final Link... links) {
        this.parseStringAndCheck(text, Lists.of(links));
    }

    @Override
    public List<Link> parseString(final String text) {
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
