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
import java.math.RoundingMode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.silvermania.rpn.postfix.calculator.RPNCalculator;
import org.silvermania.rpn.support.CalculationContext;

/**
 * The class CustomizingInfixCalculationExample demonstrates the fluent API of
 * the {@link org.silvermania.rpn.postfix.calculator.RPNCalculator
 * org.silvermania.rpn.postfix.calculator.RPNCalculator} and shows how to use an
 * RPNCalculator instance with a custom calculation context.
 *
 * @author T.N.Silverman
 */
public class CustomizingInfixCalculationExample extends BaseExampleTestCase {

    /**
     * Demonstrates how to fully customize a
     * {@link org.silvermania.rpn.support.CalculationContext
     * org.silvermania.rpn.support.CalculationContext} and configure an
     * {@link org.silvermania.rpn.postfix.calculator.RPNCalculator
     * org.silvermania.rpn.postfix.calculator.RPNCalculator} to use the
     * <b>customized</b> calculation context.
     * <p>
     * The default {@link org.silvermania.rpn.support.CalculationContext
     * org.silvermania.rpn.support.CalculationContext} stores the registered math
     * operators, constants and functions. When it performs math operations on
     * {@link java.math.BigDecimal java.math.BigDecimal} instances, it internally
     * uses a {@link java.math.MathContext java.math.MathContext}, which by default
     * is a {@link java.math.MathContext#DECIMAL32 java.math.MathContext#DECIMAL32}.
     * This math context is configurable as demonstrated here:
     * </p>
     * <p>
     * The following configuration options are used in this example:
     * </p>
     *
     * <pre>
     * <b>
     * CalculationContext context = CalculationContext.DECIMAL64_CONTEXT;
     * BigDecimal result = RPNCalculator.withContext(context).convert(infix).doPrint().thenCalculate();
     * </b>
     * </pre>
     * <p>
     * <b>{@code CalculationContext.DECIMAL64_CONTEXT}</b> is a static calculation
     * context defined in the CalculationContext class with extended 16 decimal
     * places precision and RoundingMode of HALF_EVEN. Here, we just assign the
     * static value to our context reference.
     * </p>
     * <p>
     * <b>{@code withContext(context)}</b> tells the configurer to set the given
     * calculation context on it's calculator, and allows to continue configuration
     * operations such as convert(infix)...
     * </p>
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
     *   mathContext       DECIMAL64 precision=16 roundingMode=HALF_EVEN
     *   constants         e,PI,π
     *   operators         ^,!,√,*,×,/,÷,%,+,−,-
     *   funtions          sin,cos,tan,min,max,avg,pct,sum,log
     * CALCULATOR
     *   class             org.silvermania.rpn.postfix.calculator.RPNCalculator
     *   context           customized
     * INFIX EXPRESSION    1*2
     * POSTFIX EXPRESSION  1 2 *
     * RESULT              2.0000000000000000
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
     *          RPNCalculator.calculate -&gt; evaluated postfix '1 2 ×' to '2.0000000000000000'
     *           </pre>
     *
     * @param infix the arithmetic expression (space separated, or not)
     * @param expected the expected big decimal representation of the result of the
     *        calculation of the given {@code infix} expression
     * @throws Exception if anything goes wrong
     */
    @ParameterizedTest
    @DisplayName("test customized infix calculation example")
    //@Disabled
    // infix postfix and expected result are separated by pipe char '|'
    @CsvSource(delimiter = '|',
               value = {"2^2|4.0000000000000000", //in the power of
                        "4!|24.0000000000000000", //product
                        "√4|2.0000000000000000", //square root
                        "1*2|2.0000000000000000", // mult
                        "1×2|2.0000000000000000", // × supported for mult
                        "2/1|2.0000000000000000", // div
                        "2÷1|2.0000000000000000", // ÷ supported for div
                        "4%3|1.0000000000000000", // modulus
                        "1+2|3.0000000000000000", // add
                        "1-2|-1.0000000000000000", // subtract
                        "1−2|-1.0000000000000000", // − supported for subtract
                        "π*2|6.2831853071795862", // pi constant mult
                        "PI*2|6.2831853071795862", // pi constant mult
                        "e*2|5.4365600000000001", // Euler's number 2.71828D mult
                        "sin(90)|1.0000000000000000", // sin function
                        "cos(90)|1E-16", // cosin function
                        "tan(45)|0.9999999999999999", // tangent function
                        "√400|20.0000000000000000", // square root function
                        "min(1,4)|1.0000000000000000", // minimum of function
                        "max(1,4)|4.0000000000000000", // maximum of function
                        "avg(2,4)|3.0000000000000000", // average of function
                        "pct(2,4)|50.0000000000000000" // percent of function
               })
    public void customizedInfixCalculationExample(String infix, BigDecimal expected) throws Exception {
        context = CalculationContext.DECIMAL64_CONTEXT;
        BigDecimal actual = RPNCalculator.withContext(context).convert(infix).doPrint().thenCalculate();
        assertEquals(expected, actual);
    }

