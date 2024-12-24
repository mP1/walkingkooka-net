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

/**
 * A problem that occurred while parsing and validating a {@link HostAddress host address}.
 */
abstract public class HostAddressProblem {

    /**
     * {@see HostAddressProblemIncomplete}
     */
    public static HostAddressProblem incomplete() {
        return HostAddressProblemIncomplete.INSTANCE;
    }

    /**
     * {@see HostAddressProblemInvalidCharacter}
     */
    public static HostAddressProblem invalidCharacter(final int at) {
        return HostAddressProblemInvalidCharacter.with(at);
    }

    /**
     * {@see HostAddressProblemInvalidLength}
     */
    public static HostAddressProblem invalidLength(final int at) {
        return HostAddressProblemInvalidLength.with(at);
    }

    /**
     * {@see HostAddressProblemInvalidValue}
     */
    public static HostAddressProblem invalidValue(final int at) {
        return HostAddressProblemInvalidValue.with(at);
    }

    /**
     * {@see HostAddressProblemProbablyIp4}
     */
    public static HostAddressProblem probablyIp4() {
        return HostAddressProblemProbablyIp4.INSTANCE;
    }

    /**
     * {@see HostAddressProblemProbablyIp6}
     */
    public static HostAddressProblem probablyIp6() {
        return HostAddressProblemProbablyIp6.INSTANCE;
    }

    /**
     * Package private to limit sub classing.
     */
    HostAddressProblem() {
        super();
    }

    /**
     * Reports by throwing an exception.
     */
    abstract void report(final String message);

    /**
     * Builds a message using the address.
     */
    abstract public String message(final String address);

    /**
     * When true do not try the next parse method.
     */
    abstract boolean stopTrying();

    /**
     * Returns the relative priority of this problem.
     */
    abstract int priority();

    /**
     * Force sub classes to implement.
     */
    @Override
    abstract public String toString();
}
