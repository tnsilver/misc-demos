/*
 * File: ExpressionConfigurer.java
 * Creation Date: Jun 22, 2019
 *
 * Copyright (c) 2019 T.N.Silverman - all rights reserved
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions
 * and limitations under the License.
 */
package org.silvermania.rpn.postfix.api;

/**
 * The interface ExpressionConfigurer configures either an {@code infix}
 * expression or a {@code postfix} reversed polish notation expression for the
 * RPNCalculator to process.
 *
 * @author T.N.Silverman
 */
public interface ExpressionConfigurer extends Configurer {

    /**
     * Supplies an {@code infix} expression (such as <b>&quot;2*3&quot;</b> or
     * <b>&quot;sin(max(2,3)÷3×π)&quot;</b>) to the RPNCalculator for conversion to
     * {@code postfix}.
     *
     * @apiNote {@code infix expressions} need not be space separated and can
     *          contain spaces between the operands, operators, function argument
     *          separator (comma) or parentheses and brackets, but <b>not</b> in
     *          between function names.
     *
     * @param infix the {@code infix} expression to calculate.
     * @return a reference to a {@link CalculationConfigurer} implementation for
     *         further configuration and chaining of operations
     */
    CalculationConfigurer convert(CharSequence infix);

    /**
     * Supplies an {@code postfix} expression (such as <b>&quot;23*&quot;</b> or
     * <b>&quot;2 3 max 3 ÷ π × sin&quot;</b>) for the RPNCalculator to process.
     *
     * @apiNote {@code postfix} expressions must be space separated (e.g. <b>2 3 max
     *          3 ÷ π × sin</b>)
     *
     * @param postfix the {@code postfix} expression to process.
     * @return a reference to a ({@link CalculationConfigurer}) for further
     *         configuration and chaining of operations
     */
    CalculationConfigurer accept(CharSequence postfix);

}
