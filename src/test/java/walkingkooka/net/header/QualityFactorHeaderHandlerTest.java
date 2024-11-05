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

public final class QualityFactorHeaderHandlerTest extends
        NonStringHeaderHandlerTestCase<QualityFactorHeaderHandler, Float> {

    @Override
    public String typeNamePrefix() {
        return "QualityFactor";
    }

    @Test
    public void testParseEmptyStringFails() {
        this.parseStringFails(
                "",
                new HeaderException("Empty \"text\"")
        );
    }

    @Test
    public void testParseInvalidNumberFails() {
        this.parseStringFails(
                "ABC",
                new HeaderException("Invalid number in \"ABC\"")
        );
    }

    @Test
    public void testParseNegativeFails() {
        this.parseStringFails(
                "-0.1",
                new HeaderException("Q factor -0.1 must be between 0.0 and 1.0")
        );
    }

    @Test
    public void testMoreThanOneFails() {
        this.parseStringFails(
                "1.01",
                new HeaderException("Q factor 1.01 must be between 0.0 and 1.0")
        );
    }

    @Test
    public void testZero() {
        this.parseAndToTextAndCheck("0.0", 0f);
    }

    @Test
    public void testHalf() {
        this.parseAndToTextAndCheck("0.5", 0.5f);
    }

    @Test
    public void testOne() {
        this.parseAndToTextAndCheck("1.0", 1.0f);
    }

    @Override
    QualityFactorHeaderHandler handler() {
        return QualityFactorHeaderHandler.INSTANCE;
    }

    @Override
    MediaTypeParameterName<?> name() {
        return MediaTypeParameterName.Q;
    }

    @Override
    String invalidHeader() {
        return "abc";
    }

    @Override
    Float value() {
        return 0.25f;
    }

    @Override
    String valueType() {
        return this.valueType(Float.class);
    }

    @Override
    String handlerToString() {
        return "QualityFactor";
    }

    @Override
    public Class<QualityFactorHeaderHandler> type() {
        return QualityFactorHeaderHandler.class;
    }
}
