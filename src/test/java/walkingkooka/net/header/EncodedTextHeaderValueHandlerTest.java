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

public final class EncodedTextHeaderValueHandlerTest extends
        NonStringHeaderValueHandlerTestCase<EncodedTextHeaderValueHandler, EncodedText> {

    @Override
    public String typeNamePrefix() {
        return EncodedText.class.getSimpleName();
    }

    @Test
    public void testFilename() {
        final String filename = this.value().toHeaderText();
        this.parseAndToTextAndCheck(filename, this.value());
    }

    @Override
    EncodedTextHeaderValueHandler handler() {
        return EncodedTextHeaderValueHandler.INSTANCE;
    }

    @Override
    ContentDispositionParameterName name() {
        return ContentDispositionParameterName.FILENAME;
    }

    @Override
    String invalidHeaderValue() {
        return "utf-8'";
    }

    @Override
    EncodedText value() {
        return EncodedText.with(CharsetName.UTF_8, EncodedText.NO_LANGUAGE, "abc 123");
    }

    @Override
    String valueType() {
        return this.valueType(EncodedText.class);
    }

    @Override
    String handlerToString() {
        return EncodedText.class.getSimpleName();
    }

    @Override
    public Class<EncodedTextHeaderValueHandler> type() {
        return EncodedTextHeaderValueHandler.class;
    }
}
