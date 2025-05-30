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

import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;

import java.util.Optional;

/**
 * Base class which parses text containing one or many media types.
 * <a href="https://tools.ietf.org/html/rfc7231#section-3.1.1.1></a>
 * <pre>
 *    HTTP uses Internet media types [RFC2046] in the Content-Type
 *    (Section 3.1.1.5) and Accept (Section 5.3.2) header fields in order
 *    to provide open and extensible data typing and type negotiation.
 *    Media types define both a data format and various processing models:
 *    how to process that data in accordance with each context in which it
 *    is received.
 *
 *      media-type = type "/" subtype *( OWS ";" OWS parameter )
 *      type       = token
 *      subtype    = token
 *
 *    The type/subtype MAY be followed by parameters in the form of
 *    name=value pairs.
 *
 *      parameter      = token "=" ( token / quoted-string )
 *
 *    The type, subtype, and parameter name tokens are case-insensitive.
 *    Parameter values might or might not be case-sensitive, depending on
 *    the semantics of the parameter name.  The presence or absence of a
 *    parameter might be significant to the processing of a media-type,
 *    depending on its definition within the media type registry.
 *
 *    A parameter value that matches the token production can be
 *    transmitted either as a token or within a quoted-string.  The quoted
 *    and unquoted values are equivalent.  For example, the following
 *    examples are all equivalent, but the first is preferred for
 *    consistency:
 *
 *      text/html;charset=utf-8
 *      text/html;charset=UTF-8
 *      Text/HTML;Charset="utf-8"
 *      text/html; charset="utf-8"
 *
 *    Internet media types ought to be registered with IANA according to
 *    the procedures defined in [BCP13].
 *
 *       Note: Unlike some similar constructs in other header fields, media
 *       type parameters do not allow whitespace (even "bad" whitespace)
 *       around the "=" character.
 * </pre>
 */
abstract class MediaTypeHeaderParser extends HeaderParserWithParameters<MediaType,
    MediaTypeParameterName<?>> {

    // @VisibleForTesting
    MediaTypeHeaderParser(final String text) {
        super(text);
    }

    @Override final MediaType wildcardValue() {
        final String type = "" + WILDCARD;
        this.position++;

        this.requireSlash();

        return MediaType.withParameters(
            type,
            this.subType(),
            this.suffix(),
            MediaType.NO_PARAMETERS
        );
    }

    @Override final MediaType value() {
        return MediaType.withParameters(
            this.type(),
            this.subType(),
            this.suffix(),
            MediaType.NO_PARAMETERS
        );
    }

    /**
     * Handles parsing the type part of a media type.
     */
    private String type() {
        final String type = this.token(TYPE);
        if (!this.hasMoreCharacters()) {
            failEmptyToken(TYPE_STRING);
        }

        this.requireSlash();

        if (type.isEmpty()) {
            this.failEmptyToken(TYPE_STRING);
        }

        return type;
    }

    final static CharPredicate TYPE = RFC2045TOKEN;

    private void requireSlash() {
        if (!this.hasMoreCharacters()) {
            this.failEmptyToken(SUBTYPE);
        }
        if (this.character() != SLASH) {
            this.failInvalidCharacter();
        }
        this.position++;
    }

    /**
     * Handles parsing the sub-type part of a media type, but not any following suffix or parameters.
     */
    private String subType() {
        String subType;

        if (this.hasMoreCharacters() && this.character() == WILDCARD) {
            subType = "" + WILDCARD;
            this.position++;
        } else {
            subType = this.token(SUB_TYPE);
            if (subType.isEmpty()) {
                if (false == this.hasMoreCharacters()) {
                    this.failEmptyToken(SUBTYPE);
                }

                this.failInvalidCharacter();
            }
        }
        return subType;
    }

    final static CharPredicate SUB_TYPE = RFC2045TOKEN.andNot(
        CharPredicates.any("+;")
    );

    /**
     * Handle parsing the suffix.
     */
    private Optional<String> suffix() {
        String suffix;

        if (this.hasMoreCharacters() && this.character() == MediaType.SUFFIX_SEPARATOR.character()) {
            this.position++;

            suffix = this.token(SUFFIX);
            if (suffix.isEmpty()) {
                if (!this.hasMoreCharacters()) {
                    this.failEmptyToken(SUBTYPE);
                }

                this.failInvalidCharacter();
            }
        } else {
            suffix = null;
        }

        return Optional.ofNullable(suffix);
    }

    final static CharPredicate SUFFIX = RFC2045TOKEN.andNot(
        CharPredicates.any(";")
    );

    @Override final void missingValue() {
        this.failMissingValue(MEDIATYPE);
    }

    final static String MEDIATYPE = "media type";

    private final static char SLASH = '/';
    private final static String TYPE_STRING = "type";
    private final static String SUBTYPE = "sub type";

    @Override final MediaTypeParameterName<?> parameterName() {
        return this.parameterName(PARAMETER_NAME, MediaTypeParameterName::with);
    }

    private final static CharPredicate PARAMETER_NAME = RFC2045TOKEN;

    @Override final String quotedParameterValue(final MediaTypeParameterName<?> parameterName) {
        return this.quotedText(QUOTED_PARAMETER_VALUE, ESCAPING_SUPPORTED);
    }

    final static CharPredicate QUOTED_PARAMETER_VALUE = ASCII;

    @Override final String unquotedParameterValue(final MediaTypeParameterName<?> parameterName) {
        return this.token(UNQUOTED_PARAMETER_VALUE);
    }

    final static CharPredicate UNQUOTED_PARAMETER_VALUE = RFC2045TOKEN;
}