    /**
     * Demonstrates how to partially customize a
     * {@link org.silvermania.rpn.support.CalculationContext
     * org.silvermania.rpn.support.CalculationContext} and configure an
     * {@link org.silvermania.rpn.postfix.calculator.RPNCalculator
     * org.silvermania.rpn.postfix.calculator.RPNCalculator} to use the
     * <b>customized</b> calculation context.
     * <p>
     * The default {@link org.silvermania.rpn.support.CalculationContext
     * org.silvermania.rpn.support.CalculationContext} stores the registered math
     * operators, constants and functions. When it performs math operations on
     * {@link java.math.BigDecimal java.math.BigDecimal} instances, it internally
     * uses a {@link java.math.MathContext java.math.MathContext}, which by default
     * is a {@link java.math.MathContext#DECIMAL32 java.math.MathContext#DECIMAL32}
     * with 7 decimal places percision and rounding mode of HALF_EVEN. These
     * parameters can be changed and the math context is configurable as
     * demonstrated here:
     * </p>
     * <p>
     * The following configuration options are used in this example:
     * </p>
     *
     * <pre>
     * <b>
     * CalculationContext context = CalculationContext.newInstance();
     * context.setRoundingMode(RoundingMode.HALF_UP);
     * context.setPrecision(2);
     * BigDecimal result = RPNCalculator.withContext(context).convert(infix).doPrint().thenCalculate();
     * </b>
     * </pre>
     * <p>
     * <b>{@code withContext(context)}</b> tells the configurer to set the given
     * calculation context on it's calculator, and allows to continue configuration
     * operations such as convert(infix)...
     * </p>
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
     *   mathContext       CUSTOM precision=2 roundingMode=HALF_UP
     *   constants         e,PI,π
     *   operators         ^,!,√,*,×,/,÷,%,+,−,-
     *   funtions          sin,cos,tan,min,max,avg,pct,sum,log
     * CALCULATOR
     *   class             org.silvermania.rpn.postfix.calculator.RPNCalculator
     *   context           customized
     * INFIX EXPRESSION    1*2
     * POSTFIX EXPRESSION  1 2 *
     * RESULT              2.00
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
     *          RPNCalculator.calculate -&gt; evaluated postfix '1 2 ×' to '2.00'
     *           </pre>
     *
     * @param infix the arithmetic expression (space separated, or not)
     * @param expected the expected big decimal representation of the result of the
     *        calculation of the given {@code infix} expression
     * @throws Exception if anything goes wrong
     */
    @ParameterizedTest
    @DisplayName("partially customized infix calculation example")
    //@Disabled
    // infix postfix and expected result are separated by pipe char '|'
    @CsvSource(delimiter = '|',
               value = {"2^2|4.00", //in the power of
                        "4!|24.00", //product
                        "√4|2.00", //square root
                        "1*2|2.00", // mult
                        "1×2|2.00", // × supported for mult
                        "2/1|2.00", // div
                        "2÷1|2.00", // ÷ supported for div
                        "4%3|1.00", // modulus
                        "1+2|3.00", // add
                        "1-2|-1.00", // subtract
                        "1−2|-1.00", // − supported for subtract
                        "π*2|6.28", // pi constant mult
                        "PI*2|6.28", // pi constant mult
                        "e*2|5.44", // Euler's number 2.71828D mult
                        "sin(90)|1.00", // sin function
                        "cos(90)|0.00", // cosin function
                        "tan(45)|1.00", // tangent function
                        "√400|20.00", // square root function
                        "min(1,4)|1.00", // minimum of function
                        "max(1,4)|4.00", // maximum of function
                        "avg(2,4)|3.00", // average of function
                        "pct(2,4)|50.00", // percent of function
               })
    public void partiallyCustomizedInfixCalculationExample(String infix, BigDecimal expected) throws Exception {
        // this won't affect any of these simple calculations
        context.setRoundingMode(RoundingMode.HALF_UP);
        // this will set the precision of decimal places to 2
        context.setPrecision(2);
        BigDecimal actual = RPNCalculator.withContext(context).convert(infix).doPrint().thenCalculate();
        assertEquals(expected, actual);
    }

}
