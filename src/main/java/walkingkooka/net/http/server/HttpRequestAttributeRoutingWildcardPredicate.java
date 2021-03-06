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

import walkingkooka.net.UrlPath;
import walkingkooka.net.UrlPathName;

import java.util.function.Predicate;

/**
 * A {@link Predicate} used by {@link HttpRequestAttributeRouting#path(UrlPath)} which provides support for matching
 * wildcard path components. It will return true for any non null {@link UrlPathName}.
 */
final class HttpRequestAttributeRoutingWildcardPredicate implements Predicate<UrlPathName> {

    /**
     * Singleton
     */
    final static HttpRequestAttributeRoutingWildcardPredicate INSTANCE = new HttpRequestAttributeRoutingWildcardPredicate();

    /**
     * Private use singleton.
     */
    private HttpRequestAttributeRoutingWildcardPredicate() {
        super();
    }

    @Override
    public boolean test(final UrlPathName pathName) {
        return null != pathName;
    }

    @Override
    public String toString() {
        return "*";
    }
}
