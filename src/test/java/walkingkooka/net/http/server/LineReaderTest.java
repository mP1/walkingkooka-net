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
import walkingkooka.net.http.HasHeaders;
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
