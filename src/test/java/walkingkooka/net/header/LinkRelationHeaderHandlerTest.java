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

import java.util.List;

public final class LinkRelationHeaderHandlerTest extends
    NonStringHeaderHandlerTestCase<LinkRelationHeaderHandler, List<LinkRelation<?>>> {

    @Override
    public String typeNamePrefix() {
        return LinkRelation.class.getSimpleName();
    }

    // http://www.rfc-editor.org/rfc/rfc5988.txt

    // </TheBook/chapter2>;
    @Test
    public void testParseLinkRegular() {
        this.parseAndToTextAndCheck2("abc123",
            LinkRelation.with("abc123"));
    }

    @Test
    public void testParseLinkUrl() {
        this.parseAndToTextAndCheck2("https://example.com",
            LinkRelation.with("https://example.com"));
    }

    @Test
    public void testParseLinkMultiple() {
        this.parseAndToTextAndCheck2("abc123 https://example.com",
            LinkRelation.with("abc123"),
            LinkRelation.with("https://example.com"));
    }

    private void parseAndToTextAndCheck2(final String text,
                                         final LinkRelation<?>... relations) {
        this.parseAndToTextAndCheck(text, Lists.of(relations));
    }

    @Override
    LinkRelationHeaderHandler handler() {
        return LinkRelationHeaderHandler.INSTANCE;
    }

    @Override
    LinkParameterName<List<LinkRelation<?>>> name() {
        return LinkParameterName.REL;
    }

    @Override
    String invalidHeader() {
        return "\r";
    }

    @Override
    List<LinkRelation<?>> value() {
        return Lists.of(LinkRelation.with("abc123"), LinkRelation.with("https://example.com"));
    }

    @Override
    String valueType() {
        return this.listValueType(LinkRelation.class);
    }

    @Override
    String handlerToString() {
        return "List<LinkRelation>";
    }

    @Override
    public Class<LinkRelationHeaderHandler> type() {
        return LinkRelationHeaderHandler.class;
    }
}
