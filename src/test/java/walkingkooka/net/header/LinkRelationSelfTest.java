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
import walkingkooka.reflect.JavaVisibility;

public final class LinkRelationSelfTest extends LinkRelationTestCase<LinkRelationSelf, String> {

    @Override
    public void testWith2() {
    }

    @Test
    public void testHeaderText() {
        this.toHeaderTextAndCheck("self");
    }

    @Override
    public void testEqualsDifferentValue() {
    }

    @Test
    public void testEqualsUrl() {
        this.checkNotEquals(LinkRelation.with("http://example.com"));
    }

    @Override
    public void testEqualsIgnoringParametersDifferent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEqualsOnlyPresentParametersDifferentFalse() {
        throw new UnsupportedOperationException();
    }

    @Override
    boolean url() {
        return false;
    }

    @Override
    LinkRelationSelf createLinkRelation(final String value) {
        this.checkEquals("self", value, "value");
        return LinkRelationSelf.create();
    }

    @Override
    String value() {
        return "self";
    }

    @Override
    String differentValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<LinkRelationSelf> type() {
        return LinkRelationSelf.class;
    }

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
