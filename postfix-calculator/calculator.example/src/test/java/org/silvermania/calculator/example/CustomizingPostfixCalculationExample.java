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
 * The class CustomizingPostfixCalculationExample demonstrates the fluent API of
 * the RPNCalculator and shows how to use an RPNCalculator instance with a
 * custom calculation context, provide it with a postfix expression to evaluate,
 * while using all the default registered operators, constants and functions.
 *
 * @author T.N.Silverman
 */
public class CustomizingPostfixCalculationExample extends BaseExampleTestCase {

    /**
     * Demonstrates how to provide a customized
     * {@link org.silvermania.rpn.postfix.calculator.RPNCalculator
     * org.silvermania.rpn.postfix.calculator.RPNCalculator} with
     * {@code postfix} expression and calculate the result. This example uses a
     * RPNCalculator with a <b>customized</b> calculation context.
     * <p>
     * The default {@link org.silvermania.rpn.support.CalculationContext
     * org.silvermania.rpn.support.CalculationContext} stores the registered
     * math operators, constants and functions. When it performs math operations
     * on {@link java.math.BigDecimal java.math.BigDecimal} instances, it
     * internally uses a {@link java.math.MathContext java.math.MathContext},
     * which by default is a {@link java.math.MathContext#DECIMAL32
     * java.math.MathContext#DECIMAL32}.
     * <p>
     * This example uses an RPNCalculator with a custom calculation context
     * which is equivalent, in math parameters, to
     * {@link java.math.MathContext#DECIMAL64} with 16 decimal places precision
     * and HALF_EVEN rounding mode. This math context is configurable as
     * demonstrated here:
     * </p>
     * <p>
     * The following static configuration options are used in this example
     * within the expression:
     * </p>
     *
     * <pre>
     * <b>
     * CalculationContext context = CalculationContext.DECIMAL64_CONTEXT;
     * BigDecimal actual = RPNCalculator.withContext(context).accept(postfix).doPrint().thenCalculate();
     * </b>
     * </pre>
     * <p>
     * <b>{@code withContext(context)}</b> tells the configurer to set the given
     * calculation context on it's calculator, and allows to continue
     * configuration operations such as accept(postfix)...
     * </p>
     * <p>
     * <b>{@code accept(postfix)}</b> tells the configurer that the expression
     * provided is a valid postfix expression and that it can be evaluated, and
     * it also allows for final configuration options, such as thenCalculate()
     * or, <b>doPrint()</b>...
     * </p>
     * <p>
     * <b>{@code doPrint()}</b> tells the configurer to print the configuration
     * parameters for debugging purposes. This will print the configuration
     * parameters just <b>before</b> the thenCalculate() operation and will show
     * in the console (with INFO logging level) as:
     * </p>
     *
     * <pre>
     * CALCULATION PARAMETERS
     * ----------------------
     * CALCULATOR
     *   class             org.silvermania.rpn.postfix.calculator.RPNCalculator
     *   context           customized
     * CUSTOM CONTEXT
     *   class             org.silvermania.rpn.support.CalculationContext
     *   mathContext       DECIMAL64 precision=16 roundingMode=HALF_EVEN
     *   constants         PI,e,π
     *   operators         ^,!,√,*,×,/,÷,%,mod,+,−,-
     *   funtions          sin,cos,tan,sqrt,min,max,avg,pct,pow,sum
     * POSTFIX EXPRESSION  1 2 ×
     * RESULT              2.0000000000000000
     * </pre>
     * <p>
     * <b>{@code thenCalculate()}</b> is a terminal instruction and it tells the
     * configurer to evaluate the expression (always postfix at this stage) and
     * return a big decimal result.
     * </p>
     *
     * @implNote providing a postfix expression to the RPNCalculator skips the
     *           normalization and conversion stages that are applied in case of
     *           an input infix expression. In fact, if you enable DEBUG level
     *           logging, you could see the single stage involved:
     *
     *           <pre>
     *          RPNCalculator.calculate -&gt; evaluated postfix '1 2 ×' to '2.0000000000000000'
     *           </pre>
     *
     * @param postfix the reversed polish notation expression (must be space
     *        separated)
     * @param expected the expected big decimal representation of the result of
     *        calculation of the given {@code postfix} expression
     * @throws Exception if anything goes wrong
     *
     * @author T.N.Silverman
     */
    @ParameterizedTest
    @DisplayName("test customized postfix calculation")
    //@Disabled
    // postfix and expected result are separated by pipe char '|'
    @CsvSource(delimiter = '|',
               value = {"2 2 ^|4.0000000000000000", //in the power of
                        "4 !|24.0000000000000000", //product
                        "4 √|2.0000000000000000", //square root
                        "1 2 *|2.0000000000000000", // mult
                        "1 2 ×|2.0000000000000000", // × supported for mult
                        "2 1 /|2.0000000000000000", // div
                        "2 1 ÷|2.0000000000000000", // ÷ supported for div
                        "4 3 %|1.0000000000000000", // modulus
                        "1 2 +|3.0000000000000000", // add
                        "1 2 -|-1.0000000000000000", // subtract
                        "1 2 −|-1.0000000000000000", // − supported for subtract
                        "π 2 *|6.2831853071795862", // pi constant mult
                        "PI 2 *|6.2831853071795862", // pi constant mult
                        "e 2 *|5.4365600000000001", // Euler's number 2.71828D mult
                        "90 sin|1.0000000000000000", // sin function
                        "90 cos|1E-16", // cosin function
                        "45 tan|0.9999999999999999", // tangent function
                        "400 √|20.0000000000000000", // square root function
                        "1 4 min|1.0000000000000000", // minimum of function
                        "1 4 max|4.0000000000000000", // maximum of function
                        "2 4 avg|3.0000000000000000", // average of function
                        "2 4 pct|50.0000000000000000" // percent of function

               })
    public void customizedPostfixCalculation(String postfix,
            BigDecimal expected) throws Exception {
        context = CalculationContext.DECIMAL64_CONTEXT;
        BigDecimal actual = RPNCalculator.withContext(context).accept(postfix)
                .doPrint().thenCalculate();
        assertEquals(expected, actual);
    }

