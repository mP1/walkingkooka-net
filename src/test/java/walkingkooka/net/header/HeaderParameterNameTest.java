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
import walkingkooka.Cast;
import walkingkooka.naming.Name;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.TypeNameTesting;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HeaderParameterNameTest implements ClassTesting2<HeaderParameterName<?>>,
    TypeNameTesting<HeaderParameterName<?>> {

    // parameterValueOrFail.............................................................................................

    @Test
    public void testParameterValueOrFailWithUnknownParameterFails() {
        final HeaderException thrown = assertThrows(
            HeaderException.class,
            () -> AcceptCharsetValueParameterName.with("Hello")
                .parameterValueOrFail(
                    new HeaderWithParameters<HeaderParameterName<?>>() {
                        @Override
                        public Map<HeaderParameterName<?>, Object> parameters() {
                            return Map.of();
                        }

                        @Override
                        public HeaderWithParameters<HeaderParameterName<?>> setParameters(final Map<HeaderParameterName<?>, Object> parameters) {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public String toHeaderText() {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public boolean isWildcard() {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public boolean isMultipart() {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public boolean isRequest() {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public boolean isResponse() {
                            throw new UnsupportedOperationException();
                        }
                    }
                )
        );

        this.checkEquals(
            "Missing header parameter Hello",
            thrown.getMessage()
        );
    }

    // class............................................................................................................

    @Test
    @Override
    public void testAllMethodsVisibility() {
        this.allMethodsVisibilityCheck("parameterValueOrFail");
    }

    @Override
    public Class<HeaderParameterName<?>> type() {
        return Cast.to(HeaderParameterName.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    // TypeNameTesting .................................................................................................

    @Override
    public String typeNamePrefix() {
        return this.subtractTypeNameSuffix();
    }

    @Override
    public String typeNameSuffix() {
        return Name.class.getSimpleName();
    }
}
