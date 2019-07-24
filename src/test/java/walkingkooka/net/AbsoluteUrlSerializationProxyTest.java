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
import walkingkooka.io.serialize.SerializationProxyTesting;
import walkingkooka.test.ClassTesting2;
import walkingkooka.type.JavaVisibility;

import java.util.Optional;

public final class AbsoluteUrlSerializationProxyTest implements ClassTesting2<AbsoluteUrlSerializationProxy>,
        SerializationProxyTesting<AbsoluteUrlSerializationProxy> {

    @Test
    public void testToString() {
        final AbsoluteUrl url = UrlScheme.HTTPS.andHost(HostAddress.with("example.com"))
                .setPort(Optional.of(IpPort.with(8080)))
                .setPath(UrlPath.parse("/abc/def"))
                .setQuery(UrlQueryString.with("ghi=jkl"));
        this.toStringAndCheck(url.writeReplace(), url.toString());
    }

    @Override
    public Class<AbsoluteUrlSerializationProxy> type() {
        return AbsoluteUrlSerializationProxy.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
