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
import walkingkooka.net.header.ETag;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallException;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
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
     * {@see AutoGzipEncodingHttpResponse}
     */
    @GwtIncompatible
    public static HttpResponse autoGzipEncoding(final HttpRequest request,
                                                final HttpResponse response) {
        return AutoGzipEncodingHttpResponse.with(request, response);
    }

    /**
     * {@see DefaultHeadersHttpResponse}
     */
    @GwtIncompatible
    public static HttpResponse defaultHeaders(final Map<HttpHeaderName<?>, List<?>> headers,
                                              final HttpResponse response) {
        return DefaultHeadersHttpResponse.with(headers, response);
    }

    /**
     * {@see FakeHttpResponse}
     */
    public static HttpResponse fake() {
        return new FakeHttpResponse();
    }

    /**
     * {@see HeadHttpResponse}
     */
    @GwtIncompatible
    public static HttpResponse head(final HttpRequest request, final HttpResponse response) {
        return HeadHttpResponse.with(request, response);
    }

    /**
     * {@see HeaderScopeHttpResponse}
     */
    @GwtIncompatible
    public static HttpResponse headerScope(final HttpResponse response) {
        return HeaderScopeHttpResponse.with(response);
    }

    /**
     * {@see HttpStatusCodeRequiredHeadersHttpResponse}
     */
    @GwtIncompatible
    public static HttpResponse httpStatusCodeRequiredHeaders(final HttpResponse response) {
        return HttpStatusCodeRequiredHeadersHttpResponse.with(response);
    }

    /**
     * {@see IfNoneMatchAwareHttpResponse}
     */
    @GwtIncompatible
    public static HttpResponse ifNoneMatchAware(final HttpRequest request,
                                                final HttpResponse response,
                                                final Function<byte[], ETag> computer) {
        return IfNoneMatchAwareHttpResponse.with(request, response, computer);
    }

    /**
     * {@see LastModifiedAwareHttpResponse}
     */
    @GwtIncompatible
    public static HttpResponse lastModifiedAware(final HttpRequest request,
                                                 final HttpResponse response) {
        return LastModifiedAwareHttpResponse.with(request, response);
    }

    /**
     * {@see MultiPartAwareHttpResponse}
     */
    @GwtIncompatible
    public static HttpResponse multiPartAware(final HttpResponse response) {
        return MultiPartAwareHttpResponse.with(response);
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

    // JsonNodeContext..................................................................................................

    /**
     * Accepts a json string holding a serialized request in text form.
     */
    static HttpResponse unmarshall(final JsonNode node,
                                   final JsonNodeUnmarshallContext context) {
        Objects.requireNonNull(node, "node");

        // horrible hack to assume SECURED, doesnt actually matter and should be ignored during routing.
        return parse(node.stringOrFail());
    }

    private static JsonNode marshall(final HttpResponse response,
                                     final JsonNodeMarshallContext context) {
        if(false == response.version().isPresent()) {
            throw new JsonNodeMarshallException("Version missing " + response);
        }
        if(false == response.status().isPresent()) {
            throw new JsonNodeMarshallException("Status missing " + response);
        }
        return JsonNode.string(response.toString());
    }

    // TODO Write an annotation-processor that discovers all HttpResponses and adds them to the last parameter.
    static {
        JsonNodeContext.register("HttpResponse",
                HttpResponses::unmarshall,
                HttpResponses::marshall,
                HttpResponse.class, RecordingHttpResponse.class); // unfortunately J2cl runtime doesnt support Class.isInstance
    }
    
    /**
     * Stop creation
     */
    private HttpResponses() {
        throw new UnsupportedOperationException();
    }
}
