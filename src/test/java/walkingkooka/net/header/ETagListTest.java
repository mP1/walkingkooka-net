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
import walkingkooka.collect.list.ImmutableListTesting;
import walkingkooka.collect.list.ListTesting2;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.HasTextTesting;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ETagListTest implements ListTesting2<ETagList, ETag>,
    ClassTesting<ETagList>,
    ImmutableListTesting<ETagList, ETag>,
    ParseStringTesting<ETagList>,
    HasTextTesting {

    private final static ETag ETAG1 = ETag.strong("strong111");

    private final static ETag ETAG2 = ETag.weak("weak222");

    // list.............................................................................................................

    @Test
    public void testGet() {
        this.getAndCheck(
            this.createList(),
            0, // index
            ETAG1 // expected
        );
    }

    @Test
    public void testGet2() {
        this.getAndCheck(
            this.createList(),
            1, // index
            ETAG2 // expected
        );
    }

    @Test
    public void testSetFails() {
        this.setFails(
            this.createList(),
            0, // index
            ETAG1 // expected
        );
    }

    @Test
    public void testRemoveIndexFails() {
        final ETagList list = this.createList();

        this.removeIndexFails(
            list,
            0
        );
    }

    @Test
    public void testRemoveElementFails() {
        final ETagList list = this.createList();

        this.removeFails(
            list,
            list.get(0)
        );
    }

    // setElements......................................................................................................

    @Test
    public void testWithDoesntDoubleWrap() {
        final ETagList list = this.createList();
        assertSame(
            list,
            list.setElements(list)
        );
    }

    @Test
    public void testSetElementsWithEmpty() {
        assertSame(
            ETagList.EMPTY,
            new ETagList(
                Lists.of(
                    ETAG1,
                    ETAG2
                )
            ).setElements(Lists.empty())
        );
    }

    // replace..........................................................................................................

    @Test
    public void testReplaceWithNullFails() {
        final ETagList etags = this.createList();

        assertThrows(
            NullPointerException.class,
            () -> etags.replace(
                1,
                null
            )
        );
    }

    @Override
    public ETagList createList() {
        return new ETagList(
            Lists.of(
                ETAG1,
                ETAG2
            )
        );
    }

    // firstOrEmpty.....................................................................................................

    @Test
    public void testFirstOrEmptyWhenEmpty() {
        this.firstOrEmptyAndCheck(
            ETagList.EMPTY
        );
    }

    @Test
    public void testFirstOrEmptyWhenNotEmpty() {
        this.firstOrEmptyAndCheck(
            ETagList.EMPTY.concat(ETAG1)
                .concat(ETAG2),
            ETAG1
        );
    }

    // parse............................................................................................................

    @Test
    @Override
    public void testParseStringEmptyFails() {
        assertThrows(
            HeaderException.class,
            () -> this.parseString("")
        );
    }

    @Test
    public void testParseStrong() {
        this.parseStringAndCheck(
            "\"strong111\"",
            ETagList.EMPTY.concat(ETAG1)
        );
    }

    @Test
    public void testParseWeak() {
        this.parseStringAndCheck(
            "W/\"weak222\"",
            ETagList.EMPTY.concat(ETAG2)
        );
    }

    @Test
    public void testParseWildcard() {
        this.parseStringAndCheck(
            "*",
            ETagList.EMPTY.concat(
                ETag.wildcard()
            )
        );
    }

    @Override
    public ETagList parseString(final String text) {
        return ETagList.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // HasText..........................................................................................................

    @Test
    public void testTextWhenEmpty() {
        this.textAndCheck(
            ETagList.EMPTY,
            ""
        );
    }

    @Test
    public void testTextWhenNotEmpty() {
        this.textAndCheck(
            ETagList.EMPTY.setElements(
                Lists.of(
                    ETAG1,
                    ETAG2,
                    ETag.wildcard()
                )
            ),
            "\"strong111\", W/\"weak222\", *"
        );
    }

    // class............................................................................................................

    @Override
    public Class<ETagList> type() {
        return ETagList.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
