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

package walkingkooka.net.http;


import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

final public class HttpProtocolVersionTest implements ClassTesting2<HttpProtocolVersion>,
    ToStringTesting<HttpProtocolVersion> {

    @Test
    public void testOneZero() {
        this.checkEquals("HTTP/1.0", HttpProtocolVersion.VERSION_1_0.value());
    }

    @Test
    public void testOneOne() {
        this.checkEquals("HTTP/1.1", HttpProtocolVersion.VERSION_1_1.value());
    }

    @Test
    public void testTwo() {
        this.checkEquals("HTTP/2", HttpProtocolVersion.VERSION_2.value());
    }

    @Test
    public void testFromNullVersionFails() {
        assertThrows(NullPointerException.class, () -> HttpProtocolVersion.with(null));
    }

    @Test
    public void testFromOneZero() {
        assertSame(HttpProtocolVersion.VERSION_1_0, HttpProtocolVersion.with("HTTP/1.0"));
    }

    @Test
    public void testFromOneOne() {
        assertSame(HttpProtocolVersion.VERSION_1_1, HttpProtocolVersion.with("HTTP/1.1"));
    }

    @Test
    public void testFromTwo() {
        assertSame(HttpProtocolVersion.VERSION_2, HttpProtocolVersion.with("HTTP/2"));
    }

    @Test
    public void testFromUnknownFails() {
        assertThrows(IllegalArgumentException.class, () -> HttpProtocolVersion.with("unknown/???"));
    }

    @Test
    public void testToStringVersion10() {
        this.toStringAndCheck(HttpProtocolVersion.VERSION_1_0, "HTTP/1.0");
    }

    @Test
    public void testToStringVersion11() {
        this.toStringAndCheck(HttpProtocolVersion.VERSION_1_1, "HTTP/1.1");
    }

    @Test
    public void testToStringVersion2() {
        this.toStringAndCheck(HttpProtocolVersion.VERSION_2, "HTTP/2");
    }

    @Override
    public Class<HttpProtocolVersion> type() {
        return HttpProtocolVersion.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
