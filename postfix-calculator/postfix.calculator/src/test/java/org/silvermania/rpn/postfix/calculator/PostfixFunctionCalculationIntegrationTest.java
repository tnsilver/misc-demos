/*
 * File: PostfixFunctionCalculationIntegrationTest.java
 * Creation Date: Jun 18, 2019
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.silvermania.rpn.infix.converter.InfixConverter;

/**
 * The Class PostfixFunctionCalculationIntegrationTest is a unit test to assert the
 * functionality of function evaluation in the {@link InfixConverter} class.
 *
 * @author T.N.Silverman
 */
class PostfixFunctionCalculationIntegrationTest extends BaseCalculatorTestCase {

    @ParameterizedTest
    @DisplayName("test function sin")
    // @Disabled
    @CsvSource({"sin ( 90 ),90 sin,1.0",
                "sin 90,90 sin,1.0",
                "sin 45,45 sin,0.7071068",
                "sin 22.5,22.5 sin,0.3826834"})
    void testFunctionSin(String infix, String postfix, BigDecimal expected) {
        String expression = infixConverter.convert(infix); // "3 4 2 × 1
                                                           // 5 − 2 3 ^
                                                           // ^ ÷ +";
        assertEquals(postfix, expression);
        BigDecimal actual = RPNCalculator.calculate(postfix);
        assertEquals(expected.doubleValue(), actual.doubleValue());
    }

    @ParameterizedTest
    // @Disabled
    @DisplayName("test function cos")
    @CsvSource({"cos ( 360 ),360 cos,1.0",
                "cos 360,360 cos,1.0",
                "cos 45,45 cos,0.7071068"})
    void testFunctionCos(String infix, String postfix, BigDecimal expected) {
        String expression = infixConverter.convert(infix); // "3 4 2 × 1
                                                           // 5 − 2 3 ^
                                                           // ^ ÷ +";
        assertEquals(postfix, expression);
        BigDecimal actual = RPNCalculator.calculate(postfix);
        assertEquals(expected.doubleValue(), actual.doubleValue());
    }

    @ParameterizedTest
    // @Disabled
    @DisplayName("test function tan")
    @CsvSource({"tan ( 45 ),45 tan,1", "tan 45,45 tan,1", "tan 360,360 tan,0"})
    void testFunctionTan(String infix, String postfix, BigDecimal expected) {
        String expression = infixConverter.convert(infix); // "3 4 2 × 1
                                                           // 5 − 2 3 ^
                                                           // ^ ÷ +";
        assertEquals(postfix, expression);
        BigDecimal actual = RPNCalculator.calculate(postfix);
        assertEquals(expected.doubleValue(), actual.doubleValue());
    }

    @ParameterizedTest
    // @Disabled
    @DisplayName("test function sqrt")
    @CsvSource({"√9,9 √,3.0",
                "√400,400 √,20.0",
                "√250000,250000 √,500.0",
                "√ 2,2 √,1.4142136"})
    void testFunctionSqrt(String infix, String postfix, BigDecimal expected) {
        String expression = infixConverter.convert(infix); // "3 4 2 × 1
                                                           // 5 − 2 3 ^
                                                           // ^ ÷ +";
        assertEquals(postfix, expression);
        BigDecimal actual = RPNCalculator.calculate(postfix);
        assertEquals(expected.doubleValue(), actual.doubleValue());
    }

    @ParameterizedTest
    // @Disabled
    @DisplayName("test function min")
    @CsvSource(delimiter = '@',
               value = {"min ( 1 , 2 )@1 2 min@1.0",
                        "min (1,2)@1 2 min@1.0",
                        "min(1.12, 3.14)@1.12 3.14 min@1.12",
                        "min(π,6.28)@π 6.28 min@3.1415927"})
    void testFunctionMin(String infix, String postfix, BigDecimal expected) {
        String expression = infixConverter.convert(infix); // "3 4 2 × 1
                                                           // 5 − 2 3 ^
                                                           // ^ ÷ +";
        assertEquals(postfix, expression);
        BigDecimal actual = RPNCalculator.calculate(postfix);
        assertEquals(expected.doubleValue(), actual.doubleValue());
    }

    @ParameterizedTest
    // @Disabled
    @DisplayName("test function max")
    @CsvSource(delimiter = '@',
               value = {"max ( 2 , 1 )@2 1 max@2.0",
                        "max (2,1)@2 1 max@2.0",
                        "max(3.14, 1.12)@3.14 1.12 max@3.14",
                        "max (6.28, π)@6.28 π max@6.28"})
    void testFunctionMax(String infix, String postfix, BigDecimal expected) {
        String expression = infixConverter.convert(infix); // "3 4 2 × 1
                                                           // 5 − 2 3 ^
                                                           // ^ ÷ +";
        assertEquals(postfix, expression);
        BigDecimal actual = RPNCalculator.calculate(postfix);
        assertEquals(expected.doubleValue(), actual.doubleValue());
    }

    @ParameterizedTest
    // @Disabled
    @DisplayName("test function avg")
    @CsvSource(delimiter = '@',
               value = {"avg ( 2 , 1 )@2 1 avg@1.5",
                        "avg (2,1)@2 1 avg@1.5",
                        "avg (1.6666,1.7565)@1.6666 1.7565 avg@1.71155",
                        "avg (3.14,1.12)@3.14 1.12 avg@2.13",
                        "avg (6.28,π)@6.28 π avg@4.7107963",
                        "avg (π/3,π/2)@π 3 / π 2 / avg@1.308997"})
    void testFunctionAvg(String infix, String postfix, BigDecimal expected) {
        String expression = infixConverter.convert(infix); // "3 4 2 × 1
                                                           // 5 − 2 3 ^
                                                           // ^ ÷ +";
        assertEquals(postfix, expression);
        BigDecimal actual = RPNCalculator.calculate(postfix);
        assertEquals(expected.doubleValue(), actual.doubleValue());
    }

    @ParameterizedTest
    // @Disabled
    @DisplayName("test nested functions")
    @CsvSource(delimiter = '@',
               value = {"min(min(10,11),9)@10 11 min 9 min@9.0",
                        "min(min(10,11),min(9,10))@10 11 min 9 10 min min@9.0",
                        "min(√4,max(4,8))@4 √ 4 8 max min@2.0",
                        "min(√16,max(4,8))@16 √ 4 8 max min@4.0",
                        "1 + [min(√16,max(4,8))]@1 16 √ 4 8 max min +@5.0",
                        "1 + [min(√16,max(π,8))]@1 16 √ π 8 max min +@5.0",
                        "max(1 + [min(√16,max(π,8))],(2 * π))@1 16 √ π 8 max min + 2 π * max@6.2831853",
                        "π * {1 + [max(min([max(√4,√16)],[avg(2,4)]),avg(2,√16))]}@π 1 4 √ 16 √ max 2 4 avg min 2 16 √ avg max + *@12.5663706"})
    void testNestedFunctions(String infix, String postfix,
            BigDecimal expected) {
        String expression = infixConverter.convert(infix); // "3 4 2 × 1
        assertEquals(postfix, expression);
        BigDecimal actual = RPNCalculator.calculate(postfix);
        assertEquals(expected.doubleValue(), actual.doubleValue());
    }

}
