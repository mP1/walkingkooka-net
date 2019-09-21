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
import org.junit.jupiter.api.io.TempDir;
import org.opentest4j.AssertionFailedError;
import walkingkooka.Binary;
import walkingkooka.net.header.ETag;
import walkingkooka.net.header.ETagValidator;
import walkingkooka.net.header.MediaType;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.ToStringTesting;
import walkingkooka.type.JavaVisibility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class FileSystemWebFileTest implements ClassTesting2<FileSystemWebFile>,
        ToStringTesting<FileSystemWebFile> {

    private final static String FILENAME = "file.custom.bin";
    private final static Binary CONTENT = Binary.with("abc123".getBytes(Charset.defaultCharset()));

    @TempDir
    public Path tempDirectory;

    // With.............................................................................................................

    @Test
    public void testWithNullPathFails() {
        this.withFails(null, this::contentTypeGuesser, this::etagComputer);
    }

    @Test
    public void testWithNullContentTypeGuesserFails() {
        this.withFails(this.path(), null, this::etagComputer);
    }

    @Test
    public void testWithNullETagComputerFails() {
        this.withFails(this.path(), this::contentTypeGuesser, null);
    }

    private void withFails(final Path path,
                           final BiFunction<String, Binary, MediaType> contentTypeGuesser,
                           final Function<Binary, Optional<ETag>> etagComputer) {
        assertThrows(NullPointerException.class, () -> {
            FileSystemWebFile.with(path, contentTypeGuesser, etagComputer);
        });
    }

    // WebFile..........................................................................................................

    @Test
    public void testLastModified() throws Exception {
        final FileSystemWebFile webFile = this.webFile();
        assertEquals(LocalDateTime.ofInstant(Files.getLastModifiedTime(webFile.path).toInstant(), ZoneId.systemDefault()),
                webFile.lastModified(),
                "lastModified");
    }

    @Test
    public void testContentType() {
        final FileSystemWebFile webFile = this.webFile();
        assertEquals(this.contentType(),
                webFile.contentType(),
                "contentType");
    }

    @Test
    public void testContentSize() {
        final FileSystemWebFile webFile = this.webFile();
        assertEquals(Long.valueOf(CONTENT.size()),
                webFile.contentSize(),
                "contentSize");
    }

    @Test
    public void testContent() throws IOException {
        final FileSystemWebFile webFile = this.webFile();

        try (final ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            try (final InputStream input = webFile.content()) {
                input.transferTo(output);
                output.flush();

                assertEquals(Long.valueOf(CONTENT.size()),
                        webFile.contentSize(),
                        "contentSize");
            }
        }
    }

    @Test
    public void testETag() {
        final FileSystemWebFile webFile = this.webFile();
        assertEquals(Optional.of(this.etag()),
                webFile.etag(),
                "etag");
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        final FileSystemWebFile webFile = this.webFile();
        this.toStringAndCheck(webFile, webFile.path.toString());
    }

    // helpers..........................................................................................................

    private FileSystemWebFile webFile() {
        return FileSystemWebFile.with(this.path(), this::contentTypeGuesser, this::etagComputer);
    }

    private Path path() {
        try {
            final Path file = Paths.get(this.tempDirectory.toString(), FILENAME);
            Files.write(file, CONTENT.value());
            return file;
        } catch (final IOException cause) {
            throw new AssertionFailedError(cause.getMessage());
        }
    }

    private MediaType contentTypeGuesser(final String filename,
                                         final Binary content) {
        return this.contentType();
    }

    private MediaType contentType() {
        return MediaType.parse("custom/file-type");
    }

    private Optional<ETag> etagComputer(final Binary binary) {
        return Optional.ofNullable(binary.equals(CONTENT) ?
                this.etag() :
                null);
    }

    private ETag etag() {
        return ETag.with("2222", ETagValidator.STRONG);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<FileSystemWebFile> type() {
        return FileSystemWebFile.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
