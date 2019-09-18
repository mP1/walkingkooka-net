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
import walkingkooka.test.ParseStringTesting;
import walkingkooka.type.JavaVisibility;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class AcceptTest extends HeaderValue2TestCase<Accept, List<MediaType>>
        implements ParseStringTesting<Accept> {

    @Test
    public void testWithNullFails() {
        assertThrows(NullPointerException.class, () -> {
            Accept.with(null);
        });
    }

    @Test
    public void testWithEmptyMediaTypesFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            Accept.with(Lists.empty());
        });
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createHeaderValue(), "image/bmp, image/gif; q=0.5");
    }

    // helpers..........................................................................................................

    @Override
    Accept createHeaderValue(final List<MediaType> value) {
        return Accept.with(value);
    }

    @Override
    List<MediaType> value() {
        return Lists.of(MediaType.IMAGE_BMP, MediaType.IMAGE_GIF.setParameters(Maps.of(MediaTypeParameterName.Q, 0.5f)));
    }

    @Override
    List<MediaType> differentValue() {
        return Lists.of(MediaType.TEXT_PLAIN);
    }

    @Override
    public boolean isMultipart() {
        return false;
    }

    @Override
    public boolean isRequest() {
        return true;
    }

    @Override
    public boolean isResponse() {
        return false;
    }

    // ParseStringTesting ..............................................................................................

    @Override
    public Accept parseString(final String text) {
        return Accept.parse(text);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<Accept> type() {
        return Accept.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
