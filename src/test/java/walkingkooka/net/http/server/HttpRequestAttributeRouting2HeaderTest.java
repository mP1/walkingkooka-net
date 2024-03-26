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

package walkingkooka.net.http.server;

import org.junit.jupiter.api.Test;
import walkingkooka.net.header.Header;
import walkingkooka.net.header.MediaType;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HttpRequestAttributeRouting2HeaderTest extends HttpRequestAttributeRouting2TestCase<HttpRequestAttributeRouting2Header, Header> {

    @Test
    public void testWithNullHeaderFails() {
        assertThrows(NullPointerException.class, () -> HttpRequestAttributeRouting2Header.with(null));
    }

    @Test
    public void testTestNullFails() {
        this.testFalse(null);
    }

    @Test
    public void testValueMissing() {
        this.testFalse(null);
    }

    @Test
    public void testEqualsDifferentHeader() {
        this.testFalse(MediaType.BINARY);
    }

    @Test
    public void testEqualsDifferentParameters() {
        this.testFalse(MediaType.parse("major/minor;z=99"));
    }

    @Test
    public void testEqualsDifferentParameters2() {
        this.testFalse(MediaType.parse("major/minor;a=1;z=99"));
    }

    @Test
    public void testMissingParameters() {
        this.testFalse(MediaType.parse("major/minor;a=1;"));
    }

    @Test
    public void testExtraParameters() {
        this.testTrue(MediaType.parse("major/minor;a=1;b=2;c=3"));
    }

    @Test
    public void testExact() {
        this.testTrue(this.requiredHeader());
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createPredicate(), this.requiredHeader().toString());
    }

    // PredicateTesting.................................................................................................

    @Override
    public HttpRequestAttributeRouting2Header createPredicate() {
        return HttpRequestAttributeRouting2Header.with(this.requiredHeader());
    }

    private Header requiredHeader() {
        return MediaType.parse("major/minor;a=1;b=2");
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<HttpRequestAttributeRouting2Header> type() {
        return HttpRequestAttributeRouting2Header.class;
    }
}
