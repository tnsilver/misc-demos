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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.silvermania.rpn.infix.converter.InfixConverter;
import org.silvermania.rpn.support.CalculationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class PostfixFunctionCalculationIntegrationTest is a unit test to assert the
 * functionality of function evaluation in the {@link InfixConverter} class.
 *
 * @author T.N.Silverman
 */
class PostfixCalculationFluentApiIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(PostfixCalculationFluentApiIntegrationTest.class);

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        logger.debug("entering {} {}", info.getTestMethod().orElseThrow().getName(), info.getDisplayName());
    }

    @ParameterizedTest
    @DisplayName("test defaults infix exponential calculation")
    //@Disabled
    @CsvSource(delimiter = '@',
               value = {"2^2^2@16.0000000",
                        "2^2*(2^2)@16.0000000",
                        "2^2^3@256.0000000",
                        "2^2*2^3@32.0000000",
                        "2^3*2^2@32.0000000",
                        "2^3+2^2@12.0000000",
                        "2^3-2^2@4.0000000",
                        "2^3-2^2+2^2@8.0000000",})
    void testDefaultInfixExponnetialCalculation(String infix, BigDecimal expected) throws Exception {
        BigDecimal actual = RPNCalculator.withDefaults().convert(infix).doPrint().thenCalculate();
        assertEquals(expected, actual.equals(BigDecimal.ZERO) ? BigDecimal.ZERO : actual);
    }

    @ParameterizedTest
    @DisplayName("test defaults infix calculation")
    //@Disabled
    @CsvSource(delimiter = '@',
               value = {"sin(90)@1.0000000",
                        "cos(360)@1.0000000",
                        "tan(45)@1.0000000",
                        "√9@3.0000000",
                        "min(1,2)@1.0000000",
                        "max(1,2)@2.0000000",
                        "avg(2,4)@3.0000000",
                        "2^4@16.0000000",
                        "2^4-(2^3)@8.0000000",
                        "12×11@132.0000000",
                        "12*11@132.0000000",
                        "22/7@3.1428571",
                        "3÷4@0.7500000",
                        "22÷7@3.1428571",
                        "22%7@1.0000000",
                        "3+4-4/2@5.0000000",
                        "3+4-4/2@5.0000000",
                        "4−2@2.0000000",
                        "4−2−2@0.0000000",
                        "10!@3628800.0000000",
                        "e*2@5.4365600",
                        "π+π@6.2831853",
                        "π*2@6.2831853",
                        "π*π@9.8696044"})
    void testDefaultInfixCalculation(String infix, BigDecimal expected) throws Exception {
        BigDecimal actual = RPNCalculator.withDefaults().convert(infix).thenCalculate();
        assertEquals(expected, actual.equals(BigDecimal.ZERO) ? BigDecimal.ZERO : actual);
    }

    @ParameterizedTest
    @DisplayName("test infix calculation with context")
    //@Disabled
    @CsvSource(delimiter = '@',
               value = {"sin(90)@1.0000000000000000",
                        "cos(360)@1.0000000000000000",
                        "tan(45)@0.9999999999999999",
                        "√9@3.0000000000000000",
                        "min(1,2)@1.0000000000000000",
                        "max(1,2)@2.0000000000000000",
                        "avg(2,4)@3.0000000000000000",
                        "2^4@16.0000000000000000",
                        "2^4-(2^3)@8.0000000000000000",
                        "12×11@132.0000000000000000",
                        "12*11@132.0000000000000000",
                        "22/7@3.1428571428571429",
                        "3÷4@0.7500000000000000",
                        "22÷7@3.1428571428571429",
                        "22%7@1.0000000000000000",
                        "3+4-4/2@5.0000000000000000",
                        "3+4-4/2@5.0000000000000000",
                        "4−2@2.0000000000000000",
                        "4−2−2@0.0000000000000000",
                        "10!@3628800.0000000000000000",
                        "e*2@5.4365600000000001",
                        "π+π@6.2831853071795862",
                        "π*2@6.2831853071795862",
                        "π*π@9.8696044010893578"})
    void testInfixCalculationWithContext(String infix, BigDecimal expected) throws Exception {
        BigDecimal actual =
            RPNCalculator.withContext(CalculationContext.DECIMAL64_CONTEXT).convert(infix).thenCalculate();
        assertEquals(expected, actual.equals(BigDecimal.ZERO) ? BigDecimal.ZERO : actual);
    }

    @ParameterizedTest
    @DisplayName("test defaults postfix calculation")
    //@Disabled
    @CsvSource(delimiter = '@',
               value = {"2 3 max 3 ÷ π × sin@0.0548037",
                        "3 4 2 × 1 5 − 2 3 ^ ^ ÷ +@3.0001221",
                        "15 7 1 1 + − ÷ 3 × 2 1 1 + + −@5.0000000",
                        "2 3 max 3 ÷ π × sin@0.0548037",
                        "1 4 2 / +@3.0000000",
                        "2 4 ^ 8 ! 3 + 8 3 % / * 1 +@322585.0000000",
                        "2 4 ^ 8 3 + *@176.0000000",
                        "-2 4 ^ 8 *@128.0000000",
                        "3 4 2 * +@11.0000000",
                        "3 4 2 * 1 5 - 2 3 ^ ^ / +@3.0001221",
                        "3 4 2 × 1 5 − 2 3 ^ ^ ÷ +@3.0001221",
                        "2 3 max 3 ÷ π × sin@0.0548037"})
    void testDefaultPostfixCalculation(String postfix, BigDecimal expected) throws Exception {
        BigDecimal actual = RPNCalculator.withDefaults().accept(postfix).thenCalculate();
        assertEquals(expected, actual.equals(BigDecimal.ZERO) ? BigDecimal.ZERO : actual);
    }

    @ParameterizedTest
    @DisplayName("test infix when expected postfix and throw")
    //@Disabled
    @CsvSource(delimiter = '@',
               value = {"12*11", "cos(360)", "22%7", "22 % 7"})
    void testUnmatchedBracketsInfixExpression(String infix) {
        assertThrows(IllegalArgumentException.class, () -> {
            try {
                RPNCalculator.withDefaults().accept(infix).thenCalculate();
            } catch (Exception ex) {
                logger.debug("{}", ex);
                throw ex;
            }
        });

    }

}
