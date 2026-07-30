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
import walkingkooka.collect.list.Lists;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ETagListHeaderHandlerTest extends
    NonStringHeaderHandlerTestCase<ETagListHeaderHandler, ETagList> {

    private static final ETag ETAG = ETag.weak("value");

    @Override
    public String typeNamePrefix() {
        return ETag.class.getSimpleName();
    }

    @Test
    public void testParseETagOne() {
        this.parseAndToTextAndCheck(
            "W/\"123\"",
            ETagList.EMPTY.concat(
                ETag.weak("123")
            )
        );
    }

    @Test
    public void testParseETagSeveral() {
        this.toTextAndCheck(
            ETagList.EMPTY.setElements(
                Lists.of(
                    ETag.weak("123"),
                    ETag.weak("456")
                )
            ),
            "W/\"123\", W/\"456\""
        );
    }

    @Test
    public void testCheckIncludesNullFails() {
        assertThrows(
            HeaderException.class,
            () -> this.check(
                Lists.of(
                    ETAG,
                    null
                )
            )
        );
    }

    @Test
    public void testCheckIncludesWrongTypeFails() {
        assertThrows(
            HeaderException.class,
            () -> this.check(
                Lists.of(
                    ETAG,
                    "WRONG!"
                )
            )
        );
    }


    @Override
    ETagListHeaderHandler handler() {
        return ETagListHeaderHandler.INSTANCE;
    }

    @Override
    HttpHeaderName<ETagList> name() {
        return HttpHeaderName.IF_MATCH;
    }

    @Override
    String invalidHeader() {
        return "I/";
    }

    @Override
    ETagList value() {
        return ETag.parseList("\"1\",\"2\"");
    }

    @Override
    String valueType() {
        return this.listValueType(ETag.class);
    }

    @Override
    String handlerToString() {
        return "ETagList";
    }

    @Override
    public Class<ETagListHeaderHandler> type() {
        return ETagListHeaderHandler.class;
    }
}
