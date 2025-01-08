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
import walkingkooka.Cast;
import walkingkooka.InvalidCharacterException;
import walkingkooka.collect.map.Maps;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

final public class MediaTypeParameterNameTest extends HeaderParameterNameTestCase<MediaTypeParameterName<?>,
    MediaTypeParameterName<?>> {

    @Test
    public void testWithIncludesWhitespaceFails() {
        assertThrows(InvalidCharacterException.class, () -> MediaTypeParameterName.with("paramet er"));
    }

    @Test
    public void testWithIncludesEqualSignFails() {
        assertThrows(InvalidCharacterException.class, () -> MediaTypeParameterName.with("parameter=value"));
    }

    @Test
    public void testWithIncludesSemiColonFails() {
        assertThrows(InvalidCharacterException.class, () -> MediaTypeParameterName.with("parameter=value;header2"));
    }

    @Test
    public void testWith() {
        this.createNameAndCheck("abc123");
    }

    @Test
    public void testConstantNameReturnsConstant() {
        assertSame(MediaTypeParameterName.Q,
            MediaTypeParameterName.with(MediaTypeParameterName.Q.value()));
    }

    @Test
    public void testConstantNameCaseInsensitiveReturnsConstant() {
        final String differentCase = MediaTypeParameterName.Q.value().toUpperCase();
        this.checkNotEquals(differentCase, MediaTypeParameterName.Q.value());
        assertSame(MediaTypeParameterName.Q, MediaTypeParameterName.with(differentCase));
    }

    // parameter value......................................................................................

    @Test
    public void testParameterValueAbsent() {
        this.parameterValueAndCheckAbsent(MediaTypeParameterName.Q,
            this.mediaType());
    }

    @Test
    public void testParameterValuePresent() {
        final MediaTypeParameterName<Float> parameter = MediaTypeParameterName.Q;
        final Float value = 0.75f;

        this.parameterValueAndCheckPresent(parameter,
            this.mediaType().setParameters(Maps.of(parameter, value)),
            value);
    }

    @Test
    public void testParameterValueCharsetNamePresent() {
        final MediaTypeParameterName<CharsetName> parameter = MediaTypeParameterName.CHARSET;
        final CharsetName charsetName = CharsetName.UTF_8;

        this.parameterValueAndCheckPresent(parameter,
            this.mediaType().setParameters(Maps.of(parameter, charsetName)),
            charsetName);
    }

    private MediaType mediaType() {
        return MediaType.with("type", "subType");
    }

    @Override
    public MediaTypeParameterName<Object> createName(final String name) {
        return Cast.to(MediaTypeParameterName.with(name));
    }

    @Override
    public Class<MediaTypeParameterName<?>> type() {
        return Cast.to(MediaTypeParameterName.class);
    }
}
