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

import walkingkooka.Cast;
import walkingkooka.Value;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.text.HasText;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The query string component within a {@link Url}. Methods are available to retrieve the first parameter value, or all parameter values
 * or to view all parameters as a {@link Map}.
 */
public final class UrlQueryString implements Value<String>,
        Comparable<UrlQueryString>,
        HasText {

    /**
     * An empty {@link UrlQueryString} with no length or parameters.
     */
    public final static UrlQueryString EMPTY = new UrlQueryString(
            "",
            Lists.empty(),
            Maps.empty()
    );

    /**
     * Factory that creates a new {@link UrlQueryString}
     */
    public static UrlQueryString parse(final String value) {
        Objects.requireNonNull(value, "queryString");

        return value.length() == 0 ?
                walkingkooka.net.UrlQueryString.EMPTY :
                parseNotEmpty(value);
    }

    private static UrlQueryString parseNotEmpty(final String queryString) {
        final List<UrlParameterKeyValuePair> pairs = Lists.array();
        final Map<UrlParameterName, UrlParameterValueList> parameters = Maps.ordered();

        final char paramSeparator = Url.QUERY_PARAMETER_SEPARATOR.character();
        final char paramSeparator2 = Url.QUERY_PARAMETER_SEPARATOR2.character();

        // parse query
        final int length = queryString.length();
        int start = 0;

        for (int i = 0; i < length; i++) {
            final char c = queryString.charAt(i);

            // end of name/value pair
            if (paramSeparator == c || paramSeparator2 == c) {
                final UrlParameterKeyValuePair pair = UrlParameterKeyValuePair.encodedWithSeparator(queryString.substring(start, i), c);
                addToMap(pair, parameters);
                pairs.add(pair);
                start = i + 1;
            }
        }

        if (start < length) {
            final UrlParameterKeyValuePair pair = UrlParameterKeyValuePair.encodedWithoutSeparator(queryString.substring(start, length));
            addToMap(pair, parameters);
            pairs.add(pair);
        }

        return new UrlQueryString(
                queryString,
                Lists.immutable(pairs),
                Maps.readOnly(parameters)
        );
    }

    /**
     * Package private constructor use factory
     */
    //@VisibleForTesting
    UrlQueryString(final String queryString,
                   final List<UrlParameterKeyValuePair> pairs,
                   final Map<UrlParameterName, UrlParameterValueList> parameters) {
        super();
        this.queryString = queryString;
        this.pairs = pairs;
        this.parameters = parameters;
    }

    /**
     * Returns the raw query string.
     */
    @Override
    public String value() {
        return this.queryString;
    }

    private final String queryString;

    /**
     * Returns true if this query string is empty, no parameters etc.
     */
    public boolean isEmpty() {
        return this.queryString.isEmpty();
    }

    // parameters ................................................................................................

    /**
     * Returns a read-only {@link Map} holding all the parameters and values.
     */
    public Map<UrlParameterName, List<String>> parameters() {
        return Cast.to(this.parameters);
    }

    /**
     * Lazily created map.
     */
    private final Map<UrlParameterName, UrlParameterValueList> parameters;

    /**
     * Makes a copy of all parameters.
     */
    private Map<UrlParameterName, UrlParameterValueList> parametersCopy() {
        final Map<UrlParameterName, UrlParameterValueList> copy = Maps.ordered();
        copy.putAll(this.parameters);
        return copy;
    }

    /**
     * Retrieves the parameter with the name returning the first value.
     */
    public Optional<String> parameter(final UrlParameterName name) {
        Optional<String> value = Optional.empty();

        for (UrlParameterKeyValuePair pair : this.pairs) {
            if (pair.name.equals(name)) {
                value = Optional.ofNullable(pair.value);
                break;
            }
        }

        return value;
    }

    /**
     * Retrieves the parameter with the name returning all values or an empty list.
     */
    public List<String> parameterValues(final UrlParameterName name) {
        return this.parameters()
                .getOrDefault(
                        name,
                        PARAMETER_VALUES_MISSING
                );
    }

    private final static List<String> PARAMETER_VALUES_MISSING = Lists.empty();

    /**
     * Adds the parameters in the given {@link UrlQueryString} to this returning a new {@link UrlQueryString} if necessary.
     */
    public UrlQueryString addParameters(final UrlQueryString queryString) {
        Objects.requireNonNull(queryString, "queryString");

        // if queryString i empty this will be returned by #addParameters0
        return this.isEmpty() ?
                queryString :
                this.addParametersNonEmpty(queryString);
    }

    private UrlQueryString addParametersNonEmpty(final UrlQueryString queryString) {
        UrlQueryString result = this;

        for (final UrlParameterKeyValuePair nameAndValues : queryString.pairs) {
            result = result.addParameter0(
                    nameAndValues.name,
                    nameAndValues.value
            );
        }

        return result;
    }

    /**
     * Adds values to the multimap of values.
     */
    private static void addToMap(final UrlParameterKeyValuePair pair,
                                 final Map<UrlParameterName, UrlParameterValueList> parameters) {
        final UrlParameterName name = pair.name;
        UrlParameterValueList values = parameters.get(name);
        if (null == values) {
            values = UrlParameterValueList.empty();
            parameters.put(name, values);
        }
        values.addParameterValue(pair.value);
    }

    /**
     * Adds a new parameter to this query string.
     */
    public UrlQueryString addParameter(final UrlParameterName name,
                                       final String value) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(value, "value");

        return addParameter0(
                name,
                value
        );
    }

    private UrlQueryString addParameter0(final UrlParameterName name,
                                         final String value) {
        return this.isEmpty() ?
                this.addParameterToEmpty(name, value) :
                this.addParameterToNotEmpty(name, value);
    }

    private UrlQueryString addParameterToEmpty(final UrlParameterName name,
                                               final String value) {
        final UrlParameterValueList values = UrlParameterValueList.empty();
        values.addParameterValue(value);

        return new UrlQueryString(encodeParameter(name, value),
                Lists.of(UrlParameterKeyValuePair.nameAndValue(name, value)),
                Maps.of(name, values));
    }

    private UrlQueryString addParameterToNotEmpty(final UrlParameterName name,
                                                  final String value) {
        final UrlParameterKeyValuePair pair = UrlParameterKeyValuePair.nameAndValue(name, value);

        final List<UrlParameterKeyValuePair> pairs = this.pairsCopy();
        pairs.add(pair);

        final Map<UrlParameterName, UrlParameterValueList> parameters = this.parametersCopy();
        addToMap(pair, parameters);

        // need to find the last param separator and append to query string if necessary and then append new param.
        final char paramSeparator = Url.QUERY_PARAMETER_SEPARATOR.character();
        final char paramSeparator2 = Url.QUERY_PARAMETER_SEPARATOR2.character();
        String queryString = this.queryString;

        final int length = queryString.length();
        final char last = queryString.charAt(length - 1);

        // queryString already ends in separator just append new key/value.
        if (paramSeparator == last || paramSeparator2 == last) {
            queryString = queryString + encodeParameter(name, value);
        } else {
            char lastParamSeparator = paramSeparator;

            // find the last separator and copy that
            for (int i = 1; i < length; i++) {
                final char c = queryString.charAt(length - i - 1);
                if (paramSeparator == c || paramSeparator2 == c) {
                    lastParamSeparator = c;
                    break;
                }
            }
            queryString = queryString + lastParamSeparator + encodeParameter(name, value);
        }

        return new UrlQueryString(queryString, pairs, parameters);
    }

    private static String encodeParameter(final UrlParameterName name,
                                          final String value) {
        return encodeParameterValue(
                name.value()
        ) + Url.QUERY_NAME_VALUE_SEPARATOR.character() +
                encodeParameterValue(value);
    }

    private static String encodeParameterValue(final String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (final UnsupportedEncodingException never) {
            throw new Error(never);
        }
    }

    /**
     * Removes all parameter values with the given name.
     */
    public UrlQueryString removeParameter(final UrlParameterName name) {
        Objects.requireNonNull(name, "name");

        return this.removeParameter0(name, null);
    }

    /**
     * Removes all parameter values with the given name and value
     */
    public UrlQueryString removeParameter(final UrlParameterName name,
                                          final String value) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(value, "value");

        return this.removeParameter0(name, value);
    }

    private UrlQueryString removeParameter0(final UrlParameterName name,
                                            final String value) {
        final List<UrlParameterKeyValuePair> pairs = this.pairsCopy();
        boolean removed = false;
        final StringBuilder queryString = new StringBuilder();

        char separator = 0;

        for (final Iterator<UrlParameterKeyValuePair> i = pairs.iterator(); i.hasNext(); ) {
            final UrlParameterKeyValuePair pair = i.next();
            if (pair.name.equals(name) && (null == value || value.equals(pair.value))) {
                i.remove();
                removed = true;
            } else {
                if (0 != separator) {
                    queryString.append(separator);
                }
                queryString.append(pair.encoded);
                if (pair.separatorIncluded) {
                    separator = pair.separator;
                }
            }
        }

        return removed ?
                removeParameter1(name, value, queryString.toString(), pairs) :
                this;
    }

    private UrlQueryString removeParameter1(final UrlParameterName name,
                                            final String value,
                                            final String queryString,
                                            final List<UrlParameterKeyValuePair> pairs) {
        final Map<UrlParameterName, UrlParameterValueList> parameters = this.parametersCopy();
        if (null == value) {
            parameters.remove(name);
        } else {
            final UrlParameterValueList values = parameters.get(name);
            values.removeParameterValues(value);
            if (values.isEmpty()) {
                parameters.remove(name);
            }
        }

        return queryString.isEmpty() ?
                EMPTY :
                new UrlQueryString(queryString, pairs, parameters);
    }

    /**
     * Makes a copy of all pairs.
     */
    private List<UrlParameterKeyValuePair> pairsCopy() {
        final List<UrlParameterKeyValuePair> copy = Lists.array();
        copy.addAll(this.pairs);
        return copy;
    }

    /**
     * Cache of {@link Map} of all parameters taken from the query string.
     */
    private final List<UrlParameterKeyValuePair> pairs;

    // Object.........................................................................................................

    @Override
    public int hashCode() {
        return this.parameters().hashCode();
    }

    /**
     * Compares the decoded parameter pairs for equality, ignoring the query string form.
     */
    @Override
    public boolean equals(final Object other) {
        return (this == other) ||
                (other instanceof UrlQueryString &&
                        this.equals0((UrlQueryString) other));
    }

    private boolean equals0(final UrlQueryString other) {
        return this.parameters().equals(
                other.parameters()
        );
    }

    /**
     * Prints the original query string in its original form.
     */
    @Override
    public String toString() {
        return this.queryString;
    }

    /**
     * This method is only called by {@link AbsoluteOrRelativeUrl#toString()} and auto adds {@link Url#QUERY_START}
     * if the query string is not empty.
     * Normal query string do not actually include the question mark within their value.
     */
    void absoluteOrRelativeUrlToString(final StringBuilder b) {
        if (false == this.isEmpty()) {
            b.append(Url.QUERY_START.character());
            b.append(this.queryString);
        }
    }

    // Comparable.......................................................................................................

    /**
     * Strings comparison is case-sensitive
     */
    @Override
    public int compareTo(UrlQueryString other) {
        return this.queryString.compareTo(other.queryString);
    }

    // HasText..........................................................................................................

    @Override
    public String text() {
        return this.queryString;
    }
}
