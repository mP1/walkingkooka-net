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

package walkingkooka.net.http.server.hateos;

import walkingkooka.Cast;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeName;
import walkingkooka.tree.xml.XmlDocument;
import walkingkooka.tree.xml.XmlName;
import walkingkooka.tree.xml.XmlNode;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Optional;

public final class TestHateosResource3 extends FakeHateosResource<Optional<String>> {

    static TestHateosResource3 fromJsonNode(final JsonNode node) {
        return with(node.objectOrFail().getOrFail(ID).fromJsonNode(String.class));
    }

    static TestHateosResource3 with(final String id) {
        return new TestHateosResource3(id);
    }

    private TestHateosResource3(final String id) {
        super();
        this.id = id;
    }

    @Override
    public Optional<String> id() {
        return Optional.ofNullable(this.id);
    }

    private final String id;

    @Override
    public String hateosLinkId() {
        return this.id;
    }

    @Override
    public JsonNode toJsonNode() {
        return JsonNode.object()
                .set(ID, HasJsonNode.toJsonNodeObject(this.id));
    }

    private final static JsonNodeName ID = JsonNodeName.with("id");

    @Override
    public XmlNode toXmlNode() {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setValidating(false);
            factory.setExpandEntityReferences(false);

            final XmlDocument document = XmlNode.createDocument(factory.newDocumentBuilder());
            return document.createElement(XmlName.element("test1"))
                    .appendChild(document.createElement(XmlName.element("id"))
                            .appendChild(document.createText(this.id)));
        } catch (final Exception cause) {
            throw new Error(cause.getMessage(), cause);
        }
    }

    static {
        HasJsonNode.register(TestHateosResource3.class.getName(), TestHateosResource3::fromJsonNode, TestHateosResource3.class);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || other instanceof TestHateosResource3 && equals0(Cast.to(other));
    }

    private boolean equals0(final TestHateosResource3 other) {
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return this.id;
    }
}
