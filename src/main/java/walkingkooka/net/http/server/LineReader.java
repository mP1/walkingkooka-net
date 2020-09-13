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

/**
 * A simple header used to parse http requests/responses that reads line terminated by CRNL from text.
 */
final class LineReader {

    static LineReader with(final String text) {
        return new LineReader(text);
    }

    private LineReader(final String text) {
        super();
        this.text = text;
    }

    /**
     * Attempts to read a line of text ending with CRNL.
     */
    String readLine() {
        final String text = this.text;
        final int position = this.position;
        final String line;
        if (position == text.length()) {
            line = null;
        } else {
            final int cr = text.indexOf('\r', position);
            if (-1 != cr) {
                if (cr + 1 >= text.length() || text.charAt(cr + 1) != '\n') {
                    throw new IllegalArgumentException("Invalid line ending");
                }
                line = text.substring(position, cr);
                this.position = cr + 2;
            } else {
                line = text.substring(position);
                this.position = text.length();
            }
        }

        return line;
    }

    String leftOver() {
        final String text = this.text;
        final int position = this.position;

        return position == text.length() ?
                null :
                text.substring(position);
    }

    private final String text;
    private int position = 0;

    @Override
    public String toString() {
        return this.leftOver();
    }
}
