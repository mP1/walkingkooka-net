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
import walkingkooka.collect.list.Lists;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public final class HttpEntityStackTraceTest extends HttpEntityStackTraceTestCase<HttpEntityStackTrace> {

    @Test
    public void testDump() {
        final Throwable thrown = new Throwable("hello");
        final HttpEntity entity = this.dumpStackTrace(thrown);
        final Map<HttpHeaderName<?>, List<?>> headers = entity.headers();
        assertEquals(Lists.of(MediaType.TEXT_PLAIN), headers.get(HttpHeaderName.CONTENT_TYPE), () -> "content-type\n" + entity);
        assertNotEquals(null, headers.get(HttpHeaderName.CONTENT_LENGTH), () -> "content-length\n" + entity);
    }

    @Override
    HttpEntity dumpStackTrace(final Throwable cause) {
        return HttpEntityStackTrace.dumpStackTrace(cause);
    }

    @Override
    public Class<HttpEntityStackTrace> type() {
        return HttpEntityStackTrace.class;
    }
}
