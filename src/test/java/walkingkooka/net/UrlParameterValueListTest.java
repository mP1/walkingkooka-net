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

import org.junit.jupiter.api.Test;
import walkingkooka.CanBeEmpty;
import walkingkooka.collect.list.ImmutableListTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class UrlParameterValueListTest implements ClassTesting2<UrlParameterValueList>,
    ImmutableListTesting<UrlParameterValueList, String> {

    @Test
    public void testIsEmpty() {
        this.isEmptyAndCheck(
            (CanBeEmpty) UrlParameterValueList.empty(),
            true
        );
    }

    @Test
    public void testNotEmpty() {
        final UrlParameterValueList list = UrlParameterValueList.empty();

        final String a1 = "a1";
        final String b2 = "b2";
        final String c3 = "c3";

        list.addParameterValue(a1);
        list.addParameterValue(b2);
        list.addParameterValue(c3);

        this.getAndCheck(list, 0, a1);
        this.getAndCheck(list, 1, b2);
        this.getAndCheck(list, 2, c3);
    }

    @Test
    public void testGetInvalidIndexFails() {
        this.getFails(UrlParameterValueList.empty(), 0);
        this.getFails(UrlParameterValueList.empty(), 1);
    }

    @Test
    public void testGetInvalidIndexFails2() {
        final UrlParameterValueList list = UrlParameterValueList.empty();

        list.addParameterValue("a1");
        list.addParameterValue("b2");
        list.addParameterValue("c3");

        this.getFails(list, -1);
        this.getFails(list, 3);
    }

    @Test
    public void testSwap() {
        final UrlParameterValueList list = UrlParameterValueList.empty();

        list.addParameterValue("a1");
        list.addParameterValue("b2");
        list.addParameterValue("c3");

        final UrlParameterValueList expected = UrlParameterValueList.empty();

        expected.addParameterValue("c3");
        expected.addParameterValue("b2");
        expected.addParameterValue("a1");

        this.swapAndCheck(
            list,
            0,
            2,
            expected
        );
    }

    @Test
    public void testSetElementsWithListWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> UrlParameterValueList.empty()
                .setElements(
                    Lists.of(
                        "A1",
                        null
                    )
                )
        );
    }

    @Override
    public UrlParameterValueList createList() {
        return UrlParameterValueList.empty();
    }

    @Override
    public Class<UrlParameterValueList> type() {
        return UrlParameterValueList.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
