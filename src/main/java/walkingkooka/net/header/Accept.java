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

import walkingkooka.collect.list.Lists;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html"></a>
 * <pre>
 * 14.1 Accept
 * The Accept request-header field can be used to specify certain media types which are acceptable for the response. Accept headers can be used to indicate that the request is specifically limited to a small set of desired types, as in the case of a request for an in-line image.
 *
 *        Accept         = "Accept" ":"
 *                         #( media-range [ accept-params ] )
 *        media-range    = ( "**&#47;"
 *                         | ( type "/" "*" )
 *                         | ( type "/" subtype )
 *                         ) *( ";" parameter )
 *        accept-params  = ";" "q" "=" qvalue *( accept-extension )
 *        accept-extension = ";" token [ "=" ( token | quoted-string ) ]
 * The asterisk "*" character is used to group media types into ranges, with "**&#47;" indicating all media types and "type*&#47;" indicating all subtypes of that type. The media-range MAY include media type parameters that are applicable to that range.
 *
 * Each media-range MAY be followed by one or more accept-params, beginning with the "q" parameter for indicating a relative quality factor. The first "q" parameter (if any) separates the media-range parameter(s) from the accept-params. Quality factors allow the user or user agent to indicate the relative degree of preference for that media-range, using the qvalue scale from 0 to 1 (section 3.9). The default value is q=1.
 *
 *       Note: Use of the "q" parameter name to separate media type
 *       parameters from Accept extension parameters is due to historical
 *       practice. Although this prevents any media type parameter named
 *       "q" from being used with a media range, such an event is believed
 *       to be unlikely given the lack of any "q" parameters in the IANA
 *       media type registry and the rare usage of any media type
 *       parameters in Accept. Future media types are discouraged from
 *       registering any parameter named "q".
 * The example
 *
 *        Accept: audio*&#47;; q=0.2, audio/basic
 * SHOULD be interpreted as "I prefer audio/basic, but send me any audio type if it is the best available after an 80% mark-down in quality."
 *
 * If no Accept header field is present, then it is assumed that the client accepts all media types. If an Accept header field is present, and if the server cannot send a response which is acceptable according to the combined Accept field value, then the server SHOULD send a 406 (not acceptable) response.
 *
 * A more elaborate example is
 *
 *        Accept: text/plain; q=0.5, text/html,
 *                text/x-dvi; q=0.8, text/x-c
 * Verbally, this would be interpreted as "text/html and text/x-c are the preferred media types, but if they do not exist, then send the text/x-dvi entity, and if that does not exist, send the text/plain entity."
 *
 * Media ranges can be overridden by more specific media ranges or specific media types. If more than one media range applies to a given type, the most specific reference has precedence. For example,
 *
 *        Accept: text*&#47;, text/html, text/html;level=1, **&#47;
 * have the following precedence:
 *
 *        1) text/html;level=1
 *        2) text/html
 *        3) text*&#47;
 *        4) **&#47;
 * The media type quality factor associated with a given type is determined by finding the media range with the highest precedence which matches that type. For example,
 *
 *        Accept: text*&#47;;q=0.3, text/html;q=0.7, text/html;level=1,
 *                text/html;level=2;q=0.4, **&#47;;q=0.5
 * would cause the following values to be associated:
 *
 *        text/html;level=1         = 1
 *        text/html                 = 0.7
 *        text/plain                = 0.3
 *        image/jpeg                = 0.5
 *        text/html;level=2         = 0.4
 *        text/html;level=3         = 0.7
 *       Note: A user agent might be provided with a default set of quality
 *       values for certain media ranges. However, unless the user agent is
 *       a closed system which cannot interact with other rendering agents,
 *       this default set ought to be configurable by the user.
 * </pre>
 */
public final class Accept extends Header2<List<MediaType>> implements Predicate<MediaType>,
    HasQualityFactorSortedValues<MediaType> {

    // @VisibleForTesting
    final static List<MediaType> ALL_MEDIA_TYPE = Lists.of(MediaType.ALL);

    /**
     * The default Accept equivalent to
     * <pre>
     * Accept: *\/*
     * </pre>
     */
    public static Accept DEFAULT = new Accept(ALL_MEDIA_TYPE);

    /**
     * Parses a header value that contains {@link Accept}
     */
    public static Accept parse(final String text) {
        return with(MediaTypeListHeaderParser.parseMediaTypeList(text));
    }

    /**
     * Factory that creates a new {@link Accept}
     */
    public static Accept with(final List<MediaType> mediaTypes) {
        return withCopy(
            nonEmptyImmutableList(mediaTypes, "media types")
        );
    }

    private static Accept withCopy(final List<MediaType> mediaTypes) {
        return ALL_MEDIA_TYPE.equals(mediaTypes) ?
            DEFAULT :
            new Accept(mediaTypes);
    }

    /**
     * Private ctor
     */
    private Accept(final List<MediaType> values) {
        super(values);
    }

    // Header.....................................................................................................

    @Override
    public String toHeaderText() {
        return Header.toHeaderTextList(value, SEPARATOR);
    }

    private final static String SEPARATOR = Header.SEPARATOR.string().concat(" ");

    @Override
    public boolean isWildcard() {
        return false;
    }

    @Override
    public boolean isMultipart() {
        return false;
    }

    @Override
    public boolean isRequest() {
        return true;
    }

    @Override
    public boolean isResponse() {
        return false;
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof Accept;
    }

    // Predicate........................................................................................................

    /**
     * Returns true if the given {@link MediaType} is matched by any of the values.
     */
    @Override
    public boolean test(final MediaType contentType) {
        Objects.requireNonNull(contentType, "contentType");

        return this.value.stream()
            .anyMatch(m -> m.test(contentType));
    }

    /**
     * Tests if the given {@link MediaType} satisfies this ACCEPT header.
     */
    public void testOrFail(final MediaType mediaType) {
        if (false == this.test(mediaType)) {

            // Accept: Got text/plain require text/json
            throw new IllegalArgumentException(
                this.requireIncompatibleMessage(mediaType)
            );
        }
    }

    /**
     * The message that is used to report that this ACCEPT {@link MediaType} was not satisfied by a given {@link MediaType}.
     */
    public String requireIncompatibleMessage(final MediaType mediaType) {
        return HttpHeaderName.ACCEPT +
            ": Got " +
            mediaType.clearParameters() +
            " require " +
            this.mediaTypesWithoutParameters();
    }

    private String mediaTypesWithoutParameters() {
        if (null == this.mediaTypesWithoutParameters) {
            this.mediaTypesWithoutParameters = this.value()
                .stream()
                .map(m -> m.clearParameters().toString())
                .collect(Collectors.joining(SEPARATOR));
        }

        return this.mediaTypesWithoutParameters;
    }

    private String mediaTypesWithoutParameters;
}
