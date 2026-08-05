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

import javaemul.internal.annotations.GwtIncompatible;
import walkingkooka.reflect.PublicStaticHelper;

import java.util.function.Supplier;

public final class HttpResponses implements PublicStaticHelper {

    /**
     * {@see HttpResponseParser}
     */
    public static HttpResponse parse(final String response) {
        return HttpResponseParser.parse(response);
    }

    /**
     * {@see AutoContentLengthHttpResponse}
     */
    @GwtIncompatible
    public static HttpResponse contentLength(final HttpRequest request,
                                             final HttpResponse response) {
        return AutoContentLengthHttpResponse.with(request, response);
    }

    /**
     * {@see FakeHttpResponse}
     */
    public static HttpResponse fake() {
        return new FakeHttpResponse();
    }

    /**
     * {@see RangeAwareHttpResponse}
     */
    @GwtIncompatible
    public static HttpResponse rangeAware(final HttpRequest request,
                                          final HttpResponse response,
                                          final Supplier<Byte> boundaryCharacters) {
        return RangeAwareHttpResponse.with(request, response,
            boundaryCharacters);
    }

    /**
     * {@see RecordingHttpResponse}
     */
    public static HttpResponse recording() {
        return RecordingHttpResponse.with();
    }

    /**
     * {@see RequiredHeadersHttpResponse}
     */
    @GwtIncompatible
    public static HttpResponse requiredHeaders(final HttpRequest request,
                                               final HttpResponse response) {
        return RequiredHeadersHttpResponse.with(request, response);
    }

    /**
     * Stop creation
     */
    private HttpResponses() {
        throw new UnsupportedOperationException();
    }
}
