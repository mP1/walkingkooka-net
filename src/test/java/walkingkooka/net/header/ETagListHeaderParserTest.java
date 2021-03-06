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
import walkingkooka.text.CharSequences;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class ETagListHeaderParserTest extends ETagHeaderParserTestCase<ETagListHeaderParser> {

    @Test
    public void testValueSeparatorFails() {
        this.parseStringMissingValueFails(",");
    }

    @Test
    public final void testSeparatorFails() {
        this.parseStringMissingValueFails("\"ABC\",");
    }

    @Test
    public final void testSeparatorSpaceFails() {
        this.parseStringMissingValueFails("\"ABC\", ");
    }

    @Test
    public final void testSeparatorTabFails() {
        this.parseStringMissingValueFails("\"ABC\",\t");
    }

    @Test
    public final void testWeakSeparatorSpaceFails() {
        this.parseStringMissingValueFails("W/\"ABC\", ");
    }

    @Test
    public final void testWeakSeparatorTabFails() {
        this.parseStringMissingValueFails("W/\"ABC\",\t");
    }

    @Test
    public void testETagSeparatorETag() {
        this.parseStringAndCheck2("\"A\",\"B\"",
                ETag.with("A", ETagValidator.STRONG),
                ETag.with("B", ETagValidator.STRONG));
    }

    @Test
    public void testETagSeparatorSpaceETag() {
        this.parseStringAndCheck2("\"A\", \"B\"",
                ETag.with("A", ETagValidator.STRONG),
                ETag.with("B", ETagValidator.STRONG));
    }

    @Test
    public void testETagSeparatorTabETag() {
        this.parseStringAndCheck2("\"A\",\t\"B\"",
                ETag.with("A", ETagValidator.STRONG),
                ETag.with("B", ETagValidator.STRONG));
    }

    @Test
    public void testETagSeparatorSpaceSpaceETag() {
        this.parseStringAndCheck2("\"A\",  \"B\"",
                ETag.with("A", ETagValidator.STRONG),
                ETag.with("B", ETagValidator.STRONG));
    }

    @Test
    public void testWeakETagSeparatorWeakETag() {
        this.parseStringAndCheck2("W/\"A\",W/\"B\"",
                ETag.with("A", ETagValidator.WEAK),
                ETag.with("B", ETagValidator.WEAK));
    }

    @Test
    public void testWeakETagSeparatorSpaceWeakETag() {
        this.parseStringAndCheck2("\"A\", W/\"B\"",
                ETag.with("A", ETagValidator.STRONG),
                ETag.with("B", ETagValidator.WEAK));
    }

    @Test
    public void testWeakETagSeparatorTabWeakETag() {
        this.parseStringAndCheck2("\"A\",\tW/\"B\"",
                ETag.with("A", ETagValidator.STRONG),
                ETag.with("B", ETagValidator.WEAK));
    }

    final void parseStringAndCheck2(final String text, final ETag... tags) {
        assertEquals(Lists.of(tags),
                listReadOnlyCheck(ETagListHeaderParser.parseList(text)),
                "Incorrect result parsing " + CharSequences.quote(text));
    }

    @Override
    public ETag parseString(final String text) {
        final List<ETag> tags = ETagListHeaderParser.parseList(text);
        assertEquals(1, tags.size(), "expected one tag =" + CharSequences.quote(text) + "=" + tags);
        return tags.get(0);
    }

    @Override
    public Class<ETagListHeaderParser> type() {
        return ETagListHeaderParser.class;
    }
}
