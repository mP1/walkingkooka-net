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

import walkingkooka.collect.list.Lists;
import walkingkooka.naming.Name;
import walkingkooka.net.http.server.HttpRequest;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * The {@link Name} of a query string parameter.
 */
public final class UrlParameterName extends NetName
        implements Comparable<UrlParameterName>,
        HttpRequestAttribute<List<String>> {

    private final static long serialVersionUID = 1L;

    /**
     * Factory that creates a {@link UrlParameterName}
     */
    public static UrlParameterName with(final String name) {
        return new UrlParameterName(
                CharSequences.failIfNullOrEmpty(name, "name")
        );
    }

    /**
     * Private constructor
     */
    private UrlParameterName(final String name) {
        super(name);
    }

    /**
     * Returns the first value or fails by throwing a {@link IllegalArgumentException} if missing.
     */
    public String firstParameterValueOrFail(final Map<HttpRequestAttribute<?>, ?> parameters) {
        return this.firstParameterValue(parameters)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Missing query parameter " +
                                        CharSequences.quote(this.value())
                        )
                );
    }

    /**
     * Returns the first parameter or empty.
     */
    public Optional<String> firstParameterValue(final Map<HttpRequestAttribute<?>, ?> parameters) {
        List<String> values = this.parameterValue(parameters).orElse(Lists.empty());

        final String first;
        switch (values.size()) {
            case 0:
                first = null;
                break;
            default:
                first = values.get(0);
                break;
        }

        return Optional.ofNullable(first);
    }

    /**
     * Assumes a single required parameter value and converts using the given {@link Function} or fails.
     */
    public <T> T parameterValueOrFail(final Map<HttpRequestAttribute<?>, ?> parameters,
                                      final Function<String, T> converter) {
        checkParameters(parameters);
        Objects.requireNonNull(converter, "converter");

        final Optional<List<String>> maybeValues = this.parameterValue(parameters);
        if (!maybeValues.isPresent()) {
            throw new IllegalArgumentException("Required parameter " + this + " missing");
        }
        final List<String> values = maybeValues.get();
        if (values.size() != 1) {
            throw new IllegalArgumentException("Required parameter " + this + " incorrect=" + values);
        }
        final String value = values.get(0);
        try {
            return converter.apply(value);
        } catch (final NullPointerException | IllegalArgumentException cause) {
            throw cause;
        } catch (final Exception cause) {
            throw new IllegalArgumentException("Invalid parameter " + this + " value " + CharSequences.quoteIfChars(value));
        }
    }

    private static void checkParameters(final Map<HttpRequestAttribute<?>, ?> parameters) {
        Objects.requireNonNull(parameters, "parameters");
    }

    // HttpRequestAttribute..............................................................................................

    /**
     * A typed getter that retrieves a value from a {@link HttpRequest}
     */
    @Override
    public Optional<List<String>> parameterValue(final HttpRequest request) {
        return Optional.ofNullable(request.url().query().parameters().get(this));
    }

    // Comparable..............................................................................................

    @Override
    public int compareTo(final UrlParameterName other) {
        return this.compareTo0(other);
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof UrlParameterName;
    }

    @Override
    public String toString() {
        return this.name;
    }

    // HasCaseSensitivity................................................................................................

    @Override
    public CaseSensitivity caseSensitivity() {
        return CASE_SENSITIVITY;
    }

    private final static CaseSensitivity CASE_SENSITIVITY = CaseSensitivity.SENSITIVE;
}
