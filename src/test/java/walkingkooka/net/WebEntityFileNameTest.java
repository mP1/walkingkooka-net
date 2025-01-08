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

package walkingkooka.net;

import org.junit.jupiter.api.Test;
import walkingkooka.InvalidCharacterException;
import walkingkooka.naming.NameTesting2;
import walkingkooka.text.CaseSensitivity;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class WebEntityFileNameTest implements NameTesting2<WebEntityFileName, WebEntityFileName> {

    @Test
    public void testWithForwardSlashFails() {
        assertThrows(
            InvalidCharacterException.class,
            () -> WebEntityFileName.with("path/filename")
        );
    }

    @Test
    public void testWithBackSlashFails() {
        assertThrows(
            InvalidCharacterException.class,
            () -> WebEntityFileName.with("path\\filename")
        );
    }

    @Override
    public int minLength() {
        return WebEntityFileName.MIN_LENGTH;
    }

    @Override
    public int maxLength() {
        return WebEntityFileName.MAX_LENGTH;
    }

    @Override
    public String possibleValidChars(final int i) {
        if (null == this.valid) {
            this.valid = IntStream.rangeClosed(0, Character.MAX_VALUE)
                .filter(c -> WebEntityFileName.FILENAME.test((char) c))
                .mapToObj(c -> Character.valueOf((char) c).toString())
                .collect(Collectors.joining(""));
        }
        return this.valid;
    }

    String valid;

    @Override
    public String possibleInvalidChars(int i) {
        return "/";
    }

    @Override
    public WebEntityFileName createName(final String name) {
        return WebEntityFileName.with(name);
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return WebEntityFileName.CASE_SENSITIVITY;
    }

    @Override
    public String nameText() {
        return "file1.txt";
    }

    @Override
    public String differentNameText() {
        return "different-file-2.txt";
    }

    @Override
    public String nameTextLess() {
        return "before.txt";
    }

    // class............................................................................................................

    @Override
    public Class<WebEntityFileName> type() {
        return WebEntityFileName.class;
    }
}
