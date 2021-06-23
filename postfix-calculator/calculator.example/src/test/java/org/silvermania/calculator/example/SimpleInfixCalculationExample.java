/*
 * File: SimpleInfixCalculationExample.java
 * Creation Date: Jul 1, 2019
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
package org.silvermania.calculator.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.silvermania.rpn.postfix.calculator.RPNCalculator;

/**
 * The class SimpleInfixCalculationExample demonstrates the fluent API of the
 * RPNCalculator and shows how to use a pre-configured instance, provide it with
 * a regular arithmetic infix expression to evaluate, while using all the
 * default registered operators, constants and functions.
 *
 * @author T.N.Silverman
 */
public class SimpleInfixCalculationExample extends BaseExampleTestCase {

    /**
     * Demonstrates how to provide an
     * {@link org.silvermania.rpn.postfix.calculator.RPNCalculator
     * org.silvermania.rpn.postfix.calculator.RPNCalculator} with a regular
     * {@code infix} arithmetic expression and calculate the result. This example
     * uses a RPNCalculator with the default calculation context.
     * <p>
     * The default {@link org.silvermania.rpn.support.CalculationContext
     * org.silvermania.rpn.support.CalculationContext} stores the registered math
     * operators, constants and functions. When it performs math operations on
     * {@link java.math.BigDecimal java.math.BigDecimal} instances, it internally
     * uses a {@link java.math.MathContext java.math.MathContext}, which by default
     * is a {@link java.math.MathContext#DECIMAL32 java.math.MathContext#DECIMAL32}.
     * This math context is configurable as later examples will demonstrate.
     * </p>
     * <p>
     * The following static configuration options are used in this example within
     * the expression:
     * </p>
     * <pre>
     * <b>BigDecimal result = RPNCalculator.withDefaults().convert(infix).thenCalculate();</b>
     * </pre>
     * Or, simply:
     * <pre>
     * <b>BigDecimal result = RPNCalculator.convert(infix).thenCalculate();</b>
     * </pre>
     * <p>
     * <b>{@code withDefault()}</b> optional method that returns a configurer, which
     * uses a default calculation context and allows for more configurations such as
     * convert()... For all intents and purposes when we do not wish to configure the
     * context, there's no need to call this method so we can proceed to
     * {@code convert(infix)} or {@code accept(postfix)} methods.
     * <p>
     * <b>{@code convert(infix)}</b> tells the configurer that the expression
     * provided is an infix and that it requires conversion to postfix. Then it
     * allows for more options, such as thenCalculate()...
     * </p>
     * <p>
     * <b>{@code thenCalculate()}</b> is a terminal instruction and it tells the
     * configurer to evaluate the expression (always postfix at this stage) and
     * return a big decimal result.
     * </p>
     *
     * @implNote behind the scene, the given {@code infix} is normalized into a
     *           space separated expression, then its converted to a {@code postfix}
     *           expression, and finally evaluated to provide a
     *           {@link java.math.BigDecimal} result.
     *           <p>
     *           In fact, if you enable DEBUG level logging, you could see the three
     *           stages involved in the evaluation of each infix expression.
     *           </p>
     *           <p>
     *           First, the given infix is normalize to a space separated
     *           expression, then it's converted to a postfix expression, and
     *           finally, it is evaluated:
     *           </p>
     *
     *           <pre>
     *          InfixNormalizer.normalize -&gt; normalized '1×2' to '1 × 2'
     *          InfixConverter.convert -&gt; converted infix '1 × 2' to postfix '1 2 ×'
     *          RPNCalculator.calculate -&gt; evaluated postfix '1 2 ×' to '2.0000000'
     *           </pre>
     *
     * @param infix the arithmetic expression (space separated, or not)
     * @param expected the expected big decimal representation of the result of the
     *        calculation of the given {@code infix} expression
     * @throws Exception if anything goes wrong
     */
    @ParameterizedTest
    @DisplayName("infix calculation example")
    // infix postfix and expected result are separated by pipe char '|'
    @CsvSource(delimiter = '|',
               value = {"2^2|4.0000000", //in the power of
                        "4!|24.0000000", //product
                        "√4|2.0000000", //square root
                        "1*2|2.0000000", // mult
                        "1×2|2.0000000", // × supported for mult
                        "2/1|2.0000000", // div
                        "2÷1|2.0000000", // ÷ supported for div
                        "4%3|1.0000000", // modulus
                        "1+2|3.0000000", // add
                        "1-2|-1.0000000", // subtract
                        "1−2|-1.0000000", // − supported for subtract
                        "π*2|6.2831853", // pi constant mult
                        "PI*2|6.2831853", // pi constant mult
                        "e*2|5.4365600", // Euler's number 2.71828D mult
                        "sin(90)|1.0000000", // sin function
                        "cos(90)|0.0000000", // cosin function
                        "tan(45)|1.0000000", // tangent function
                        "√400|20.0000000", // square root function
                        "min(1,4)|1.0000000", // minimum of function
                        "max(1,4)|4.0000000", // maximum of function
                        "avg(2,4)|3.0000000", // average of function
                        "pct(2,4)|50.0000000" // percent of function
               })
    public void infixCalculationExample(String infix, BigDecimal expected) throws Exception {
        BigDecimal actual = RPNCalculator.withDefaults().convert(infix).thenCalculate();
        assertEquals(expected, actual);
    }

