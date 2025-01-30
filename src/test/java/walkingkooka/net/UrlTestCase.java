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

import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.IsMethodTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.HasTextTesting;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;

import java.util.function.Predicate;

/**
 * Base class for testing a {@link Url} with mostly parameter checking tests.
 */
abstract public class UrlTestCase<U extends Url> implements ClassTesting2<U>,
    HashCodeEqualsDefinedTesting2<U>,
    JsonNodeMarshallingTesting<U>,
    IsMethodTesting<U>,
    ParseStringTesting<U>,
    HasTextTesting,
    ToStringTesting<U>,
    TypeNameTesting<U> {

    UrlTestCase() {
        super();
    }

    // HasText..........................................................................................................

    public final void testText() {
        final U url = this.createUrl();

        this.textAndCheck(
            url,
            url.toString()
        );
    }

    // factory

    abstract U createUrl();

    // ClassTesting.....................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // HashCodeEqualsDefinedTesting.....................................................................................

    @Override
    public final U createObject() {
        return this.createUrl();
    }

    // JsonNodeContextTesting...........................................................................................

    @Override
    public final U createJsonNodeMarshallingValue() {
        return this.createUrl();
    }

    // IsMethodTesting..................................................................................................

    @Override
    public final U createIsMethodObject() {
        return this.createUrl();
    }

    @Override
    public final Predicate<String> isMethodIgnoreMethodFilter() {
        return (m) -> m.equals("isBase64");
    }


    @Override
    public final String toIsMethodName(final String typeName) {
        return this.toIsMethodNameWithPrefixSuffix(
            typeName,
            "", // drop-prefix
            "Url" // drop-suffix
        );
    }

    // ParseStringTesting ..............................................................................................

    @Override
    public final RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        return expected;
    }

    @Override
    public final Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> expected) {
        return expected;
    }

    // TypeNameTesting .................................................................................................

    @Override
    public final String typeNamePrefix() {
        return "";
    }

    @Override
    public final String typeNameSuffix() {
        return Url.class.getSimpleName();
    }
}
