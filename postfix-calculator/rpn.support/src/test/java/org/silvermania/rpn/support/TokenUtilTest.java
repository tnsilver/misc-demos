/*
 * File: TokenUtilTest.java
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
package org.silvermania.rpn.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.silvermania.rpn.support.TokenUtil.getArithmeticToken;
import static org.silvermania.rpn.support.TokenUtil.getOperator;
import static org.silvermania.rpn.support.TokenUtil.getOperatorAssociativity;
import static org.silvermania.rpn.support.TokenUtil.getOperatorPrecedence;
import static org.silvermania.rpn.support.TokenUtil.isArithmeticToken;
import static org.silvermania.rpn.support.TokenUtil.isBinaryArithmeticToken;
import static org.silvermania.rpn.support.TokenUtil.isBinaryOperator;
import static org.silvermania.rpn.support.TokenUtil.isClosing;
import static org.silvermania.rpn.support.TokenUtil.isClosingBracket;
import static org.silvermania.rpn.support.TokenUtil.isClosingCurlyBracket;
import static org.silvermania.rpn.support.TokenUtil.isClosingParentheses;
import static org.silvermania.rpn.support.TokenUtil.isFunction;
import static org.silvermania.rpn.support.TokenUtil.isFunctionArgSeparator;
import static org.silvermania.rpn.support.TokenUtil.isMultiArgArithmeticToken;
import static org.silvermania.rpn.support.TokenUtil.isMultiOperator;
import static org.silvermania.rpn.support.TokenUtil.isNumericConstant;
import static org.silvermania.rpn.support.TokenUtil.isOpener;
import static org.silvermania.rpn.support.TokenUtil.isOpeningBracket;
import static org.silvermania.rpn.support.TokenUtil.isOpeningCurlyBracket;
import static org.silvermania.rpn.support.TokenUtil.isOpeningParentheses;
import static org.silvermania.rpn.support.TokenUtil.isOperand;
import static org.silvermania.rpn.support.TokenUtil.isOperator;
import static org.silvermania.rpn.support.TokenUtil.isPostfixBinaryOperator;
import static org.silvermania.rpn.support.TokenUtil.isPostfixMultiOperator;
import static org.silvermania.rpn.support.TokenUtil.isPostfixUnaryOperator;
import static org.silvermania.rpn.support.TokenUtil.isPrefixBinaryOperator;
import static org.silvermania.rpn.support.TokenUtil.isPrefixMultiOperator;
import static org.silvermania.rpn.support.TokenUtil.isPrefixUnaryOperator;
import static org.silvermania.rpn.support.TokenUtil.isToken;
import static org.silvermania.rpn.support.TokenUtil.isUnaryArithmeticToken;
import static org.silvermania.rpn.support.TokenUtil.isUnaryOperator;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class TokenUtilTest is a unit test case to assert the functionality of
 * the {@link TokenUtil} class
 *
 * @author T.N.Silverman
 */
class TokenUtilTest {

    private static final Logger logger = LoggerFactory.getLogger(TokenUtilTest.class);
    private static CalculationContext context = CalculationContext.newInstance();

    @BeforeEach
    public void beforeEach(TestInfo info) throws Exception {
        logger.debug("entering {} {}", info.getTestMethod().orElseThrow().getName(), info.getDisplayName());
    }

