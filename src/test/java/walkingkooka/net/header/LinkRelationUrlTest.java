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
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.HasAbsoluteUrlTesting;

public final class LinkRelationUrlTest extends LinkRelationTestCase<LinkRelationUrl, AbsoluteUrl>
    implements HasAbsoluteUrlTesting {

    @Test
    public void testHeaderText() {
        this.textAndCheck(
            ABSOLUTE_URL.toString()
        );
    }

    @Test
    public void testEqualsDifferentUrl() {
        this.checkNotEquals(
            LinkRelation.with(
                DIFFERENT_ABSOLUTE_URL.toString()
            )
        );
    }

    @Override
    boolean url() {
        return true;
    }

    @Override
    LinkRelationUrl createLinkRelation(final AbsoluteUrl value) {
        return LinkRelationUrl.url(value.toString());
    }

    @Override
    AbsoluteUrl value() {
        return ABSOLUTE_URL;
    }

    @Override
    AbsoluteUrl differentValue() {
        return DIFFERENT_ABSOLUTE_URL;
    }

    @Override
    public Class<LinkRelationUrl> type() {
        return LinkRelationUrl.class;
    }
}
