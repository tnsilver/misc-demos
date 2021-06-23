/*
 * File: InfixTokenHandlerTest.java
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
package org.silvermania.rpn.infix.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Collectors;

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
 * The Class InfixNormalizerTest is a unit test to assert the functionality of
 * the {@link InfixNormalizer} class
 *
 * @author T.N.Silverman
 */
class InfixNormalizerTest {

    private static final Logger logger = LoggerFactory.getLogger(InfixNormalizerTest.class);
    private InfixNormalizer normalizer;
    private CalculationContext context = CalculationContext.newInstance();

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        normalizer = InfixNormalizer.newInstance(context);
        logger.debug("entering {} {}", info.getTestMethod().orElseThrow().getName(), info.getDisplayName());
    }

    @ParameterizedTest
    //@Disabled
    @DisplayName("test parse infix ok")
    @CsvSource(delimiter = '@',
               value = {"6^4*[(8!+3)/(8%3)]+1@6 ^ 4 * [ ( 8 ! + 3 ) / ( 8 % 3 ) ] + 1",
                        "2^4*[(8!+3)/(8%3)]+1@2 ^ 4 * [ ( 8 ! + 3 ) / ( 8 % 3 ) ] + 1",
                        "2 ^ 4 * [ ( 8 + 3 ) ]@2 ^ 4 * [ ( 8 + 3 ) ]",
                        "2^4 * 8@2 ^ 4 * 8",
                        "3 +4 *2 /(1 - 5) ^ 2  ^3@3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3",
                        "3 +4 ×2÷( 1 − 5)^ 2 ^3@3 + 4 × 2 ÷ ( 1 − 5 ) ^ 2 ^ 3",
                        "3+4×2÷( 1 − 5 )^2^3@3 + 4 × 2 ÷ ( 1 − 5 ) ^ 2 ^ 3",
                        "sin( max( 2 ,3) ÷3×π)@sin ( max ( 2 , 3 ) ÷ 3 × π )"})
    public void testParseLegalInfix(String actual, String expected) {
        assertEquals(expected, normalizer.normalize(actual));
    }

    @ParameterizedTest
    //@Disabled
    @DisplayName("test stream infix ok")
    @CsvSource(delimiter = '@',
               value = {"6^4*[(8!+3)/(8%3)]+1@6 ^ 4 * [ ( 8 ! + 3 ) / ( 8 % 3 ) ] + 1",
                        "2^4*[(8!+3)/(8%3)]+1@2 ^ 4 * [ ( 8 ! + 3 ) / ( 8 % 3 ) ] + 1",
                        "2 ^ 4 * [ ( 8 + 3 ) ]@2 ^ 4 * [ ( 8 + 3 ) ]",
                        "2^4 * 8@2 ^ 4 * 8",
                        "3 +4 *2 /(1 - 5) ^ 2  ^3@3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3",
                        "3 +4 ×2÷( 1 − 5)^ 2 ^3@3 + 4 × 2 ÷ ( 1 − 5 ) ^ 2 ^ 3",
                        "3+4×2÷( 1 − 5 )^2^3@3 + 4 × 2 ÷ ( 1 − 5 ) ^ 2 ^ 3",
                        "sin( max( 2 ,3) ÷3×π)@sin ( max ( 2 , 3 ) ÷ 3 × π )"})
    public void testStreamLegalInfix(String actual, String expected) {
        assertEquals(expected, normalizer.stream(actual).collect(Collectors.joining(" ")));
    }

    @ParameterizedTest
    //@Disabled
    @DisplayName("test parse illegal infix throws")
    @CsvSource({"null",
                "2s ^ 4 * [ ( 8 + 3 ) ]",
                "a2^4 * 8@2 ^ 4 * 8",
                "3 +4 *2x /(1v -  5v) ^ 2  ^3",
                "3c+4 ×2÷( 1 − 5)^ 2 ^3",
                "3s+4×2÷( 1 − 5 )^2^3",
                "mix( max( 2 ,3) ÷3×π)"})
    public void testParseIllegalInfix(String infix) {
        assertThrows(IllegalArgumentException.class, () -> normalizer.normalize(infix));
    }

    @ParameterizedTest
    //@Disabled
    @DisplayName("test stream illegal infix throws")
    @CsvSource({"null",
                "2s ^ 4 * [ ( 8 + 3 ) ]",
                "a2^4 * 8@2 ^ 4 * 8",
                "3 +4 *2x /(1v -  5v) ^ 2  ^3",
                "3c+4 ×2÷( 1 − 5)^ 2 ^3",
                "3s+4×2÷( 1 − 5 )^2^3",
                "mix( max( 2 ,3) ÷3×π)"})
    public void testStreamIllegalInfix(String infix) {
        assertThrows(IllegalArgumentException.class, () -> normalizer.stream(infix));
    }

    @Test
    //@Disabled
    @DisplayName("test parse null infix throws")
    public void testParseNullInfixThrows() {
        assertThrows(IllegalArgumentException.class, () -> normalizer.normalize(null));
    }

    @Test
    //@Disabled
    @DisplayName("test stream null infix throws")
    public void testStreamNullInfixThrows() {
        assertThrows(IllegalArgumentException.class, () -> normalizer.stream(null));
    }

    @Test
    //@Disabled
    @DisplayName("test parse blank infix throws")
    public void testParseBlankInfixThrows() {
        assertThrows(IllegalArgumentException.class, () -> normalizer.normalize(""));
    }

    @Test
    //@Disabled
    @DisplayName("test stream blank infix throws")
    public void testStreamBlankInfixThrows() {
        assertThrows(IllegalArgumentException.class, () -> normalizer.stream(""));
    }

    @Test
    //@Disabled
    @DisplayName("test parse white infix throws")
    public void testParseWhiteSpaceInfixThrows() {
        assertThrows(IllegalArgumentException.class, () -> normalizer.normalize("\t"));
    }

    @Test
    //@Disabled
    @DisplayName("test stream white infix throws")
    public void testStreamWhiteSpaceInfixThrows() {
        assertThrows(IllegalArgumentException.class, () -> normalizer.stream("\t"));
    }

}
