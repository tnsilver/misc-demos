/*
 * File: CalculatorState.java
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
package org.silvermania.rpn.postfix.api;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.silvermania.rpn.postfix.calculator.RPNCalculator;
import org.silvermania.rpn.support.CalculationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class CalculatorState is the internal state of this configurer and
 * contains a reference to a {@code RPNCalculator}, and a
 * {@code CalculationContext} which configures it, a reference to a
 * {@link org.silvermania.rpn.infix.converter.InfixConverter} (in case the supplied
 * expression is an {@code infix} expression) and a string {@code postfix} to be evaluated and
 * calculated by the calculator.
 *
 * @author T.N.Silverman
 */
public class CalculatorState implements Serializable {

    /** The Constant logger. */
    protected static final Logger logger =
        LoggerFactory.getLogger(CalculatorState.class);

    /** The calculator. */
    private RPNCalculator calculator;

    /** The context. */
    private CalculationContext context;

    /** The postfix. */
    private String postfix;

    /** The infix postfix. */
    private String infix;

    /** The printing. */
    private boolean printing;

    /** the message buffer. */
    private Map<String, String> messageBuffer;

    /**
     * Instantiates a new calculator state.
     *
     * @param context the calculation context to be applied to the state
     */
    public CalculatorState(final CalculationContext context) {
        super();
        messageBuffer = new LinkedHashMap<>();
        messageBuffer.put("", "");
        messageBuffer.put("CALCULATION PARAMETERS", "");
        messageBuffer.put("----------------------", "");
        this.context = context; // IMPORTANT: do not use setter here
        messageBuffer.put("CONTEXT", getContext().print());
        calculator = initCalculator();
        messageBuffer.put("CALCULATOR", getCalculator().print());
    }

    /**
     * Instantiates a CalculatorState with default calculation context.
     */
    public CalculatorState() {
        super();
        messageBuffer = new LinkedHashMap<>();
        messageBuffer.put("", "");
        messageBuffer.put("CALCULATION PARAMETERS", "");
        messageBuffer.put("----------------------", "");
        this.context = CalculationContext.newInstance(); // IMPORTANT: do not use setter here
        messageBuffer.put("CONTEXT", getContext().print());
        calculator = initCalculator();
        messageBuffer.put("CALCULATOR", getCalculator().print());
    }

    /**
     * Gets the calculator.
     *
     * @return this state's calculator
     */
    public RPNCalculator getCalculator() {
        return calculator;
    }

    /**
     * Sets the calculator.
     *
     * @param calculator the new calculator
     */
    public void setCalculator(RPNCalculator calculator) {
        this.calculator = calculator;
        messageBuffer.put("CALCULATOR", getCalculator().print());
    }

    /**
     * Gets the calculation context.
     *
     * @return this state's calculation context
     */
    public CalculationContext getContext() {
        return context;
    }

    /**
     * Sets the calculation context.
     *
     * @param context the new calculation context
     */
    public void setContext(CalculationContext context) {
        this.context = context;
        messageBuffer.remove("CONTEXT");
        messageBuffer.put("CUSTOM CONTEXT", getContext().print());
    }

    /**
     * Gets this state's postfix expression.
     *
     * @return this state's postfix expression
     */
    public String getPostfix() {
        return postfix;
    }

    /**
     * Sets this state's postfix.
     *
     * @param postfix the new postfix expression
     */
    public void setPostfix(String postfix) {
        this.postfix = postfix;
        messageBuffer.put("POSTFIX EXPRESSION", getPostfix());
    }

    /**
     * Gets this state's infix expression.
     *
     * @return this state's infix expression
     */
    public String getInfix() {
        return infix;
    }

    /**
     * Sets this state's infix expression.
     *
     * @param infix the new infix expression
     */
    public void setInfix(String infix) {
        this.infix = infix;
        messageBuffer.put("INFIX EXPRESSION", getInfix());
    }

    /**
     * Checks if this state's print flag was set to true
     *
     * @return true, if is printing flag was set to true, otherwise false
     */
    public boolean isPrinting() {
        return printing;
    }

    /**
     * Sets this state's printing flag.
     *
     * @param printing the new printing
     */
    public void setPrinting(boolean printing) {
        this.printing = printing;
    }

    /**
     * Performs an evaluation of this state's {@code postfix} expression using
     * the accumulated state properties. These include the postfix expression,
     * an {@code calculator} configured with this state's
     * {@code CalculationContext}. The state may also include an infix
     * expression that was converted to the postfix expression. Possibly, the
     * state may also have a {@code true} printing flag, in which case it will
     * print it's properties to the console (INFO logging level) for debugging
     * purposes.
     *
     * @return the result of the calculation
     */
    public BigDecimal calculate() {
        BigDecimal result = getCalculator().calculate(getPostfix());
        messageBuffer.put("RESULT", result.toString() + "\n");
        print();
        return result;
    }

    /**
     * Prints the state's properties to the console (INFO logging level).
     */
    private void print() {
        if (!isPrinting()) {
            return;
        }
        String message = messageBuffer.entrySet().stream()
                .map(e -> String.format("%n%-20s%s", e.getKey(), e.getValue()))
                .collect(Collectors.joining());
        logger.info(message);
    }

    /**
     * Reflectively obtain a reference to a {@link calculator}. Since the
     * {@code getInstance()} method and constructor of the calculator is
     * hidden for encapsulation reasons, we use reflection.
     *
     * @return an instance of the calculator configured with this state's
     *         {@code calculation context}
     *
     * @throws RuntimeException if the reflective instantiation fails for
     *         security, access or class loading reasons.
     */
    protected RPNCalculator initCalculator() {
        try {
            Constructor<RPNCalculator> constructor =
                RPNCalculator.class.getDeclaredConstructor(
                        new Class<?>[]{CalculationContext.class});
            constructor.setAccessible(true);
            return constructor.newInstance(new Object[]{getContext()});
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

}
