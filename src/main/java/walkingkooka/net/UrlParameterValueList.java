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

import walkingkooka.collect.list.ImmutableList;
import walkingkooka.collect.list.ImmutableListDefaults;
import walkingkooka.collect.list.Lists;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * A custom immutable {@link List} that appears read only outside this package, but is mutable during the parsing process.
 */
final class UrlParameterValueList extends AbstractList<String> implements ImmutableListDefaults<ImmutableList<String>, String> {

    static UrlParameterValueList empty() {
        return new UrlParameterValueList();
    }

    private UrlParameterValueList() {
    }

    void addParameterValue(final String value) {
        this.values.add(value);
    }

    void removeParameterValues(final String value) {
        // List.remove only removes the FIRST and not all values...
        this.values.removeIf(s -> s.equals(value));
    }

    @Override
    public String get(final int index) {
        return this.values.get(index);
    }

    @Override
    public int size() {
        return this.values.size();
    }

    private final List<String> values = Lists.array();

    @Override
    public void elementCheck(final String value) {
        Objects.requireNonNull(value, "value");
    }

    @Override
    public ImmutableList<String> setElements(final Collection<String> values) {
        return values instanceof UrlParameterValueList ?
            (UrlParameterValueList) values :
            this.createAndCopy(values);
    }

    private ImmutableList<String> createAndCopy(final Collection<String> values) {
        final UrlParameterValueList copy = UrlParameterValueList.empty();
        for (final String value : values) {
            copy.addParameterValue(
                Objects.requireNonNull(value, "value")
            );
        }
        return this.equals(copy) ?
            this :
            copy;
    }
}