    @ParameterizedTest
    @DisplayName("test is numeric")
    @CsvSource({"1,true",
                "-1,true",
                "12345678910111213141516,true",
                "-12345678910111213141516,true",
                "3.14,true",
                "π,true",
                "e,true",
                "a,false",
                "sin,false"})
    public void testIsNumeric(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isOperand(token, context));
    }

    @Test
    @DisplayName("test is operand with null")
    public void testIsOperandWithNull() throws Exception {
        CharSequence token = null;
        assertThrows(IllegalArgumentException.class, () -> isOperand(token, context));
    }

    @ParameterizedTest
    @DisplayName("test is token")
    @CsvSource(delimiter = '@',
               value = {"1@true", "π@true", "e@true", "sin@true", "(@true", "[@true", "[@true", ",@true"})
    public void testIsToken(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isToken(token, context));
    }

    @ParameterizedTest
    @DisplayName("test is numeric constant")
    @CsvSource({"π,true", "e,true", "1,false", "a,false", "sin,false"})
    public void testIsNumericConstant(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isNumericConstant(token, context));
    }

    @ParameterizedTest
    @DisplayName("test is function arg separator")
    @CsvSource(delimiter = '@',
               value = {",@true", "e@false", "1@false", "(@false", ")@false"})
    public void testIsFunctionArgeSeparator(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isFunctionArgSeparator(token));
    }

    @ParameterizedTest
    @DisplayName("test is opener")
    @CsvSource({"(,true", "[,true", "{,true", "1.2,false", "),false", "],false", "},false", "max,false"})
    public void testIsOpener(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isOpener(token));
    }

    @ParameterizedTest
    @DisplayName("test is closing")
    @CsvSource({"(,false", "[,false", "{,false", "),true", "],true", "},true", "1.2,false", "@,false",})
    public void testIsClosing(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isClosing(token));
    }

    @ParameterizedTest
    @DisplayName("test is opening parentheses")
    @CsvSource({"(,true", "[,false", "{,false", "1.2,false", "),false", "],false", "},false", "max,false"})
    public void testIsOpenParentheses(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isOpeningParentheses(token));
    }

    @ParameterizedTest
    @DisplayName("test is opening bracket")
    @CsvSource({"(,false", "[,true", "{,false", "1.2,false", "),false", "],false", "},false", "max,false"})
    public void testIsOpenBracket(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isOpeningBracket(token));
    }

    @ParameterizedTest
    @DisplayName("test is opening curly")
    @CsvSource({"(,false", "[,false", "{,true", "1.2,false", "),false", "],false", "},false", "max,false"})
    public void testIsOpenCurly(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isOpeningCurlyBracket(token));
    }

    @ParameterizedTest
    @DisplayName("test is closing parentheses")
    @CsvSource({"(,false", "[,false", "{,false", "1.2,false", "),true", "],false", "},false", "max,false"})
    public void testIsCloseParentheses(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isClosingParentheses(token));
    }

    @ParameterizedTest
    @DisplayName("test is closing bracket")
    @CsvSource({"(,false", "[,false", "{,false", "1.2,false", "),false", "],true", "},false", "max,false"})
    public void testIsCloseBracket(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isClosingBracket(token));
    }

    @ParameterizedTest
    @DisplayName("test is closing curly")
    @CsvSource({"(,false", "[,false", "{,false", "1.2,false", "),false", "],false", "},true", "max,false"})
    public void testIsCloseCurly(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isClosingCurlyBracket(token));
    }

    @Test
    @DisplayName("test is function with null")
    public void testIsFunctionWithNull() {
        assertFalse(isFunction(null, context));
    }

    @ParameterizedTest
    @DisplayName("test is function")
    @CsvSource({"sin,true",
                "cos,true",
                "min,true",
                "max,true",
                "avg,true",
                "log,true",
                "mod,false",
                "%,false",
                "*,false",
                "3.14,false"})
    public void testIsFunction(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isFunction(token, context));
    }

    @ParameterizedTest
    @DisplayName("test is operator")
    @CsvSource({"^,true",
                "!,true",
                "*,true",
                "×,true",
                "/,true",
                "÷,true",
                "%,true",
                "+,true",
                "−,true",
                "-,true",
                "sin,false",
                "cos,false",
                "min,false",
                "max,false",
                "avg,false",
                "log,false",
                "sqrt,false"})
    public void testIsOperator(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isOperator(token, context));
    }

    @ParameterizedTest
    @DisplayName("test is binary operator")
    @CsvSource({"^,true",
                "!,false",
                "*,true",
                "×,true",
                "/,true",
                "÷,true",
                "%,true",
                "+,true",
                "−,true",
                "-,true",
                "sin,false"})
    public void testIsBinaryOperator(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isBinaryOperator(token, context));
    }

    @ParameterizedTest
    @DisplayName("test is unary operator")
    @CsvSource({"!,true",
                "^,false",
                "*,false",
                "×,false",
                "/,false",
                "÷,false",
                "%,false",
                "mod,false",
                "+,false",
                "−,false",
                "-,false",
                "sin,false"})
    public void testIsUnaryOperator(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isUnaryOperator(token, context));
    }

    @ParameterizedTest
    @DisplayName("test is unary postdix operator")
    @CsvSource({"!,true"})
    public void testIsUnaryPostfixOperator(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isPostfixUnaryOperator(token, context));
    }

    @ParameterizedTest
    @DisplayName("test is unary prefix operator")
    @CsvSource({"!,false"})
    public void testIsUnaryPrefixOperator(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isPrefixUnaryOperator(token, context));
    }

    @ParameterizedTest
    @DisplayName("test is prefix operator")
    @CsvSource({"^,false",
                "!,false",
                "*,true",
                "×,true",
                "/,true",
                "÷,true",
                "%,true",
                "+,true",
                "−,true",
                "-,true"})
    public void testIsPrefixOperator(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isPrefixBinaryOperator(token, context));
    }

    @ParameterizedTest
    @DisplayName("test is postfix operator")
    @CsvSource({"^,true",
                "!,false",
                "*,false",
                "×,false",
                "/,false",
                "÷,false",
                "%,false",
                "mod,false",
                "+,false",
                "−,false",
                "-,false"})
    public void testIsPostfixOperator(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isPostfixBinaryOperator(token, context));
    }

    @ParameterizedTest
    @DisplayName("test get operator precedence")
    @CsvSource({"^,HIGHEST",
                "!,HIGH",
                "*,LOW",
                "×,LOW",
                "/,LOW",
                "÷,LOW",
                "%,LOW",
                "+,LOWEST",
                "−,LOWEST",
                "-,LOWEST"})
    public void testGetPrecedence(String token, Precedence expected) {
        assertEquals(expected, getOperatorPrecedence(token, context));
    }

    @ParameterizedTest
    @DisplayName("test get operator")
    @CsvSource({"^", "!", "*", "×", "/", "÷", "%", "+", "−", "-"})
    public void testGetOperator(String token) {
        assertNotNull(getOperator(token, context));
    }

    @Test
    @DisplayName("test get null operator")
    public void testGetNullOperator() {
        assertThrows(NoSuchElementException.class, () -> getOperator(null, context));
    }

    @ParameterizedTest
    @DisplayName("test get bogus operator")
    @CsvSource({"@", "$", ";", "blah"})
    public void testGetBogusOperator(String token) {
        assertThrows(NoSuchElementException.class, () -> getOperator(token, context));
    }

    @ParameterizedTest
    @DisplayName("test get function as operator")
    @CsvSource({"min", "max", "avg", "sin", "cos"})
    public void testGetFunctionAsOperator(String token) {
        assertThrows(NoSuchElementException.class, () -> getOperator(token, context));
    }

    @ParameterizedTest
    @DisplayName("test get function associativity")
    @CsvSource({"min", "max", "avg", "sin", "cos"})
    public void testGetFunctionAssociativity(String token) {
        assertThrows(NoSuchElementException.class, () -> getOperatorAssociativity(token, context));
    }

    @ParameterizedTest
    @DisplayName("test get associativity")
    @CsvSource({"^", "!", "*", "×", "/", "÷", "%", "+", "−", "-"})
    public void testGetAssociativity(String token) {
        assertNotNull(getOperatorAssociativity(token, context));
    }

    @ParameterizedTest
    @DisplayName("test is arithmetic token")
    @CsvSource({"^,true", "@,false", "sin,true", "power,false"})
    public void testIsArithmeticToken(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isArithmeticToken(token, context));
    }

    @ParameterizedTest
    @DisplayName("test is binary arithmetic token")
    @CsvSource({"^,true", "!,false", "max,true", "sin,false"})
    public void testIsBinaryArithmeticToken(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isBinaryArithmeticToken(token, context));
    }

    @ParameterizedTest
    @DisplayName("test is unary arithmetic token")
    @CsvSource({"^,false", "!,true", "max,false", "sin,true"})
    public void testIsUnaryArithmeticToken(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isUnaryArithmeticToken(token, context));
    }

    @ParameterizedTest
    @DisplayName("test is arithmetic token")
    @CsvSource({"^", "+", "!", "-", "/", "sin", "cos"})
    public void testGetArithmeticToken(String token) {
        assertNotNull(getArithmeticToken(token, context));
    }

    @ParameterizedTest
    @DisplayName("test is mutli operator token")
    @CsvSource({"bingo,false", "~,false", "max,false", "sin,false"})
    public void testIsMultiOperatorToken(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isMultiOperator(token, context));
    }

    @ParameterizedTest
    @DisplayName("test is prefix mutli operator token")
    @CsvSource({"bingo,false", "!,false", "max,false", "sin,false"})
    public void testIsPrefixMultiOperatorToken(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isPrefixMultiOperator(token, context));
    }

    @ParameterizedTest
    @DisplayName("test is postfix mutli operator token")
    @CsvSource({"bingo,false", "~,false", "max,false", "sin,false"})
    public void testIsPostfixMultiOperatorToken(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isPostfixMultiOperator(token, context));
    }

    @ParameterizedTest
    @DisplayName("test is mutli arg operator token")
    @CsvSource({"sum,true", "!,false", "max,false", "sin,false"})
    public void testIsMultiArgArithmeticToken(String token, String expected) {
        assertEquals(Boolean.valueOf(expected), isMultiArgArithmeticToken(token, context));
    }

}
