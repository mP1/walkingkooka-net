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

public final class LanguageNameHeaderHandlerTest extends
    NonStringHeaderHandlerTestCase<LanguageNameHeaderHandler, LanguageName> {

    @Override
    public String typeNamePrefix() {
        return LanguageName.class.getSimpleName();
    }

    @Test
    public void testRoundtrip() {
        this.parseAndToTextAndCheck("en", LanguageName.with("en"));
    }

    @Override
    LanguageNameHeaderHandler handler() {
        return LanguageNameHeaderHandler.INSTANCE;
    }

    @Override
    LinkParameterName<LanguageName> name() {
        return LinkParameterName.HREFLANG;
    }

    @Override
    String invalidHeader() {
        return "invalid!!!";
    }

    @Override
    LanguageName value() {
        return LanguageName.with("en");
    }

    @Override
    String valueType() {
        return this.valueType(LanguageName.class);
    }

    @Override
    String handlerToString() {
        return LanguageName.class.getSimpleName();
    }

    @Override
    public Class<LanguageNameHeaderHandler> type() {
        return LanguageNameHeaderHandler.class;
    }
}
