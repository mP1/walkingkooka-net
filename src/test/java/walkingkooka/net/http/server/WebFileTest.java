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
import walkingkooka.net.header.CharsetName;
import walkingkooka.net.header.MediaType;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

public final class WebFileTest implements ClassTesting2<WebFile> {

    @Test
    public void testContentText() {
        final String text = "abc123def456";
        this.checkEquals(text, new FakeWebFile() {

            @Override
            public MediaType contentType() throws WebFileException {
                return MediaType.TEXT_PLAIN.setCharset(CharsetName.UTF_8);
            }

            @Override
            public InputStream content() throws WebFileException {
                return new ByteArrayInputStream(text.getBytes(Charset.forName("UTF-8")));
            }
        }.contentText(Charset.forName("UTF-16")));
    }

    @Override
    public Class<WebFile> type() {
        return WebFile.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
