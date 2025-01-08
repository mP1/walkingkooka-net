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

import walkingkooka.text.CharSequences;

import java.util.Arrays;
import java.util.Optional;

/**
 * The range unit used in headers such as content-range.
 */
public enum RangeHeaderUnit implements Header {

    NONE("none") {
        @Override
        RangeHeaderUnit rangeCheck() {
            throw new IllegalArgumentException("Invalid range unit=" + this);
        }
    },

    BYTES("bytes") {
        @Override
        RangeHeaderUnit rangeCheck() {
            return this;
        }
    };

    RangeHeaderUnit(final String headerText) {
        this.headerText = headerText;
    }

    abstract RangeHeaderUnit rangeCheck();

    @Override
    public final String toHeaderText() {
        return this.headerText;
    }

    private final String headerText;

    @Override
    public final boolean isWildcard() {
        return false;
    }

    // HasHeaderScope....................................................................
    @Override
    public final boolean isMultipart() {
        return RangeHeader.IS_MULTIPART;
    }

    @Override
    public final boolean isRequest() {
        return RangeHeader.IS_REQUEST;
    }

    @Override
    public final boolean isResponse() {
        return RangeHeader.IS_RESPONSE;
    }

    /**
     * Finds a matching {@link RangeHeaderUnit} for the given text or throw an {@link IllegalArgumentException}.
     */
    public static RangeHeaderUnit parse(final String text) {
        final Optional<RangeHeaderUnit> found = Arrays.stream(values())
            .filter(u -> u.headerText.equalsIgnoreCase(text))
            .findFirst();
        if (!found.isPresent()) {
            throw new HeaderException("Unknown range unit " + CharSequences.quote(text));
        }
        return found.get();
    }
}
