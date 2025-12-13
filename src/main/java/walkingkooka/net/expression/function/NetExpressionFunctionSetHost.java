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

package walkingkooka.net.expression.function;

import walkingkooka.Cast;
import walkingkooka.net.HasHostAddress;
import walkingkooka.net.HostAddress;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;

import java.util.List;

/**
 * A function that may be used to set or replace the {@link HostAddress} component from a given {@link HasHostAddress}.
 */
final class NetExpressionFunctionSetHost<C extends ExpressionEvaluationContext> extends NetExpressionFunction<HasHostAddress, C> {

    static <C extends ExpressionEvaluationContext> NetExpressionFunctionSetHost<C> instance() {
        return Cast.to(INSTANCE);
    }

    /**
     * Singleton
     */
    private final static NetExpressionFunctionSetHost<ExpressionEvaluationContext> INSTANCE = new NetExpressionFunctionSetHost<>();

    private NetExpressionFunctionSetHost() {
        super("setHost");
    }

    @Override
    public List<ExpressionFunctionParameter<?>> parameters(final int count) {
        return PARAMETERS;
    }

    private final static List<ExpressionFunctionParameter<?>> PARAMETERS = ExpressionFunctionParameter.list(
        HAS_HOST_ADDRESS,
        HAS_HOST_ADDRESS2
    );

    @Override
    public Class<HasHostAddress> returnType() {
        return HasHostAddress.class;
    }

    @Override
    public HasHostAddress apply(final List<Object> parameters,
                                final C context) {
        final HasHostAddress has = HAS_HOST_ADDRESS.getOrFail(parameters, 0, context);
        final HasHostAddress newHost = HAS_HOST_ADDRESS2.getOrFail(parameters, 1, context);

        System.out.println("parameters=" + parameters);
        System.out.println(has + " setHostAddress " + newHost + " " + newHost.hostAddress());

        return has.setHostAddress(
            newHost.hostAddress()
        );
    }
}
