/*
 * File: PostfixCalculatorIntegrationTest.java
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

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.silvermania.rpn.support.Associativity.LEFT;
import static org.silvermania.rpn.support.Multiplicity.UNARY;
import static org.silvermania.rpn.support.Precedence.HIGH;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.silvermania.rpn.support.CalculationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class PostfixCalculatorIntegrationTest is an integration test of advanced
 * {@link RPNCalculator} configuration options and calculations.
 */
public class PostfixCalculatorIntegrationTest {

    /** The logger. */
    private final static Logger logger = LoggerFactory.getLogger(PostfixCalculatorIntegrationTest.class);

    /** The calculation context used as default, unless configured. */
    protected CalculationContext context;

    /**
     * Before each test in the test case, logs the entering to a test method.
     *
     * @param info the test info instance
     * @throws Exception if anything goes wrong
     */
    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        logger.debug("entering {} {}", info.getTestMethod().orElseThrow().getName(), info.getDisplayName());
        context = CalculationContext.newInstance();
    }

    /**
     * Simple infix calculation.
     *
     * @param infix the infix
     * @param expected the expected
     * @throws Exception the exception
     */
    @ParameterizedTest
    @DisplayName("test simple infix calculation")
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
                        "pct(2,4)|50.0000000", // percent of function
                        "log(10)|1.0000000" // base 10 log of
               })
    public void simpleInfixCalculation(String infix, BigDecimal expected) throws Exception {
        BigDecimal actual = RPNCalculator.withDefaults().convert(infix).thenCalculate();
        assertEquals(expected, actual);
    }

    /**
     * Simple infix calculation with printing.
     *
     * @param infix the infix
     * @param expected the expected
     * @throws Exception the exception
     */
    @ParameterizedTest
    @DisplayName("test simple infix calculation with printing")
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
                        "pct(2,4)|50.0000000", // percent of function
                        "log(10)|1.0000000" // base 10 log of
               })
    public void simpleInfixCalculationWithPrinting(String infix, BigDecimal expected) throws Exception {
        BigDecimal actual = RPNCalculator.withDefaults().convert(infix).doPrint().thenCalculate();
        assertEquals(expected, actual);
    }

    /**
     * Simple postfix calculation.
     *
     * @param postfix the postfix
     * @param expected the expected
     * @throws Exception the exception
     */
    @ParameterizedTest
    @DisplayName("test simple postfix calculation")
    @CsvSource(delimiter = '|',
               value = {"2 2 ^|4.0000000", //in the power of
                        "4 !|24.0000000", //product
                        "4 √|2.0000000", //square root
                        "1 2 *|2.0000000", // mult
                        "1 2 ×|2.0000000", // × supported for mult
                        "2 1 /|2.0000000", // div
                        "2 1 ÷|2.0000000", // ÷ supported for div
                        "4 3 %|1.0000000", // modulus
                        "1 2 +|3.0000000", // add
                        "1 2 -|-1.0000000", // subtract
                        "1 2 −|-1.0000000", // − supported for subtract
                        "π 2 *|6.2831853", // pi constant mult
                        "PI 2 *|6.2831853", // pi constant mult
                        "e 2 *|5.4365600", // Euler's number 2.71828D mult
                        "90 sin|1.0000000", // sin function
                        "90 cos|0.0000000", // cosin function
                        "45 tan|1.0000000", // tangent function
                        "400 √|20.0000000", // square root function
                        "1 4 min|1.0000000", // minimum of function
                        "1 4 max|4.0000000", // maximum of function
                        "2 4 avg|3.0000000", // average of function
                        "2 4 pct|50.0000000", // percent of function
                        "10 log|1.0000000" // base 10 log of

               })
    public void simplePostfixCalculation(String postfix, BigDecimal expected) throws Exception {
        BigDecimal actual = RPNCalculator.withDefaults().accept(postfix).thenCalculate();
        assertEquals(expected, actual);
    }

    /**
     * Simple postfix calculation with printing.
     *
     * @param postfix the postfix
     * @param expected the expected
     * @throws Exception the exception
     */
    @ParameterizedTest
    @DisplayName("test simple postfix calculation with printing")
    @CsvSource(delimiter = '|',
               value = {"2 2 ^|4.0000000", //in the power of
                        "4 !|24.0000000", //product
                        "4 √|2.0000000", //square root
                        "1 2 *|2.0000000", // mult
                        "1 2 ×|2.0000000", // × supported for mult
                        "2 1 /|2.0000000", // div
                        "2 1 ÷|2.0000000", // ÷ supported for div
                        "4 3 %|1.0000000", // modulus
                        "1 2 +|3.0000000", // add
                        "1 2 -|-1.0000000", // subtract
                        "1 2 −|-1.0000000", // − supported for subtract
                        "π 2 *|6.2831853", // pi constant mult
                        "PI 2 *|6.2831853", // pi constant mult
                        "e 2 *|5.4365600", // Euler's number 2.71828D mult
                        "90 sin|1.0000000", // sin function
                        "90 cos|0.0000000", // cosin function
                        "45 tan|1.0000000", // tangent function
                        "400 √|20.0000000", // square root function
                        "1 4 min|1.0000000", // minimum of function
                        "1 4 max|4.0000000", // maximum of function
                        "2 4 avg|3.0000000", // average of function
                        "2 4 pct|50.0000000", // percent of function
                        "10 log|1.0000000" // base 10 log of

               })
    public void simplePostfixCalculationWithPrinting(String postfix, BigDecimal expected) throws Exception {
        BigDecimal actual = RPNCalculator.withDefaults().accept(postfix).doPrint().thenCalculate();
        assertEquals(expected, actual);
    }

    /**
     * Nested function infix calculation.
     *
     * @param infix the infix
     * @param expected the expected
     * @throws Exception the exception
     */
    @ParameterizedTest
    @DisplayName("test nested function infix calculation")
    @CsvSource(delimiter = '|',
               value = {"2+min(2,max(4,6))|4.0000000",
                        "avg(min(2,4),max(1,6))|4.0000000",
                        "4-[avg(√4,avg(√4,10%4))]|2.0000000",
                        "{2*[avg(sin(90),min(√1,π))]}-avg(sin(90),min(√1,π))|1.0000000"})
    public void nestedFunctionInfixCalculation(String infix, BigDecimal expected) throws Exception {
        BigDecimal actual = RPNCalculator.withDefaults().convert(infix).thenCalculate();
        assertEquals(expected, actual);
    }

    /**
     * Customized infix calculation.
     *
     * @param infix the infix
     * @param expected the expected
     * @throws Exception the exception
     */
    @ParameterizedTest
    @DisplayName("test customized infix calculation")
    //@Disabled
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
                        "pct(2,4)|50.0000000000000000", // percent of function
                        "log(10)|1.0000000000000000" // base 10 log of
               })
    public void customizedInfixCalculation(String infix, BigDecimal expected) throws Exception {
        context = CalculationContext.DECIMAL64_CONTEXT;
        BigDecimal actual = RPNCalculator.withContext(context).convert(infix).doPrint().thenCalculate();
        assertEquals(expected, actual);
    }

    /**
     * Partially customized infix calculation.
     *
     * @param infix the infix
     * @param expected the expected
     * @throws Exception the exception
     */
    @ParameterizedTest
    @DisplayName("test partially customized infix calculation")
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
                        "log(10)|1.00" // base 10 log of
               })
    public void partiallyCustomizedInfixCalculation(String infix, BigDecimal expected) throws Exception {
        // this won't affect any of these simple calculations
        context.setRoundingMode(RoundingMode.HALF_UP);
        // this will set the precision of decimal places to 2
        context.setPrecision(2);
        BigDecimal actual = RPNCalculator.withContext(context).convert(infix).doPrint().thenCalculate();
        assertEquals(expected, actual);
    }

    /**
     * Customized postfix calculation.
     *
     * @param postfix the postfix
     * @param expected the expected
     * @throws Exception the exception
     */
    @ParameterizedTest
    @DisplayName("test customized postfix calculation")
    //@Disabled
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
                        "2 4 pct|50.0000000000000000", // percent of function
                        "10 log|1.0000000000000000" // base 10 log of
               })
    public void customizedPostfixCalculation(String postfix, BigDecimal expected) throws Exception {
        context = CalculationContext.DECIMAL64_CONTEXT;
        BigDecimal actual = RPNCalculator.withContext(context).accept(postfix).doPrint().thenCalculate();
        assertEquals(expected, actual);
    }

    /**
     * Partially customized postfix calculation.
     *
     * @param postfix the postfix
     * @param expected the expected
     * @throws Exception the exception
     */
    @ParameterizedTest
    @DisplayName("test partially customized postfix calculation")
    //@Disabled
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
                        "2 4 pct|50.00", // percent of function
                        "10 log|1.00" // percent of function

               })
    public void partiallyCustomizedPostfixCalculation(String postfix, BigDecimal expected) throws Exception {
        // this won't affect any of these simple calculations
        context.setRoundingMode(RoundingMode.HALF_UP);
        // this will set the precision of decimal places to 2
        context.setPrecision(2);
        BigDecimal actual = RPNCalculator.withContext(context).accept(postfix).doPrint().thenCalculate();
        assertEquals(expected, actual);
    }

    /**
     * Register new constant.
     *
     * @param infix the infix
     * @param expected the expected
     * @throws Exception the exception
     */
    @ParameterizedTest
    @DisplayName("test register new constant")
    //@Disabled
    @CsvSource(delimiter = '|',
               value = {"~/2|60.0000000", // new virtual operator ~
                        "~*[1/√2]|84.8528160" // RMS value of ~
               })
    public void registerNewConstant(String infix, BigDecimal expected) throws Exception {
        context.registerConstant("~", BigDecimal.valueOf(120));
        BigDecimal actual = RPNCalculator.withContext(context).convert(infix).doPrint().thenCalculate();
        assertEquals(expected, actual);
    }

    /**
     * Register new function.
     *
     * @param infix the infix
     * @param expected the expected
     * @throws Exception the exception
     */
    @ParameterizedTest
    @DisplayName("test register new constant")
    //@Disabled
    @CsvSource(delimiter = '|',
               value = {"rms(~)|84.8528160", // new virtual operator ~
               })
    public void registerNewFunction(String infix, BigDecimal expected) throws Exception {
        context.registerConstant("~", BigDecimal.valueOf(120));
        context.registerFunction("rms", UNARY, arr -> arr[0].multiply(valueOf(0.7071068)));
        BigDecimal actual = RPNCalculator.withContext(context).convert(infix).doPrint().thenCalculate();
        assertEquals(expected, actual);
    }

    /**
     * Register new operator.
     *
     * @param infix the infix
     * @param expected the expected
     * @throws Exception the exception
     */
    @ParameterizedTest
    @DisplayName("test register new operator")
    //@Disabled
    @CsvSource(delimiter = '|',
               value = {"r 4|0.2500000"})
    public void registerNewOperator(String infix, BigDecimal expected) throws Exception {
        context.registerOperator("r", HIGH, LEFT, UNARY,
                (arr) -> ONE.divide(arr[0], context.getPrecision(), context.getRoundingMode()));
        BigDecimal actual = RPNCalculator.withContext(context).convert(infix).doPrint().thenCalculate();
        assertEquals(expected, actual);
    }

}
