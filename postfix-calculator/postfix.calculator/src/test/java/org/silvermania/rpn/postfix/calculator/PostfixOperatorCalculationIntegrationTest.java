/*
 * File: PostfixOperatorCalculationIntegrationTest.java
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

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.silvermania.rpn.infix.converter.InfixConverter;

/**
 * The Class PostfixOperatorCalculationIntegrationTest is a unit test to assert the
 * functionality of operators evaluation in the {@link InfixConverter} class.
 *
 * @author T.N.Silverman
 */
class PostfixOperatorCalculationIntegrationTest extends BaseCalculatorTestCase {

    @ParameterizedTest
    // @Disabled
    @DisplayName("test operator pow")
    @CsvSource({"2 ^ 4,2 4 ^,16", "2 ^ 4 - ( 2 ^ 3 ),2 4 ^ 2 3 ^ -,8"})
    void testOperatorPow(String infix, String postfix, BigDecimal expected) {
        String expression = infixConverter.convert(infix);
        assertEquals(postfix, expression);
        BigDecimal actual = RPNCalculator.calculate(postfix);
        assertEquals(expected.doubleValue(), actual.doubleValue());
    }

    @ParameterizedTest
    // @Disabled
    @DisplayName("test operator mult")
    @CsvSource({"12 × 11,12 11 ×,132", "12 * 11,12 11 *,132"})
    void testOperatorMult(String infix, String postfix, BigDecimal expected) {
        String expression = infixConverter.convert(infix); // "3 4 2 × 1
                                                           // 5 − 2 3 ^
                                                           // ^ ÷ +";
        assertEquals(postfix, expression);
        BigDecimal actual = RPNCalculator.calculate(postfix);
        assertEquals(expected.doubleValue(), actual.doubleValue());
    }

    @ParameterizedTest
    // @Disabled
    @DisplayName("test operator div")
    @CsvSource({"3 / 4,3 4 /,0.75",
                "22 / 7,22 7 /,3.1428571",
                "3 ÷ 4,3 4 ÷,0.75",
                "22 ÷ 7,22 7 ÷,3.1428571"})
    void testOperatorDiv(String infix, String postfix, BigDecimal expected) {
        String expression = infixConverter.convert(infix); // "3 4 2 × 1
                                                           // 5 − 2 3 ^
                                                           // ^ ÷ +";
        assertEquals(postfix, expression);
        BigDecimal actual = RPNCalculator.calculate(postfix);
        assertEquals(expected.doubleValue(), actual.doubleValue());
    }

    @ParameterizedTest
    // @Disabled
    @DisplayName("test operator mod")
    @CsvSource({"22 % 7,22 7 %,1"})
    void testOperatorMod(String infix, String postfix, BigDecimal expected) {
        String expression = infixConverter.convert(infix);
        assertEquals(postfix, expression);
        BigDecimal actual = RPNCalculator.calculate(postfix);
        assertEquals(expected.doubleValue(), actual.doubleValue());
    }

    @ParameterizedTest
    // @Disabled
    @DisplayName("test operator plus")
    @CsvSource({"3 + 4 - 2,3 4 + 2 -,5",
                "3 + 4 - 4 / 2,3 4 + 4 2 / -,5",
                "3 + 4 - 4 / 2,3 4 + 4 2 / -,5",
                "3000001 + ( 4000001 - 2000000 ) / 2,"
                    + "3000001 4000001 2000000 - 2 / +,4000001.50"})
    void testOperatorPlus(String infix, String postfix, BigDecimal expected) {
        String expression = infixConverter.convert(infix);
        assertEquals(postfix, expression);
        BigDecimal actual = RPNCalculator.calculate(postfix);
        assertEquals(expected.doubleValue(), actual.doubleValue());
    }

    @ParameterizedTest
    // @Disabled
    @DisplayName("test operator minus")
    @CsvSource({"4 - 2,4 2 -,2",
                "4 - 2 - 2,4 2 - 2 -,0",
                "4 − 2,4 2 −,2",
                "4 − 2 − 2,4 2 − 2 −,0"})
    void testOperatorMinus(String infix, String postfix, BigDecimal expected) {
        String expression = infixConverter.convert(infix);
        assertEquals(postfix, expression);
        BigDecimal actual = RPNCalculator.calculate(postfix);
        assertEquals(expected.doubleValue(), actual.doubleValue());
    }

    @ParameterizedTest
    // @Disabled
    @DisplayName("test operator factorial")
    @CsvSource({"10!,10 !,3628800", "0!,0 !,1","20!,20 !,2.43290200817664E18"})
    void testOperatorFactorial(String infix, String postfix,
            BigDecimal expected) {
        String expression = infixConverter.convert(infix);
        assertEquals(postfix, expression);
        BigDecimal actual = RPNCalculator.calculate(postfix);
        assertEquals(expected.doubleValue(), actual.doubleValue());
    }

    @ParameterizedTest
    // @Disabled
    @DisplayName("test factorial out of range throws")
    @CsvSource({"-1!","21!"})
    void testFactorialOutOfRangeThrows(String infix) {
        String postfix = infixConverter.convert(infix);
        assertThrows(IllegalArgumentException.class, () -> RPNCalculator.calculate(postfix));
    }


    @ParameterizedTest
    // @Disabled
    @DisplayName("test operator constants")
    @CsvSource({"e * 2,e 2 *,5.436560",
                "π + π,π π +,6.2831853",
                "π * 2,π 2 *,6.2831853",
                "π * π,π π *,9.8696044"})
    void testOperatorConstants(String infix, String postfix,
            BigDecimal expected) {
        String expression = infixConverter.convert(infix);
        assertEquals(postfix, expression);
        BigDecimal actual = RPNCalculator.calculate(postfix);
        assertEquals(expected.doubleValue(), actual.doubleValue());
    }
}
