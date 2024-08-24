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

package walkingkooka.net;

import walkingkooka.Binary;
import walkingkooka.CanBeEmpty;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.header.MediaType;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Holds a payload text/binary along with a describing {@link MediaType}.
 * This will be useful to hold the contents of a browser clipboard or other similar payloads.
 */
public final class WebEntity implements HasText,
        CanBeEmpty,
        TreePrintable {

    /**
     * An empty {@link WebEntity} with no content-type or binary/text.
     */
    public static WebEntity empty() {
        return EMPTY;
    }

    private final static WebEntity EMPTY = new WebEntity(HttpEntity.EMPTY);

    // @VisibleForTesting
    static WebEntity with(final HttpEntity httpEntity) {
        return httpEntity.isEmpty() ?
                EMPTY :
                new WebEntity(httpEntity);
    }

    private WebEntity(final HttpEntity httpEntity) {
        this.httpEntity = httpEntity;
    }

    public Optional<MediaType> contentType() {
        return this.httpEntity.contentType();
    }

    public WebEntity setContentType(final Optional<MediaType> contentType) {
        Objects.requireNonNull(contentType, "contentType");

        final HttpEntity httpEntity = this.httpEntity;
        final HttpEntity after = httpEntity.setHeader(
                HttpHeaderName.CONTENT_TYPE,
                contentType.map(List::of)
                        .orElse(Lists.empty())
        );

        return httpEntity.equals(after) ?
                this :
                with(after);
    }

    public Binary binary() {
        return this.httpEntity.body();
    }

    public WebEntity setBinary(final Binary binary) {
        Objects.requireNonNull(binary, "binary");

        final HttpEntity httpEntity = this.httpEntity;
        final HttpEntity after = httpEntity.setBody(binary);

        return httpEntity.equals(after) ?
                this :
                with(after);
    }

    @Override
    public String text() {
        return this.httpEntity.text();
    }

    public WebEntity setText(final String text) {
        Objects.requireNonNull(text, "text");

        final HttpEntity httpEntity = this.httpEntity;
        final HttpEntity after = httpEntity.setBodyText(text);

        return httpEntity.equals(after) ?
                this :
                with(after);
    }

    // @VisibleForTesting
    final HttpEntity httpEntity;

    // CanBeEmpty.......................................................................................................

    @Override
    public boolean isEmpty() {
        return this.httpEntity.isEmpty();
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.httpEntity.hashCode();
    }

    public boolean equals(final Object other) {
        return this == other || other instanceof WebEntity && this.equals0((WebEntity) other);
    }

    private boolean equals0(final WebEntity other) {
        return this.httpEntity.equals(other.httpEntity);
    }

    @Override
    public String toString() {
        return this.httpEntity.toString();
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());
        printer.indent();
        {
            this.httpEntity.printTree(printer);
        }
        printer.outdent();
    }
}
