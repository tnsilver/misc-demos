/*
 * File: RPNCalculator.java
 * Creation Date: Jul 6, 2019
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
package org.silvermania.rpn.postfix.calculator;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.stream.Collectors;

import org.silvermania.rpn.postfix.api.CalculationConfigurer;
import org.silvermania.rpn.postfix.api.CalculatorState;
import org.silvermania.rpn.postfix.api.ExpressionConfigurer;
import org.silvermania.rpn.postfix.support.ExpressionConfigurerImpl;
import org.silvermania.rpn.postfix.support.PostfixTokenHandler;
import org.silvermania.rpn.support.CalculationContext;
import org.silvermania.rpn.support.Printable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class RPNCalculator is a reversed polish notation ({@code postfix})
 * postfix RPNCalculator.
 * <p>
 * It's input is a space separated {@code RPN} postfix that's evaluated left to
 * right.
 * <p>
 * The basic algorithm is as follows:
 *
 * <pre>
 * 1) Create a stack to store operands (or values).
 * 2) Scan the given postfix and do following for every scanned element.
 *   .a) If the element is a number, push it into the stack
 *   .b) If the element is a operator, pop operands for the operator from stack.
 *       Evaluate the operator and push the result back to the stack
 * 3) When the postfix is ended, the number in the stack is the final answer
 * </pre>
 *
 * @author T.N.Silverman
 */
public final class RPNCalculator implements Printable {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(RPNCalculator.class);

    /** The default instance of a CalculationContext. */
    public static final CalculationContext DEFAULTS = CalculationContext.newInstance();

    /** The context. */
    private CalculationContext context;

    /**
     * hidden constructor Instantiates a new {@code RPNCalculator}.
     *
     * @param context the calculation context on which to locate the meaning of
     *        functions, operators, constants and to obtain information about
     *        rounding modes and rounding decimal places
     */
    private RPNCalculator(final CalculationContext context) {
        super();
        this.context = context;
    }

    /**
     * factory method obtaining a new instance of this {@code RPNCalculator}.
     *
     * @param context the calculation context on which to locate the meaning of
     *        functions, operators, constants and to obtain information about
     *        rounding modes and rounding decimal places
     * @return new instance of {@code RPNCalculator}
     * @apiNote blocked visibility in favor of fluent API
     */
    protected static RPNCalculator newInstance(CalculationContext context) {
        return new RPNCalculator(context);
    }

    /**
     * the method calculate accepts a reversed polish notation postfix and evaluates
     * it to an arithmetic postfix (calculation) which is returned as a
     * {@link CharSequence}.
     * <p>
     * For convenience, the
     * {@link org.silvermania.rpn.infix.converter.InfixConverter} can convert a
     * space separated infix postfix to a postfix postfix, so that it can be served
     * as input to this method.
     * <p>
     * The basic algorithm is as follows:
     *
     * <pre>
     * 1) Create a stack to store operands (or values).
     * 2) Scan the given postfix and do following for every scanned element.
     *   .a) If the element is a number, push it into the stack
     *   .b) If the element is a operator, pop operands for the operator from stack.
     *       Evaluate the operator and push the result back to the stack
     * 3) When the postfix is ended, the number in the stack is the final answer
     * </pre>
     *
     * @param postfix the postfix postfix
     * @return a {@link CharSequence} representing the evaluation of the postfix
     *         postfix
     * @throws IllegalArgumentException if the {@code postfix} postfix is null or
     *         blank
     */
    public BigDecimal calculate(String postfix) {
        if (null == postfix || postfix.isBlank()) {
            throw new IllegalArgumentException("postfix postfix cannot be null or empty!");
        }
        Stack<BigDecimal> stack = new Stack<>();
        PostfixTokenHandler handler = PostfixTokenHandler.newInstance(context);
        try (Scanner scanner = new Scanner(postfix)) {
            scanner.useDelimiter(" ");
            while (scanner.hasNext()) {
                handler.handle(scanner.next(), stack);
            }
        }
        BigDecimal result = stack.pop();
        logger.debug("evaluated postfix '{}' to '{}'", postfix, result);
        return result;
    }

    /* +++++++++++++++++ fluent +++++++++++++++++++ */

    /**
     * begins the fluent API configuration and allows to either obtain a
     * pre-configured RPNCalculator, or further augment it with a custom
     * {@link CalculationContext context}.
     *
     * @param context the context
     * @return an instance of InitializerConfigurerImpl to begin the configuration
     */
    public static ExpressionConfigurer withContext(CalculationContext context) {
        return new ExpressionConfigurerImpl(new CalculatorState(context));
    }

    /**
     * continues the fluent API configuration with a pre-configured RPNCalculator.
     *
     * @return an instance of {@link ExpressionConfigurerImpl} to configure the
     *         {@code infix} or {@code postfix} postfix for the RPNCalculator to
     *         evaluate.
     */
    public static ExpressionConfigurer withDefaults() {
        return new ExpressionConfigurerImpl(new CalculatorState());
    }

    /**
     * A shortcut method in the fluent API to convert an {@code infix} expression to
     * postfix and return a {@link CalculationConfigurer} for further calculation.
     *
     * @param infix the infix expression
     * @return the calculation configurer for further calculations
     */
    public static CalculationConfigurer convert(CharSequence infix) {
        ExpressionConfigurer configurer = new ExpressionConfigurerImpl(new CalculatorState());
        return configurer.convert(infix);
    }

    /**
     * A shortcut method in the fluent API to accept a {@code posfix} expression and
     * return a {@link CalculationConfigurer} for further calculation.
     *
     * @param postfix the postfix expression
     * @return the calculation configurer for further calculations
     */
    public static CalculationConfigurer accept(CharSequence postfix) {
        ExpressionConfigurer configurer = new ExpressionConfigurerImpl(new CalculatorState());
        return configurer.accept(postfix);
    }

    /**
     * Prints the.
     *
     * @return the string
     */
    /*
     * (non-Javadoc)
     *
     * @see org.silvermania.rpn.support.Printable#print()
     */
    @Override
    public String print() {
        Map<String, String> props = new LinkedHashMap<>();
        props.put(indent() + "class", "org.silvermania.rpn.postfix.calculator.RPNCalculator");
        props.put(indent() + "context", context.print().equals(DEFAULTS.print()) ? "default" : "customized");
        return props.entrySet().stream().map(e -> String.format("%n%-20s%s", e.getKey(), e.getValue()))
                .collect(Collectors.joining());

    }

    /**
     * To string.
     *
     * @return the string
     */
    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "RPNCalculator [context=" + context + "]";
    }

}
