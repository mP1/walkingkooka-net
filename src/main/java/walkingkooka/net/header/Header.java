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

import walkingkooka.text.CharacterConstant;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Contract implemented by header value types.
 */
public interface Header extends HasHeaderScope {

    /**
     * Remove any comments from text. If the text contains an unterminated comment an exception will be thrown.
     */
    static String removeComments(final String text) {
        return CommentRemovingHeaderParser.removeComments(text);
    }

    /**
     * Builds a header value representation of a list of headers.
     */
    static <H extends Header> String toHeaderTextList(final List<H> headers,
                                                      final String separator) {
        Objects.requireNonNull(headers, "headers");
        Objects.requireNonNull(separator, "separator");

        return headers.stream()
                .map(Header::toHeaderText)
                .collect(Collectors.joining(separator));
    }

    /**
     * The separator character that separates multiple header values.
     */
    CharacterConstant SEPARATOR = CharacterConstant.with(',');

    /**
     * A special name that identifies a wildcard selection.
     */
    CharacterConstant WILDCARD = CharacterConstant.with('*');

    /**
     * The separator between parameter name and value.
     */
    CharacterConstant PARAMETER_NAME_VALUE_SEPARATOR = CharacterConstant.with('=');

    /**
     * The separator character that separates parameters belonging to a header value.
     */
    CharacterConstant PARAMETER_SEPARATOR = CharacterConstant.with(';');

    /**
     * Returns the value as header text and never includes the header name or colon separator.
     * <pre>
     * Content-Range: bytes STAR/STAR
     * ->
     * bytes STAR/STAR
     *
     * Content-Length: 123
     * ->
     * 123
     * </pre>
     */
    String toHeaderText();

    /**
     * Returns true only if this is a wildcard.
     */
    boolean isWildcard();

    /**
     * Tests if two headers are equal ignoring any parameters on either.
     */
    default boolean equalsIgnoringParameters(final Object other) {
        return this.equals(other);
    }

    /**
     * Tests if two headers are equal and requiring parameters on this to all be present on other, ignoring
     * any extra parameters.
     */
    default boolean equalsOnlyPresentParameters(final Object other) {
        return this.equals(other);
    }
}
