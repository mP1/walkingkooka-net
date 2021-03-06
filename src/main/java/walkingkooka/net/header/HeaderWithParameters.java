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

import java.util.Map;

/**
 * Defines a method to retrieve the parameters from a header.
 */
public interface HeaderWithParameters<P extends HeaderParameterName<?>> extends Header {

    /**
     * A read only map view of all parameters.
     */
    Map<P, Object> parameters();

    /**
     * Would be setter that returns a {@link HeaderWithParameters} creating a new instance if the parameters
     * are different.
     */
    HeaderWithParameters<P> setParameters(final Map<P, Object> parameters);
}
