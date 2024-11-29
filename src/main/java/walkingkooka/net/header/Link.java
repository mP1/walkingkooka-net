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

import walkingkooka.Cast;
import walkingkooka.Value;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.Url;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.JsonString;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallException;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;


/**
 * A {@link Value} as described in <a href="https://tools.ietf.org/search/rfc5988"></a>.
 */
final public class Link extends HeaderWithParameters2<Link,
        LinkParameterName<?>,
        Url> {

    /**
     * No parameters.
     */
    public final static Map<LinkParameterName<?>, Object> NO_PARAMETERS = Maps.empty();

    /**
     * Creates a {@link Link} after parsing the text.
     */
    public static List<Link> parse(final String text) {
        return LinkHeaderParser.parseLink(text);
    }

    /**
     * Converts the list of links into header text.
     */
    public static String toHeaderTextList(final List<Link> links) {
        return Header.toHeaderTextList(links, Link.SEPARATOR.string());
    }

    /**
     * Creates a {@link Link} using the already broken type and sub types. It is not possible to pass parameters with or without values.
     */
    public static Link with(final Url url) {
        checkValue(url);

        return new Link(url, NO_PARAMETERS);
    }

    // ctor ...................................................................................................

    /**
     * Private constructor
     */
    private Link(final Url url,
                 final Map<LinkParameterName<?>, Object> parameters) {
        super(url, parameters);
    }

    // value.....................................................................................................

    /**
     * Would be setter that returns a {@link Link} with the given value creating a new instance as necessary.
     */
    public Link setValue(final Url value) {
        checkValue(value);

        return this.value.equals(value) ?
                this :
                this.replace(value);
    }

    private static void checkValue(final Url value) {
        Objects.requireNonNull(value, "value");
    }

    // replaceParameters ...............................................................................................

    private Link replace(final Url value) {
        return this.replace0(value, this.parameters);
    }

    @Override
    Link replaceParameters(final Map<LinkParameterName<?>, Object> parameters) {
        return this.replace0(this.value, parameters);
    }

    private Link replace0(final Url value, final Map<LinkParameterName<?>, Object> parameters) {
        return new Link(value, parameters);
    }

    // Header................................................................................................................

    @Override
    String toHeaderTextValue() {
        return "<" + this.value + ">";
    }

    @Override
    String toHeaderTextParameterSeparator() {
        return PARAMETER_SEPARATOR.string();
    }

    /**
     * Links do not support wildcards in any form.
     */
    @Override
    public boolean isWildcard() {
        return false;
    }

    // HasHeaderScope ....................................................................................................

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

    // JsonNodeContext..................................................................................................

    /**
     * Accepts a json object with a single required property href.
     */
    static Link unmarshall(final JsonNode node,
                           final JsonNodeUnmarshallContext context) {
        Objects.requireNonNull(node, "node");

        String href = null;

        for (JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case "href":
                    if (!child.isString()) {
                        throw new JsonNodeUnmarshallException("Property " + name + " is not a String=" + node, node);
                    }
                    href = child.cast(JsonString.class).value();
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
            }
        }

        if (null == href) {
            JsonNodeUnmarshallContext.requiredPropertyMissing(HREF_JSON_PROPERTY, node);
        }
        return Link.with(Url.parse(href));
    }

    /**
     * Builds the json representation of this link, with the value assigned to HREF attribute.
     */
    private JsonNode marshall(final JsonNodeMarshallContext context) {
        JsonObject json = JsonNode.object()
                .set(HREF_JSON_PROPERTY, JsonNode.string(this.value.toString()));

        for (Entry<LinkParameterName<?>, Object> parameterNameAndValue : this.parameters.entrySet()) {
            final LinkParameterName<?> name = parameterNameAndValue.getKey();

            json = json.set(JsonPropertyName.with(name.value()),
                    JsonNode.string(name.handler.toText(Cast.to(parameterNameAndValue.getValue()), name)));
        }


        return json;
    }

    /**
     * The attribute on the json object which will hold the {@link #value}.
     */
    // @VisibleForTesting
    final static JsonPropertyName HREF_JSON_PROPERTY = JsonPropertyName.with("href");

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(Link.class),
                Link::unmarshall,
                Link::marshall,
                Link.class
        );
    }

    // Object................................................................................................................

    @Override
    int hashCode0(final Url value) {
        return value.hashCode();
    }

    @Override
    boolean equals1(final Url value, final Url otherValue) {
        return value.equals(otherValue);
    }

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof Link;
    }
}
