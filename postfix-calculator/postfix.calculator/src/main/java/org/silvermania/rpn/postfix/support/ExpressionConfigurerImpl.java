/*
 * File: ExpressionConfigurerImpl.java
 * Creation Date: Jul 2, 2019
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
package org.silvermania.rpn.postfix.support;

import org.silvermania.rpn.infix.converter.InfixConverter;
import org.silvermania.rpn.postfix.api.CalculationConfigurer;
import org.silvermania.rpn.postfix.api.CalculatorState;
import org.silvermania.rpn.postfix.api.ExpressionConfigurer;

/**
 * The class ExpressionConfigurerImpl configures either a {@code infix} - or
 * {@code postfix} expressions for the RPNCalculator to process.
 * <p>
 * In case of a {@code infix} expressions, an extra step is required to convert
 * the expression to a {@code postifx}.
 * <p>
 * This is performed by letting a designated {@code InfixConverter} convert the
 * {@code infix} expression into {@code postfix} and passing it to the
 * configurer's {@link CalculatorState} to be evaluated.
 *
 * @author T.N.Silverman
 */
public class ExpressionConfigurerImpl extends AbstractConfigurerImpl implements ExpressionConfigurer {

    /**
     * Instantiates a new expression configurer impl.
     *
     * @param calculatorState the state
     */
    public ExpressionConfigurerImpl(final CalculatorState calculatorState) {
        super(calculatorState);
    }

    /**
     * supplies an {@code infix} expression (such as <b>&quot;2*3&quot;</b> or
     * <b>&quot;sin(max(2,3)÷3×π)&quot;</b>) to the RPNCalculator for conversion to
     * {@code postfix}.
     *
     * @param infix the {@code infix} expression.
     * @return a reference to the appropriate {@link CalculationConfigurerImpl} for
     *         further configuration and chaining of operations
     * @apiNote {@code infix expressions} need not be space separated and can
     *          contain spaces between the operands, operators, function argument
     *          separator (comma) or praentheses and brackets, but <b>not</b> in
     *          between function names.
     */
    @Override
    public CalculationConfigurer convert(CharSequence infix) {
        CalculatorState calculatorState = getState();
        calculatorState.setInfix(infix.toString());
        calculatorState.setPostfix(
                InfixConverter.newInstance(calculatorState.getContext()).convert(calculatorState.getInfix()));
        CalculationConfigurerImpl configurer = new CalculationConfigurerImpl(calculatorState);
        return configurer;
    }

    /**
     * supplies an {@code postfix} postfix (reversed polish notation style, such as
     * <b>&quot;23*&quot;</b> or <b>&quot;2 3 max 3 ÷ π × sin&quot;</b>) to the
     * RPNCalculator for calculation.
     *
     * @param postfix the {@code postfix} postfix to calculate.
     * @return a reference to the appropriate configurer,
     *         ({@link CalculationConfigurerImpl}) in this case, for further
     *         configuration and chaining of operations
     * @apiNote {@code postfix expressions} must be space separated (e.g. <b>2 3 max
     *          3 ÷ π × sin</b>)
     */
    @Override
    public CalculationConfigurer accept(CharSequence postfix) {
        CalculatorState calculatorState = getState();
        calculatorState.setPostfix(postfix.toString());
        CalculationConfigurerImpl configurer = new CalculationConfigurerImpl(calculatorState);
        return configurer;
    }

}
