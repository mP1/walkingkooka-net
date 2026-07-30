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

import walkingkooka.collect.list.ImmutableListDefaults;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.HasText;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * An immutable list holding non null {@link ETag}.
 */
public final class ETagList extends AbstractList<ETag> implements ImmutableListDefaults<ETagList, ETag>,
    HasText {

    /**
     * Empty immutable singleton
     */
    public static final ETagList EMPTY = new ETagList(Lists.empty());

    /**
     * Parsers a header value which may hold one or more tags.
     */
    public static ETagList parse(final String text) {
        return ETagListHeaderParser.parseList(text);
    }

    private final List<ETag> etags;

    ETagList(final List<ETag> etags) {
        this.etags = etags;
    }

    @Override
    public ETag get(int index) {
        return this.etags.get(index);
    }

    @Override
    public int size() {
        return this.etags.size();
    }

    @Override
    public void elementCheck(final ETag etag) {
        Objects.requireNonNull(etag, "etag");
    }

    @Override
    public ETagList setElements(final Collection<ETag> etags) {
        Objects.requireNonNull(etags, "etags");

        ETagList eTagList;
        if (etags instanceof ETagList) {
            eTagList = (ETagList) etags;
        } else {
            List<ETag> copy = Lists.array();
            copy.addAll(etags);
            switch (etags.size()) {
                case 0:
                    eTagList = EMPTY;
                    break;
                default:
                    eTagList = new ETagList(copy);
            }
        }

        return this.equals(eTagList) ?
            this :
            eTagList;
    }

    // HasText..........................................................................................................

    @Override
    public String text() {
        return Header.toHeaderTextList(
            this,
            ETagListHeaderHandler.SEPARATOR
        );
    }
}
