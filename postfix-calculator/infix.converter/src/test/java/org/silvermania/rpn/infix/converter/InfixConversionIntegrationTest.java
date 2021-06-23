/*
 * File: InfixConversionIntegrationTest.java
 * Creation Date: Jun 18, 2019
 *
 * Copyright (c) 2019 T.N.Silverman - all rights reserved
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license
 * agreements. See the NOTICE file distributed with this work for additional
 * information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache
 * License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express
 * or implied. See the License for the specific language governing permissions
 * and limitations under
 * the License.
 */

package org.silvermania.rpn.infix.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.silvermania.rpn.support.CalculationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class InfixConversionIntegrationTest.is a test case for asserting the functionality
 * of the {@link InfixConverter} class
 *
 * @author T.N.Silverman
 */
class InfixConversionIntegrationTest {

    private static final Logger logger =
        LoggerFactory.getLogger(InfixConversionIntegrationTest.class);
    private CalculationContext context = CalculationContext.newInstance();

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        logger.debug("entering {} {}",
                info.getTestMethod().orElseThrow().getName(),
                info.getDisplayName());
    }

    @Test
    //@Disabled
    @DisplayName("sanity test shunting yard wiki conversion")
    void testShuntingYard1WikiConversion() {
        String infix = "3 + 4 × 2 ÷ ( 1 − 5 ) ^ 2 ^ 3";
        String expected = "3 4 2 × 1 5 − 2 3 ^ ^ ÷ +"; // 3 4 2 × 1 5 − 2 3 ^ ^ ÷ +
        String actual = InfixConverter.newInstance(context).convert(infix);
        assertEquals(expected, actual);
    }

    @Test
    //@Disabled
    @DisplayName("sanity test wiki example")
    void testSanitySimpleWikiConversion() {
        String expression =
            "( ( 15 ÷ ( 7 − ( 1 + 1 ) ) ) × 3 ) − ( 2 + ( 1 + 1 ) )";
        String expected = "15 7 1 1 + − ÷ 3 × 2 1 1 + + −";
        String actual = InfixConverter.newInstance(context).convert(expression);
        assertEquals(expected, actual);
    }

    @Test
    //@Disabled
    @DisplayName("sanity test shunting yard 2 wiki conversion")
    void testShuntingYard2WikiConversion() {
        String expression = "sin ( max ( 2 , 3 ) ÷ 3 × π )";
        String expected = "2 3 max 3 ÷ π × sin";
        String actual = InfixConverter.newInstance(context).convert(expression);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @DisplayName("test infix conversion")
    //@Disabled
    @CsvSource(delimiter = '@',
               value = {"1 + 4 / 2@1 4 2 / +",
                        "2 ^ 4 * [ ( 8 ! + 3 ) / ( 8 % 3 ) ] + 1@2 4 ^ 8 ! 3 + 8 3 % / * 1 +",
                        "2 ^ 4 * [ ( 8 + 3 ) ]@2 4 ^ 8 3 + *",
                        "-2 ^ 4 * 8@-2 4 ^ 8 *",
                        "2 ^ 4 * 8@2 4 ^ 8 *",
                        "-2 ^ 4 * 8@-2 4 ^ 8 *",
                        "3 + 4 * 2@3 4 2 * +",
                        "3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3@3 4 2 * 1 5 - 2 3 ^ ^ / +",
                        "3 + 4 × 2 ÷ ( 1 − 5 ) ^ 2 ^ 3@3 4 2 × 1 5 − 2 3 ^ ^ ÷ +",
                        "sin ( max ( 2 , 3 ) ÷ 3 × π )@2 3 max 3 ÷ π × sin"})
    void testConversion(String expression, String expected) {
        String actual = InfixConverter.newInstance(context).convert(expression);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @DisplayName("test unmatched brackets infix expression conversion")
    //@Disabled
    @CsvSource(delimiter = '@',
               value = {"{ { [ ( 1 + 1 ) + ( 1 + 1 ) ] }",
                        "{ [ ( 1 + 1 ) + ( 1 + 1 ) ] } }",
                        "{ [ [ ( 1 + 1 ) + ( 1 + 1 ) ] }",
                        "{ [ ( 1 + 1 ) + ( 1 + 1 ) ] ] }",
                        "{ [ ( ( 1 + 1 ) + ( 1 + 1 ) ] }",
                        "{ [ ( 1 + 1 ) + ( 1 + 1 ) ) ] }",
                        "1 + { 2 + [ ( 1 + 1 ) + ( 1 + 1 ) ] } + 2 }"})
    void testUnmatchedBracketsInfixExpression(String expression) {
        assertThrows(IllegalArgumentException.class,
                () -> InfixConverter.newInstance(context).convert(expression));
    }

    @ParameterizedTest
    @DisplayName("test multi bracket infix conversion")
    //@Disabled
    @CsvSource(delimiter = '@',
               value = {"1 + [ 2 ! - ( 8 - 3 ) * ( 8 % 3 ) ] + 1@1 2 ! 8 3 - 8 3 % * - + 1 +",
                        "1 + ( 2 ! - ( 8 - 3 ) * ( 8 % 3 ) ) + 1@1 2 ! 8 3 - 8 3 % * - + 1 +",
                        "1 + { [ 2 ! - ( 8 - 3 ) * ( 8 % 3 ) ] } + 1@1 2 ! 8 3 - 8 3 % * - + 1 +",
                        "1 + { 4 * [ 2 ! - ( 8 - 3 ) * ( 8 % 3 ) ] } + 1@1 4 2 ! 8 3 - 8 3 % * - * + 1 +"})
    void testMultiBracketInfixConversion(String expression, String expected) {
        String actual = InfixConverter.newInstance(context).convert(expression);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @DisplayName("test illegal infix conversion")
    //@Disabled
    @CsvSource(delimiter = '|',
               value = {"1+blah(102)", "1 $ 3", "1 root 3", "1 @ ( 3 + 1)"})
    void testMalformedInfixExpression(String expression) {
        assertThrows(IllegalArgumentException.class, () -> {
            try {
                CharSequence postfix =
                    InfixConverter.newInstance(context).convert(expression);
                logger.debug("Converted '{}' to: {}", expression, postfix);
            } catch (Exception ex) {
                logger.debug("{}", ex);
                throw ex;
            }
        });
    }

    @ParameterizedTest
    @DisplayName("test quirk conversion")
    //@Disabled
    @CsvSource(delimiter = '@',
               value = {"min (1,2,3)@1 2 3 min"}) // quirk! too many arguments - but it works
    /*
     * FIXME: process multi-arg functions is to be added later
     */
    void testQuirks(String infix, String expected) {
        CharSequence actual =
            InfixConverter.newInstance(context).convert(infix);
        assertEquals(expected, actual);
    }

}
