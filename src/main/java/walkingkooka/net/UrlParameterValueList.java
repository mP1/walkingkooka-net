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
        return new UrlParameterValueList(
            Lists.linkedList()
        );
    }

    // @VisibleForTesting
    UrlParameterValueList(final List<String> strings) {
        super();
        this.strings = strings;
    }

    void addParameterValue(final String value) {
        this.strings.add(value);
    }

    void removeParameterValues(final String value) {
        // List.remove only removes the FIRST and not all values...
        this.strings.removeIf(s -> s.equals(value));
    }

    @Override
    public String get(final int index) {
        return this.strings.get(index);
    }

    @Override
    public int size() {
        return this.strings.size();
    }

    private final List<String> strings;

    @Override
    public void elementCheck(final String value) {
        Objects.requireNonNull(value, "value");
    }

    @Override
    public UrlParameterValueList setElements(final Collection<String> elements) {
        return (UrlParameterValueList)
            this.setElementsMaybeCreate(
                elements,
                (final Collection<String> collection) -> collection instanceof UrlParameterValueList,
                this::elementCheck,
                this::replaceStrings
            );
    }

    @Override
    public UrlParameterValueList subList(final int from,
                                         final int to) {
        return 0 == from && this.size() == to ?
            this :
            this.replaceStrings(
                this.strings.subList(
                    from,
                    to
                )
            );
    }

    private UrlParameterValueList replaceStrings(final List<String> strings) {
        return strings.isEmpty() ?
            empty() :
            new UrlParameterValueList(strings);
    }
}
