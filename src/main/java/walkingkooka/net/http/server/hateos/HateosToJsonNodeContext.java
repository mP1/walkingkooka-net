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

import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonStringNode;
import walkingkooka.tree.json.marshall.ToJsonNodeContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link ToJsonNodeContext} which adds links for values that are {@link HateosResource} registered with relations.
 */
final class HateosToJsonNodeContext implements ToJsonNodeContext {

    static HateosToJsonNodeContext with(final ToJsonNodeContext context) {
        Objects.requireNonNull(context, "context");

        return new HateosToJsonNodeContext(context);
    }

    private HateosToJsonNodeContext(final ToJsonNodeContext context) {
        super();
        this.context = context;
    }

    @Override
    public Optional<Class<?>> registeredType(final JsonStringNode typeName) {
        return this.context.registeredType(typeName);
    }

    @Override
    public Optional<JsonStringNode> typeName(final Class<?> type) {
        return this.context.typeName(type);
    }

    @Override
    public JsonNode toJsonNode(final Object value) {
        return this.context.toJsonNode(value);
    }

    @Override
    public JsonNode toJsonNodeWithType(final Object value) {
        return this.context.toJsonNodeWithType(value);
    }

    @Override
    public JsonNode toJsonNodeList(final List<?> list) {
        return this.context.toJsonNodeList(list);
    }

    @Override
    public JsonNode toJsonNodeSet(final Set<?> set) {
        return this.context.toJsonNodeSet(set);
    }

    @Override
    public JsonNode toJsonNodeMap(final Map<?, ?> map) {
        return this.context.toJsonNodeMap(map);
    }

    @Override
    public JsonNode toJsonNodeWithTypeList(final List<?> list) {
        return this.context.toJsonNode(list);
    }

    @Override
    public JsonNode toJsonNodeWithTypeMap(final Map<?, ?> map) {
        return this.context.toJsonNode(map);
    }

    @Override
    public JsonNode toJsonNodeWithTypeSet(final Set<?> set) {
        return this.context.toJsonNode(set);
    }

    private final ToJsonNodeContext context;

    @Override
    public String toString() {
        return this.context.toString();
    }
}

