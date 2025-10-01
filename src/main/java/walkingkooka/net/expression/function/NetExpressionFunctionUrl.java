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
import walkingkooka.net.Url;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;

import java.util.List;

/**
 * A function that may be used to get a url including parsing text into a {@link Url}.
 */
final class NetExpressionFunctionUrl<C extends ExpressionEvaluationContext> extends NetExpressionFunction<Url, C> {

    static <C extends ExpressionEvaluationContext> NetExpressionFunctionUrl<C> instance() {
        return Cast.to(INSTANCE);
    }

    /**
     * Singleton
     */
    private final static NetExpressionFunctionUrl<ExpressionEvaluationContext> INSTANCE = new NetExpressionFunctionUrl<>();

    private NetExpressionFunctionUrl() {
        super("url");
    }

    @Override
    public List<ExpressionFunctionParameter<?>> parameters(final int count) {
        return PARAMETERS;
    }

    private final static List<ExpressionFunctionParameter<?>> PARAMETERS = ExpressionFunctionParameter.list(
        URL
    );

    @Override
    public Class<Url> returnType() {
        return Url.class;
    }

    @Override
    public Url apply(final List<Object> parameters,
                     final C context) {
        return URL.getOrFail(parameters, 0);
    }
}