    /**
     * Demonstrates how to provide an
     * {@link org.silvermania.rpn.postfix.calculator.RPNCalculator
     * org.silvermania.rpn.postfix.calculator.RPNCalculator} with a regular
     * {@code infix} arithmetic expression, calculate the result <b>and print the
     * calculation parameters for debugging purposes</b>. This example uses a
     * RPNCalculator with the default calculation context.
     * <p>
     * The default {@link org.silvermania.rpn.support.CalculationContext
     * org.silvermania.rpn.support.CalculationContext} stores the registered math
     * operators, constants and functions. When it performs math operations on
     * {@link java.math.BigDecimal java.math.BigDecimal} instances, it internally
     * uses a {@link java.math.MathContext java.math.MathContext}, which by default
     * is a {@link java.math.MathContext#DECIMAL32 java.math.MathContext#DECIMAL32}.
     * This math context is configurable as later examples will demonstrate.
     * </p>
     * <p>
     * The following configuration options are used in this example:
     * </p>
     *
     * <pre>
     * <b>BigDecimal result = RPNCalculator.withDefaults().convert(infix).doPrint().thenCalculate();</b>
     * </pre>
     *
     * Or simply:
     *
     * <pre>
     * <b>BigDecimal result = RPNCalculator.convert(infix).doPrint().thenCalculate();</b>
     * </pre>
     * <p>
     * <b>{@code withDefault()}</b> optional method that returns a configurer, which
     * uses a default calculation context and allows for more configurations such as
     * convert()... For all intents and purposes when we do not wish to configure the
     * context, there's no need to call this method so we can proceed to
     * {@code convert(infix)} or {@code accept(postfix)} methods.
     * <p>
     * <b>{@code convert(infix)}</b> tells the configurer that the expression
     * provided is an infix and that it requires conversion to postfix. Then it
     * allows for more options, such as thenCalculate() or, <b>doPrint()</b>...
     * </p>
     * <p>
     * <b>{@code doPrint()}</b> tells the configurer to print the configuration
     * parameters for debugging purposes. This will print the configuration
     * parameters just <b>before</b> the thenCalculate() operation and will show in
     * the console (with INFO logging level) as:
     * </p>
     *
     * <pre>
     * CALCULATION PARAMETERS
     * ----------------------
     * CONTEXT
     *   class             org.silvermania.rpn.support.CalculationContext
     *   mathContext       DECIMAL32 precision=7 roundingMode=HALF_EVEN
     *   constants         e,π,PI
     *   operators         ^,!,√,*,×,/,÷,%,+,−,-
     *   funtions          sin,cos,tan,min,max,avg,pct,sum,log
     * CALCULATOR
     *   class             org.silvermania.rpn.postfix.calculator.RPNCalculator
     *   context           customized
     * INFIX EXPRESSION    1×2
     * POSTFIX EXPRESSION  1 2 ×
     * RESULT              2.0000000
     * </pre>
     * <p>
     * <b>{@code thenCalculate()}</b> is a terminal instruction and it tells the
     * configurer to evaluate the expression (always postfix at this stage) and
     * return a big decimal result.
     * </p>
     *
     * @implNote behind the scene, the given {@code infix} is normalized into a
     *           space separated expression, then its converted to a {@code postfix}
     *           expression, and finally evaluated to provide a
     *           {@link java.math.BigDecimal} result.
     *           <p>
     *           In fact, if you enable DEBUG level logging, you could see the three
     *           stages involved in the evaluation of each infix expression.
     *           </p>
     *           <p>
     *           First, the given infix is normalize to a space separated
     *           expression, then it's converted to a postfix expression, and
     *           finally, it is evaluated:
     *           </p>
     *
     *           <pre>
     *          InfixNormalizer.normalize -&gt; normalized '1×2' to '1 × 2'
     *          InfixConverter.convert -&gt; converted infix '1 × 2' to postfix '1 2 ×'
     *          RPNCalculator.calculate -&gt; evaluated postfix '1 2 ×' to '2.0000000'
     *           </pre>
     *
     * @param infix the arithmetic expression (space separated, or not)
     * @param expected the expected big decimal representation of the result of the
     *        calculation of the given {@code infix} expression
     * @throws Exception if anything goes wrong
     */
    @ParameterizedTest
    @DisplayName("infix calculation with debug example")
    // infix postfix and expected result are separated by pipe char '|'
    @CsvSource(delimiter = '|',
               value = {"2^2|4.0000000", //in the power of
                        "4!|24.0000000", //product
                        "√4|2.0000000", //square root
                        "1*2|2.0000000", // mult
                        "1×2|2.0000000", // × supported for mult
                        "2/1|2.0000000", // div
                        "2÷1|2.0000000", // ÷ supported for div
                        "4%3|1.0000000", // modulus
                        "1+2|3.0000000", // add
                        "1-2|-1.0000000", // subtract
                        "1−2|-1.0000000", // − supported for subtract
                        "π*2|6.2831853", // pi constant mult
                        "PI*2|6.2831853", // pi constant mult
                        "e*2|5.4365600", // Euler's number 2.71828D mult
                        "sin(90)|1.0000000", // sin function
                        "cos(90)|0.0000000", // cosin function
                        "tan(45)|1.0000000", // tangent function
                        "√400|20.0000000", // square root function
                        "min(1,4)|1.0000000", // minimum of function
                        "max(1,4)|4.0000000", // maximum of function
                        "avg(2,4)|3.0000000", // average of function
                        "pct(2,4)|50.0000000" // percent of function
               })
    public void infixCalculationWithDebugExample(String infix, BigDecimal expected) throws Exception {
        BigDecimal actual = RPNCalculator.convert(infix).doPrint().thenCalculate();
        assertEquals(expected, actual);
    }
}
