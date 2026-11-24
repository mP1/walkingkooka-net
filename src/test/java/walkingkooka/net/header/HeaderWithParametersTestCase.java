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

import java.util.Map;

public abstract class HeaderWithParametersTestCase<V extends HeaderWithParameters<N>,
    N extends HeaderParameterName<?>> extends HeaderTestCase<V>
    implements HeaderWithParametersTesting2<V, N> {

    HeaderWithParametersTestCase() {
        super();
    }

    @Test
    public final void testParametersReadonly() {
        this.parametersReadOnlyCheck(
            this.parameterName()
        );
    }

    /**
     * This parameter name should be unique and not used within other tests.
     * It is used to put a new parameter upon the parameter {@link Map} to verify it is readonly.
     */
    abstract N parameterName();
}