    /**
     * Demonstrates how to provide a <b>partially</b> customized
     * {@link org.silvermania.rpn.postfix.calculator.RPNCalculator
     * org.silvermania.rpn.postfix.calculator.RPNCalculator} with
     * {@code postfix} expression and calculate the result. This example uses an
     * RPNCalculator with a <b>custom</b> calculation context which is assigned
     * a decimal precision of 2 and a rounding mode of HALF_UP.
     * <p>
     * The default {@link org.silvermania.rpn.support.CalculationContext
     * org.silvermania.rpn.support.CalculationContext} stores the registered
     * math operators, constants and functions. When it performs math operations
     * on {@link java.math.BigDecimal java.math.BigDecimal} instances, it
     * internally uses a {@link java.math.MathContext java.math.MathContext},
     * which by default is a {@link java.math.MathContext#DECIMAL32
     * java.math.MathContext#DECIMAL32}.
     * <p>
     * This example uses an RPNCalculator with a custom calculation context
     * which is equivalent, in math parameters, to
     * {@link java.math.MathContext#DECIMAL64} with 16 decimal places precision
     * and HALF_EVEN rounding mode. This math context is configurable as
     * demonstrated here:
     * </p>
     * <p>
     * The following static configuration options are used in this example
     * within the expression:
     * </p>
     *
     * <pre>
     * <b>
     * CalculationContext context = CalculationContext.newInstance();
     * context.setRoundingMode(RoundingMode.HALF_UP);
     * context.setPrecision(2);
     * BigDecimal actual = RPNCalculator.withContext(context).accept(postfix).doPrint().thenCalculate();
     * </b>
     * </pre>
     * <p>
     * <b>{@code withContext(context)}</b> tells the configurer to set the given
     * calculation context on it's calculator, and allows to continue
     * configuration operations such as accept(postfix)...
     * </p>
     * <p>
     * <b>{@code accept(postfix)}</b> tells the configurer that the expression
     * provided is a valid postfix expression and that it can be evaluated, and
     * it also allows for final configuration options, such as thenCalculate()
     * or, <b>doPrint()</b>...
     * </p>
     * <p>
     * <b>{@code doPrint()}</b> tells the configurer to print the configuration
     * parameters for debugging purposes. This will print the configuration
     * parameters just <b>before</b> the thenCalculate() operation and will show
     * in the console (with INFO logging level) as:
     * </p>
     *
     * <pre>
     * CALCULATION PARAMETERS
     * ----------------------
     * CALCULATOR
     *   class             org.silvermania.rpn.postfix.calculator.RPNCalculator
     *   context           customized
     * CUSTOM CONTEXT
     *   class             org.silvermania.rpn.support.CalculationContext
     *   mathContext       CUSTOM precision=2 roundingMode=HALF_UP
     *   constants         PI,e,π
     *   operators         ^,!,√,*,×,/,÷,%,mod,+,−,-
     *   funtions          sin,cos,tan,sqrt,min,max,avg,pct,pow,sum
     * POSTFIX EXPRESSION  1 2 ×
     * RESULT              2.00
     * </pre>
     * <p>
     * <b>{@code thenCalculate()}</b> is a terminal instruction and it tells the
     * configurer to evaluate the expression (always postfix at this stage) and
     * return a big decimal result.
     * </p>
     *
     * @implNote providing a postfix expression to the RPNCalculator skips the
     *           normalization and conversion stages that are applied in case of
     *           an input infix expression. In fact, if you enable DEBUG level
     *           logging, you could see the single stage involved:
     *
     *           <pre>
     *          RPNCalculator.calculate -&gt; evaluated postfix '1 2 ×' to '2.0000000000000000'
     *           </pre>
     *
     * @param postfix the reversed polish notation expression (must be space
     *        separated)
     * @param expected the expected big decimal representation of the result of
     *        calculation of the given {@code postfix} expression
     * @throws Exception if anything goes wrong
     *
     * @author T.N.Silverman
     */
    @ParameterizedTest
    @DisplayName("test partially customized postfix calculation")
    //@Disabled
    // postfix and expected result are separated by pipe char '|'
    @CsvSource(delimiter = '|',
               value = {"2 2 ^|4.00", //in the power of
                        "4 !|24.00", //product
                        "4 √|2.00", //square root
                        "1 2 *|2.00", // mult
                        "1 2 ×|2.00", // × supported for mult
                        "2 1 /|2.00", // div
                        "2 1 ÷|2.00", // ÷ supported for div
                        "4 3 %|1.00", // modulus
                        "1 2 +|3.00", // add
                        "1 2 -|-1.00", // subtract
                        "1 2 −|-1.00", // − supported for subtract
                        "π 2 *|6.28", // pi constant mult
                        "PI 2 *|6.28", // pi constant mult
                        "e 2 *|5.44", // Euler's number 2.71828D mult
                        "90 sin|1.00", // sin function
                        "90 cos|0.00", // cosin function
                        "45 tan|1.00", // tangent function
                        "400 √|20.00", // square root function
                        "1 4 min|1.00", // minimum of function
                        "1 4 max|4.00", // maximum of function
                        "2 4 avg|3.00", // average of function
                        "2 4 pct|50.00" // percent of function

               })
    public void partiallyCustomizedPostfixCalculation(String postfix,
            BigDecimal expected) throws Exception {
        // this won't affect any of these simple calculations
        context.setRoundingMode(RoundingMode.HALF_UP);
        // this will set the precision of decimal places to 2
        context.setPrecision(2);
        BigDecimal actual = RPNCalculator.withContext(context).accept(postfix)
                .doPrint().thenCalculate();
        assertEquals(expected, actual);
    }

}
