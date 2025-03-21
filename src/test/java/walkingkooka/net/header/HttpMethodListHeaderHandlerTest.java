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
import walkingkooka.collect.list.Lists;
import walkingkooka.net.http.HttpMethod;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HttpMethodListHeaderHandlerTest extends
    NonStringHeaderHandlerTestCase<HttpMethodListHeaderHandler, List<HttpMethod>> {

    @Override
    public String typeNamePrefix() {
        return HttpMethod.class.getSimpleName();
    }

    @Test
    public void testParseGet() {
        this.parseStringAndCheck2("GET", HttpMethod.GET);
    }

    @Test
    public void testParseGetPost() {
        this.parseStringAndCheck2("GET,POST", HttpMethod.GET, HttpMethod.POST);
    }

    @Test
    public void testParseGetWhitespacePost() {
        this.parseStringAndCheck2("GET,  POST", HttpMethod.GET, HttpMethod.POST);
    }

    private void parseStringAndCheck2(final String header, final HttpMethod... methods) {
        this.parseStringAndCheck(header, Lists.of(methods));
    }

    @Test
    public void testCheckIncludesNullFails() {
        assertThrows(HeaderException.class, () -> this.check(Lists.of(this.method(), null)));
    }

    @Test
    public void testCheckIncludesWrongTypeFails() {
        assertThrows(HeaderException.class, () -> this.check(Lists.of(this.method(), "WRONG!")));
    }

    private HttpMethod method() {
        return HttpMethod.GET;
    }

    @Test
    public void testGetResponse() {
        this.toTextAndCheck2("GET", HttpMethod.GET);
    }

    @Test
    public void testGetPostResponse() {
        this.toTextAndCheck2("GET, POST", HttpMethod.GET, HttpMethod.POST);
    }

    private void toTextAndCheck2(final String header, final HttpMethod... methods) {
        this.toTextAndCheck(Lists.of(methods), header);
    }

    @Override
    HttpMethodListHeaderHandler handler() {
        return HttpMethodListHeaderHandler.INSTANCE;
    }

    @Override
    HttpHeaderName<List<HttpMethod>> name() {
        return HttpHeaderName.ALLOW;
    }

    @Override
    String invalidHeader() {
        return "/relative/url/must/fail";
    }

    @Override
    List<HttpMethod> value() {
        return Lists.of(HttpMethod.GET, HttpMethod.POST);
    }

    @Override
    String valueType() {
        return this.listValueType(HttpMethod.class);
    }

    @Override
    String handlerToString() {
        return "List<HttpMethod>";
    }

    @Override
    public Class<HttpMethodListHeaderHandler> type() {
        return HttpMethodListHeaderHandler.class;
    }
}
