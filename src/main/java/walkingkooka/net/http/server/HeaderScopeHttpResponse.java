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

import walkingkooka.Cast;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.HttpHeaderScope;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpStatus;

import java.util.Map.Entry;
import java.util.Objects;

/**
 * A {@link HttpResponse} that checks the scope correctness of any header names when adding new headers, or reading
 * existing headers.
 */
final class HeaderScopeHttpResponse extends WrapperHttpResponse {

    static HeaderScopeHttpResponse with(final HttpResponse response) {
        Objects.requireNonNull(response, "response");
        return new HeaderScopeHttpResponse(response);
    }

    private HeaderScopeHttpResponse(final HttpResponse response) {
        super(response);
    }

    @Override
    public void setStatus(final HttpStatus status) {
        this.response.setStatus(status);
    }

    @Override
    public void setEntity(final HttpEntity entity) {
        Objects.requireNonNull(entity, "entity");

        this.response.setEntity(entity);
    }

    private void checkHeader(final Entry<HttpHeaderName<?>, Object> entry) {
        HttpHeaderScope.RESPONSE.check(Cast.to(entry.getKey()), entry.getValue());
    }
}
