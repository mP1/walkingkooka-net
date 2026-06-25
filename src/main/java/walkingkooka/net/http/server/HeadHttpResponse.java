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

import walkingkooka.collect.list.Lists;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpMethod;
import walkingkooka.net.http.HttpStatus;
import walkingkooka.net.http.HttpStatusCode;

import java.util.List;
import java.util.Objects;

/**
 * If the request was a HEAD, wraps the response and clears any body for given {@link HttpEntity}.
 */
final class HeadHttpResponse extends WrapperHttpRequestHttpResponse {

    static HttpResponse with(final HttpRequest request,
                             final HttpResponse response) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(response, "response");

        return request.method() == HttpMethod.HEAD ?
            new HeadHttpResponse(request, response) :
            response;
    }

    private HeadHttpResponse(final HttpRequest request,
                             final HttpResponse response) {
        super(request, response);
    }

    @Override
    public void setStatus(final HttpStatus status) {
        this.response.setStatus(status);

        this.fixAllowedHeaderIfNecessary();
    }

    @Override
    public void setEntity(final HttpEntity entity) {
        this.response.setEntity(
            entity.setBody(HttpEntity.NO_BODY)
        );
        this.fixAllowedHeaderIfNecessary();
    }

    private void fixAllowedHeaderIfNecessary() {
        if (this.response.status()
            .map((HttpStatus s) -> s.value() == HttpStatusCode.METHOD_NOT_ALLOWED)
            .orElse(false)) {

            final HttpResponse response = this.response;

            final HttpEntity entity = response.entity();
            final List<HttpMethod> allowed = HttpHeaderName.ALLOW.header(entity)
                .orElse(Lists.empty());

            // do nothing if already contains HEAD
            if (false == allowed.contains(HttpMethod.HEAD)) {
                if (allowed.contains(HttpMethod.GET)) {

                    final List<HttpMethod> allowedAndHeader = Lists.array();
                    allowedAndHeader.addAll(allowed);
                    allowedAndHeader.add(HttpMethod.HEAD);

                    response.setEntity(
                        entity.setHeader(
                            HttpHeaderName.ALLOW,
                            Lists.of(allowedAndHeader)
                        )
                    );
                }
            }
        }
    }
}