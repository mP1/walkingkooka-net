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
import walkingkooka.collect.map.Maps;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class AcceptHeaderValueHandlerTest extends
        NonStringHeaderValueHandlerTestCase<AcceptHeaderValueHandler, Accept> {

    private final static String TEXT = "type1/subType1; p1=v1, type2/subType2";

    @Override
    public String typeNamePrefix() {
        return MediaType.class.getSimpleName();
    }

    @Test
    public void testParseMultipleMediaTypes() {
        this.parseAndToTextAndCheck(TEXT,
                Accept.with(Lists.of(this.mediaType1(), this.mediaType2())));
    }

    @Test
    public void testCheckIncludesNullFails() {
        assertThrows(HeaderValueException.class, () -> {
            this.check(Lists.of(this.mediaType1(), null));
        });
    }

    @Test
    public void testCheckIncludesWrongTypeFails() {
        assertThrows(HeaderValueException.class, () -> {
            this.check(Lists.of(this.mediaType1(), "WRONG!"));
        });
    }

    private MediaType mediaType1() {
        return MediaType.with("type1", "subType1").setParameters(Maps.of(MediaTypeParameterName.with("p1"), "v1"));
    }

    private MediaType mediaType2() {
        return MediaType.with("type2", "subType2");
    }

    @Override
    AcceptHeaderValueHandler handler() {
        return AcceptHeaderValueHandler.INSTANCE;
    }

    @Override
    HttpHeaderName<Accept> name() {
        return HttpHeaderName.ACCEPT;
    }

    @Override
    String invalidHeaderValue() {
        return "invalid!!!";
    }

    @Override
    Accept value() {
        return Accept.with(MediaType.parseList("type1/sub1;p1=v1,type2/sub2;p2=v2"));
    }

    @Override
    String valueType() {
        return this.valueType(Accept.class);
    }

    @Override
    String handlerToString() {
        return Accept.class.getSimpleName();
    }

    @Override
    public Class<AcceptHeaderValueHandler> type() {
        return AcceptHeaderValueHandler.class;
    }
}
