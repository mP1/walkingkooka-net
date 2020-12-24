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
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.header.HeaderException;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HasHeaders;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class LineReaderTest implements ClassTesting2<LineReader>, ToStringTesting<LineReader> {

    @Test
    public void testCrMissingNl() {
        assertThrows(IllegalArgumentException.class, () -> {
            LineReader.with("cr missing nl\r").readLine();
        });
    }

    @Test
    public void testCrMissingNl2() {
        assertThrows(IllegalArgumentException.class, () -> {
            LineReader.with("cr missing nl\r1\n").readLine();
        });
    }

    @Test
    public void testOneLine() {
        this.readAndCheck("GET /url HTTP/1.1");
    }

    @Test
    public void testTwoLines() {
        this.readAndCheck("GET /url HTTP/1.1", "Content-Length: 1");
    }

    @Test
    public void testIncludesEmptyLine() {
        this.readAndCheck("POST /url HTTP/1.1", "Content-Length: 1", "", "BODY123");
    }

    private void readAndCheck(final String... lines) {
        final LineReader reader = LineReader.with(Arrays.stream(lines).collect(Collectors.joining(HasHeaders.LINE_ENDING.toString())));
        final List<String> read = Lists.array();

        for (; ; ) {
            final String line = reader.readLine();
            if (null == line) {
                final String leftOver = reader.leftOver();
                if (null != leftOver) {
                    read.add(leftOver);
                }
                break;
            }
            read.add(line);
        }

        assertEquals(Lists.of(lines), read);
    }

    // readHeaders......................................................................................................

    @Test
    public void testInvalidHeaderFails() {
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> LineReader.with("Invalid-Header").readHeaders());
        assertEquals("Header missing separator/value=\"Invalid-Header\"", thrown.getMessage());
    }

    @Test
    public void testInvalidHeaderLineEndingFails() {
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> LineReader.with("Content-Length:1\r" +
                "Invalid-Header2").readHeaders());
        assertEquals("Invalid line ending", thrown.getMessage());
    }

    @Test
    public void testInvalidHeaderFails2() {
        final HeaderException thrown = assertThrows(HeaderException.class, () -> LineReader.with("Content-Length:A").readHeaders());
        assertEquals("Failed to convert \"Content-Length\" value \"A\", message: For input string: \"A\"", thrown.getMessage());
    }

    @Test
    public void testReadHeaders() {
        assertEquals(HttpEntity.EMPTY.addHeader(HttpHeaderName.CONTENT_TYPE, MediaType.TEXT_PLAIN).addHeader(HttpHeaderName.CONTENT_LENGTH, 123L),
                LineReader.with("Content-Type: text/plain\r\nContent-Length: 123\r\n").readHeaders());
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        final String left = "1a\r\n2b\r\n3c";
        this.toStringAndCheck(LineReader.with(left), left);
    }

    @Test
    public void testToStringAfterReadLine() {
        final LineReader reader = LineReader.with("1a\r\n2b\r\n3c");
        reader.readLine();

        this.toStringAndCheck(reader, "2b\r\n3c");
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<LineReader> type() {
        return LineReader.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
