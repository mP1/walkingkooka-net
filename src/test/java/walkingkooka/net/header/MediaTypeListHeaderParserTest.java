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
import java.util.Map;

public final class MediaTypeListHeaderParserTest extends MediaTypeHeaderParserTestCase<MediaTypeListHeaderParser,
        List<MediaType>> {

    @Test
    public void testParameterSeparatorFails() {
        this.parseStringMissingValueFails(";", 0);
    }

    @Test
    public void testTypeSlashSubTypeValueSeparatorSeparatorFails() {
        this.parseStringMissingValueFails("type/subtype,");
    }

    @Test
    public void testTypeSlashSubTypeValueSeparatorWhitespaceFails() {
        this.parseStringMissingValueFails("type/subtype, ");
    }

    @Test
    public void testTypeSlashSubParametersValueSeparatorFails() {
        this.parseStringMissingValueFails("type/subtype;parameter123=value456,");
    }

    @Test
    public void testTypeSubTypeWhitespace() {
        this.parseStringAndCheck2("type1/subtype1 ",
                MediaType.with("type1", "subtype1"));
    }

    @Test
    public void testTypeSubTypeCommaWhitespaceTypeSubType() {
        this.parseStringAndCheck2("type1/subtype1, type2/subtype2",
                MediaType.with("type1", "subtype1"),
                MediaType.with("type2", "subtype2"));
    }

    @Test
    public void testTypeSubTypeParameterCommaWhitespaceTypeSubType2() {
        this.parseStringAndCheck2("type1/subtype1;p1=v1, type2/subtype2",
                MediaType.with("type1", "subtype1").setParameters(this.parameters("p1", "v1")),
                MediaType.with("type2", "subtype2"));
    }

    @Test
    public void testTypeSubTypeParameterCommaTypeSubTypeParameter() {
        this.parseStringAndCheck2("type1/subtype1;p1=v1,type2/subtype2;p2=v2",
                MediaType.with("type1", "subtype1").setParameters(this.parameters("p1", "v1")),
                MediaType.with("type2", "subtype2").setParameters(this.parameters("p2", "v2")));
    }

    @Test
    public void testTypeSubTypeParameterCommaWhitespaceTypeSubTypeParameter() {
        this.parseStringAndCheck2("type1/subtype1;p1=v1, type2/subtype2;p2=v2",
                MediaType.with("type1", "subtype1").setParameters(this.parameters("p1", "v1")),
                MediaType.with("type2", "subtype2").setParameters(this.parameters("p2", "v2")));
    }

    @Test
    public void testTypeSubTypeWhitespaceParameterCommaWhitespaceTypeSubTypeWhitespaceParameter() {
        this.parseStringAndCheck2("type1/subtype1; p1=v1, type2/subtype2; p2=v2",
                MediaType.with("type1", "subtype1").setParameters(this.parameters("p1", "v1")),
                MediaType.with("type2", "subtype2").setParameters(this.parameters("p2", "v2")));
    }

    @Test
    public void testSortedByQFactor() {
        this.parseStringAndCheck2("type1/subtype1; q=0.25, type2/subtype2; q=1.0, type3/subtype3; q=0.5",
                MediaType.with("type2", "subtype2").setParameters(this.parameters("q", 1.0f)),
                MediaType.with("type3", "subtype3").setParameters(this.parameters("q", 0.5f)),
                MediaType.with("type1", "subtype1").setParameters(this.parameters("q", 0.25f)));
    }

    @Override void parseStringAndCheck(final String text,
                                       final String type,
                                       final String subtype,
                                       final Map<MediaTypeParameterName<?>, Object> parameters) {
        parseStringAndCheckOne(text, type, subtype, parameters);
        parseStringAndCheckRepeated(text, type, subtype, parameters);
        parseStringAndCheckSeveral(text, type, subtype, parameters);
    }

    private void parseStringAndCheckOne(final String text,
                                        final String type,
                                        final String subtype,
                                        final Map<MediaTypeParameterName<?>, Object> parameters) {
        final List<MediaType> result = MediaTypeListHeaderParser.parseMediaTypeList(text);
        this.checkEquals(1, result.size(), "parse " + CharSequences.quote(text) + " got " + result);
        this.check(result.get(0), type, subtype, parameters);
    }

    private void parseStringAndCheckRepeated(final String text,
                                       final String type,
                                       final String subtype,
                                       final Map<MediaTypeParameterName<?>, Object> parameters) {
        final String parsed = text + MediaType.SEPARATOR + text;
        final List<MediaType> result = MediaTypeListHeaderParser.parseMediaTypeList(parsed);
        this.checkEquals(2, result.size(), "parse " + CharSequences.quote(parsed) + " got " + result);
        this.check(result.get(0), type, subtype, parameters);
        this.check(result.get(1), type, subtype, parameters);
    }

    private void parseStringAndCheckSeveral(final String text,
                                      final String type,
                                      final String subtype,
                                      final Map<MediaTypeParameterName<?>, Object> parameters) {
        final String parsed = "TYPE1/SUBTYPE1," + text + ",TYPE2/SUBTYPE2;x=y," + text;
        final List<MediaType> result = MediaTypeListHeaderParser.parseMediaTypeList(parsed);

        this.checkEquals(4, result.size(), "parse " + CharSequences.quote(parsed) + " got " + result);

        this.check(result.get(0), "TYPE1", "SUBTYPE1");
        this.check(result.get(1), type, subtype, parameters);

        this.check(result.get(2), "TYPE2", "SUBTYPE2", parameters("x", "y"));
        this.check(result.get(3), type, subtype, parameters);
    }

    private void parseStringAndCheck2(final String text, final MediaType... mediaTypes) {
        this.checkEquals(Lists.of(mediaTypes),
                MediaTypeListHeaderParser.parseMediaTypeList(text),
                "Incorrect result parsing " + CharSequences.quote(text));
    }

    @Override
    public List<MediaType> parseString(final String text) {
        return listReadOnlyCheck(MediaTypeListHeaderParser.parseMediaTypeList(text));
    }

    @Override
    public Class<MediaTypeListHeaderParser> type() {
        return MediaTypeListHeaderParser.class;
    }
}
