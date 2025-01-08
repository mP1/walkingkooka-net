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
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.RelativeUrl;
import walkingkooka.net.Url;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class LinkTest extends HeaderWithParametersTestCase<Link,
    LinkParameterName<?>>
    implements JsonNodeMarshallingTesting<Link>,
    ParseStringTesting<List<Link>> {

    @Test
    public void testWithNullFails() {
        assertThrows(NullPointerException.class, () -> Link.with(null));
    }

    @Test
    public void testWithAbsoluteUrl() {
        final AbsoluteUrl url = this.value();
        this.check(Link.with(url), url, Link.NO_PARAMETERS);
    }

    @Test
    public void testWithRelativeUrl() {
        final RelativeUrl url = Url.parseRelative("/path/file");
        this.check(Link.with(url), url, Link.NO_PARAMETERS);
    }

    @Test
    public void testSetValueNullFails() {
        assertThrows(NullPointerException.class, () -> this.createLink().setValue(null));
    }

    @Test
    public void testSetValueSame() {
        final Link link = this.createLink();
        assertSame(link, link.setValue(link.value()));
    }

    @Test
    public void testSetValueSame2() {
        final Link link = this.createLink();
        assertSame(link, link.setValue(this.value()));
    }

    @Test
    public void testSetValueDifferent() {
        final Link link = this.createLink();
        final AbsoluteUrl value = Url.parseAbsolute("https://example2.com");
        final Link different = link.setValue(value);
        assertNotSame(link, different);

        this.check(different, value, Link.NO_PARAMETERS);
    }

    @Test
    public void testSetParameterDifferent() {
        final Link link = this.createLink();
        final Map<LinkParameterName<?>, Object> parameters = this.parameters();
        final Link different = link.setParameters(parameters);
        this.check(different, link.value(), parameters);
    }

    @Test
    public void testSetParametersDifferentAndBack() {
        final Link link = this.createLink();
        this.checkEquals(link,
            link
                .setParameters(this.parameters())
                .setParameters(Link.NO_PARAMETERS));
    }

    private Map<LinkParameterName<?>, Object> parameters() {
        return Maps.of(LinkParameterName.REL, LinkRelation.parse("previous"));
    }

    void check(final Link language,
               final Url value,
               final Map<LinkParameterName<?>, Object> parameters) {
        this.checkEquals(value, language.value(), "value");
        this.checkParameters(language, parameters);
    }


    // toHeaderTextList.......................................................................................

    @Test
    public void testToHeaderTextListListOfOne() {
        this.toHeaderTextListAndCheck("<https://example.com>",
            this.createLink());
    }

    @Test
    public void testToHeaderTextListListOfOneWithParameters() {
        this.toHeaderTextListAndCheck("<https://example.com>;rel=previous",
            this.createLink().setParameters(Maps.of(LinkParameterName.REL, LinkRelation.parse("previous"))));
    }

    @Test
    public void testToHeaderTextListListOfMany() {
        this.toHeaderTextListAndCheck(
            "<https://example.com>, <https://example2.com>",
            this.createLink(),
            Link.with(
                Url.parse("https://example2.com")
            )
        );
    }

    // marshall .....................................................................................................

    @Test
    public void testUnmarshallBooleanFails() {
        this.unmarshallFails(JsonNode.booleanNode(true));
    }

    @Test
    public void testUnmarshallNumberFails() {
        this.unmarshallFails(JsonNode.number(123));
    }

    @Test
    public void testUnmarshallStringFails() {
        this.unmarshallFails(JsonNode.string("fails!"));
    }

    @Test
    public void testUnmarshallArrayFails() {
        this.unmarshallFails(JsonNode.array());
    }

    @Test
    public void testUnmarshallObjectEmptyFails() {
        this.unmarshallFails(JsonNode.object());
    }

    @Test
    public void testUnmarshallHrefNonStringFails() {
        this.unmarshallFails(JsonNode.object().set(Link.HREF_JSON_PROPERTY, JsonNode.number(123)));
    }

    @Test
    public void testUnmarshallUnknownPropertyFails() {
        this.unmarshallFails(JsonNode.object().set(JsonPropertyName.with("unknown-property"), JsonNode.number(123)));
    }

    @Test
    public void testUnmarshallLink() {
        final String href = "https://example.com";
        this.unmarshallAndCheck(JsonNode.object().set(Link.HREF_JSON_PROPERTY, JsonNode.string(href)),
            Link.with(Url.parse(href)));
    }

    @Test
    public void testMarshall() {
        this.marshallAndCheck2("<https://example.com>",
            "{\"href\": \"https://example.com\"}");
    }

    @Test
    public void testMarshallRel() {
        this.marshallAndCheck2("<https://example.com>;type=text/plain;rel=previous",
            "{\"href\": \"https://example.com\", \"rel\": \"previous\", \"type\": \"text/plain\"}");
    }

    private void marshallAndCheck2(final String link, final String json) {
        this.marshallAndCheck(Link.parse(link).get(0), json);
    }

    // HashCodeEqualsDefined ..................................................................................................

    @Test
    public void testEqualsDifferentUrl() {
        this.checkNotEquals(Link.with(Url.parse("/different")).setParameters(this.parameters()));
    }

    @Test
    public void testEqualsDifferentParameters() {
        this.checkNotEquals(this.createLink().setParameters(Maps.of(LinkParameterName.TYPE, MediaType.TEXT_PLAIN)));
    }

    @Test
    public void testEqualsDifferentParameters2() {
        this.checkNotEquals(this.createLink().setParameters(Maps.of(LinkParameterName.TYPE, MediaType.TEXT_PLAIN)),
            this.createLink().setParameters(Maps.of(LinkParameterName.TYPE, MediaType.BINARY)));
    }

    // parse.......................................................................................

    @Test
    public void testParseLinkWithMedia() {
        this.parseStringAndCheck("<https://example.com>;media=\"abc 123\"",
            this.createLink().setParameters(Maps.of(LinkParameterName.MEDIA, "abc 123")));
    }

    @Test
    public void testParseLinkWithMethod() {
        this.parseStringAndCheck("<https://example.com>;method=GET",
            this.createLink().setParameters(Maps.of(LinkParameterName.METHOD, HttpMethod.GET)));
    }

    @Test
    public void testParseLinkWithType() {
        this.parseStringAndCheck("<https://example.com>;type=text/plain",
            this.createLink().setParameters(Maps.of(LinkParameterName.TYPE, MediaType.TEXT_PLAIN)));
    }

    @Test
    public void testParseSeveralLinks() {
        this.parseStringAndCheck(
            "<https://example.com>;rel=previous, <https://example2.com>",
            this.createLink()
                .setParameters(
                    Maps.of(
                        LinkParameterName.REL, LinkRelation.parse("previous")
                    )
                ),
            Link.with(
                Url.parse("https://example2.com")
            )
        );
    }

    private void parseStringAndCheck(final String text, final Link... links) {
        this.parseStringAndCheck(text, Lists.of(links));
    }

    // equalsIgnoringParameters.........................................................................................

    @Test
    public void testEqualsIgnoringParametersDifferent() {
        this.equalsIgnoringParametersAndCheck(
            Link.parse("<https://example.com>").get(0),
            Link.parse("<https://different.example.com>").get(0),
            false);
    }

    @Test
    public void testEqualsIgnoringParametersDifferentParameters() {
        this.equalsIgnoringParametersAndCheck(
            Link.parse("<https://example.com>;a=1").get(0),
            Link.parse("<https://example.com>;b=2").get(0),
            true);
    }

    // equalsOnlyPresentParameters.........................................................................................

    @Test
    public void testEqualsOnlyPresentParametersDifferent() {
        this.equalsOnlyPresentParametersAndCheck(
            Link.parse("<https://example.com>").get(0),
            Link.parse("<https://different.example.com>").get(0),
            false);
    }

    @Test
    public void testEqualsOnlyPresentParametersDifferentParameters() {
        this.equalsOnlyPresentParametersAndCheck(
            Link.parse("<https://example.com>; a=1").get(0),
            Link.parse("<https://example.com>; b=2").get(0),
            false);
    }

    @Test
    public void testEqualsOnlyPresentParametersDifferentParameters2() {
        this.equalsOnlyPresentParametersAndCheck(
            Link.parse("<https://example.com>; a=1&b=2").get(0),
            Link.parse("<https://example.com>; b=2").get(0),
            false);
    }

    @Test
    public void testEqualsOnlyPresentParametersSharedParameters() {
        this.equalsOnlyPresentParametersAndCheck(
            Link.parse("<https://example.com>; a=1").get(0),
            Link.parse("<https://example.com>; a=1").get(0),
            true);
    }

    @Test
    public void testEqualsOnlyPresentParametersSharedAndIgnoredParameters() {
        this.equalsOnlyPresentParametersAndCheck(
            Link.parse("<https://example.com>; a=1").get(0),
            Link.parse("<https://example.com>; a=1; b=2").get(0),
            true);
    }

    // helpers.......................................................................................

    @Override
    public Link createHeaderWithParameters() {
        return this.createLink();
    }

    private Link createLink() {
        return Link.with(this.value());
    }

    private AbsoluteUrl value() {
        return Url.parseAbsolute("https://example.com");
    }

    @Override
    LinkParameterName<?> parameterName() {
        return LinkParameterName.with("xyz");
    }

    @Override
    public Link createDifferentHeader() {
        return Link.with(Url.parse("https://different.example.com"));
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
        return true;
    }

    @Override
    public Class<Link> type() {
        return Link.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // ParseStringTesting ........................................................................................

    @Override
    public List<Link> parseString(final String text) {
        return Link.parse(text);
    }

    // JsonNodeContextTesting...........................................................................................

    @Override
    public Link createJsonNodeMarshallingValue() {
        return this.createLink();
    }

    @Override
    public Link unmarshall(final JsonNode from,
                           final JsonNodeUnmarshallContext context) {
        return Link.unmarshall(from, context);
    }
}
