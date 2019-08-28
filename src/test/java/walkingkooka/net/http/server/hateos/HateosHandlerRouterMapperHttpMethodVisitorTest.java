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

package walkingkooka.net.http.server.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.net.http.HttpMethodVisitorTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.type.JavaVisibility;

import java.math.BigInteger;
import java.util.function.Function;

public final class HateosHandlerRouterMapperHttpMethodVisitorTest implements HttpMethodVisitorTesting<HateosHandlerRouterMapperHttpMethodVisitor>,
        ToStringTesting<HateosHandlerRouterMapperHttpMethodVisitor> {

    @Test
    public void testToString() {
        final Function<String, BigInteger> parser = BigInteger::new;

        final HateosHandlerRouterMapper<?, ?, ?> mapper = HateosHandlerRouterMapper.with(parser,
                TestHateosResource.class,
                TestHateosResource2.class);
        this.toStringAndCheck(new HateosHandlerRouterMapperHttpMethodVisitor<>(mapper), mapper.toString());
    }

    @Override
    public HateosHandlerRouterMapperHttpMethodVisitor createVisitor() {
        return new HateosHandlerRouterMapperHttpMethodVisitor<>(null);
    }

    // TypeNameTesting...................................................................................................

    @Override
    public String typeNamePrefix() {
        return HateosHandlerRouterMapper.class.getSimpleName();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<HateosHandlerRouterMapperHttpMethodVisitor> type() {
        return HateosHandlerRouterMapperHttpMethodVisitor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
