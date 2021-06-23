/*
 * File: TokenUtil.java
 * Creation Date: Jun 22, 2019
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
package org.silvermania.rpn.support;

import static org.silvermania.rpn.support.FunctionToken.FUNC_ARG_SEPARATOR;
import static org.silvermania.rpn.support.Multiplicity.BINARY;
import static org.silvermania.rpn.support.Multiplicity.MULTI;
import static org.silvermania.rpn.support.Multiplicity.UNARY;

import java.text.DecimalFormatSymbols;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;


/**
 * The Class TokenUtil is a collection of utility methods to assist in
 * identifying a {@link CharSequence} token being sequentially read left to
 * right form an infix arithmetic expression. Most methods of this class accept
 * a {@link CharSequence} token and determine if it answers to this or that
 * {@link Predicate} condition.
 *
 * @author T.N.Silverman
 */
public final class TokenUtil {

    /** The opening parentheses constant. */
    public static final CharSequence OPENING_PARENTHESES = "(";

    /** The closing parentheses constant. */
    public static final CharSequence CLOSING_PARENTHESES = ")";

    /** The opening square bracket constant. */
    public static final CharSequence OPENING_SQUARE_BRACKET = "[";

    /** The closing square bracket constant. */
    public static final CharSequence CLOSING_SQUARE_BRACKET = "]";

    /** The opening curly bracket constant. */
    public static final CharSequence OPENING_CURLY_BRACKET = "{";

    /** The closing curly bracket constant. */
    public static final CharSequence CLOSING_CURLY_BRACKET = "}";

    /*
     * Predicate creation functions (parameterized lambda expressions) to help
     * keep consistency with {@link Predicate} evaluations
     */

    private static Function<CharSequence, Predicate<OperatorToken>> equality =
        token -> operator -> operator.getSymbol().equals(token);

    private static Function<CharSequence, Predicate<OperatorToken>> binarity =
        token -> operator -> operator.getMultiplicity() == BINARY;

    private static Function<CharSequence,
            Predicate<OperatorToken>> multiplicity =
                token -> operator -> operator
                        .getMultiplicity() == MULTI;

    /** The unarity. */
    private static Function<CharSequence, Predicate<OperatorToken>> unarity =
        token -> operator -> operator.getMultiplicity() == UNARY;

    /** The left associativity. */
    private static Function<CharSequence,
            Predicate<OperatorToken>> leftAssociativity =
                token -> operator -> operator
                        .getAssociativity() == Associativity.LEFT;

    /** The right associativity. */
    private static Function<CharSequence,
            Predicate<OperatorToken>> rightAssociativity =
                token -> operator -> operator
                        .getAssociativity() == Associativity.RIGHT;

    /** The left right associativity. */
    /*
     * private static Function<CharSequence, Predicate<OperatorToken>>
     * leftRightAssociativity = token -> operator -> operator
     * .getAssociativity() == Associativity.LEFT_RIGHT;
     */

    /** The right left associativity. */
    /*
     * private static Function<CharSequence, Predicate<OperatorToken>>
     * rightLeftAssociativity = token -> operator -> operator
     * .getAssociativity() == Associativity.RIGHT_LEFT;
     */

    /**
     * Instantiates a new token util.
     */
    private TokenUtil() {
        super();
    }

    /**
     * returns the {@link OperandToken} created by a token representing a
     * constant.
     *
     * @param token the given token representing a registered mathematical
     *        constant
     * @param context the calculation context on which to look for the constant
     * @return OperandToken with a value corresponding to the registered
     *         constant
     * @throws NoSuchElementException if the given {@code token} does not
     *         represent a registered constant
     */
    public static OperandToken getConstant(CharSequence token,
            CalculationContext context) {
        return context.getConstantsRegistry().entrySet().stream()
                .filter(e -> e.getKey().equals(token))
                .map(e -> OperandToken.create(e.getValue().toString(), context))
                .findAny().orElseThrow();
    }

    /**
     * returns true if the given {@code token} is any of the constants in
     * {@code org.silvermania.rpn.support.OperandToken.CONSTANT_REGISTRY}
     *
     * @param token the given token
     * @param context the calculation context on which to look for the constant
     * @return true if the given {@code token} is any of the constants in the
     *         constants registry
     */
    public static boolean isNumericConstant(CharSequence token,
            CalculationContext context) {
        return context.getConstantsRegistry().keySet().stream()
                .anyMatch(k -> k.equals(token));
    }

    /**
     * returns true if the given {@code token} is a comma.
     *
     * @param token the token
     * @return true if the given {@code token} is a comma, otherwise false
     */
    public static boolean isFunctionArgSeparator(CharSequence token) {
        return Objects.requireNonNullElse(token, "").equals(FUNC_ARG_SEPARATOR);
    }

    /**
     * return true if the given {@code token} is an opening parentheses, square
     * bracket or curly bracket (e.g.'(,[,{')
     *
     * @param token the token
     *
     * @return true if the given {@code token} is an opening parentheses, square
     *         bracket or curly bracket, otherwise false
     */
    public static boolean isOpener(CharSequence token) {
        return Objects.requireNonNullElse(token, "").equals(OPENING_PARENTHESES)
                || token.equals(OPENING_SQUARE_BRACKET)
                || token.equals(OPENING_CURLY_BRACKET);
    }

    /**
     * return true if the given {@code token} is a closing parentheses, square
     * bracket or curly bracket (e.g.'(,[,{')
     *
     * @param token the token
     *
     * @return true if the given {@code token} is a closing parentheses, square
     *         bracket or curly bracket, otherwise false
     */
    public static boolean isClosing(CharSequence token) {
        return Objects.requireNonNullElse(token, "").equals(CLOSING_PARENTHESES)
                || token.equals(CLOSING_SQUARE_BRACKET)
                || token.equals(CLOSING_CURLY_BRACKET);
    }

    /**
     * return true if the given {@code token} is an opening parentheses (e.g.
     * {@code '('})
     *
     * @param token the token
     *
     * @return true if the given {@code token} is an opening parentheses,
     *         otherwise false
     */
    public static boolean isOpeningParentheses(CharSequence token) {
        return Objects.requireNonNullElse(token, "")
                .equals(OPENING_PARENTHESES);
    }

    /**
     * return true if the given {@code token} is a closing parentheses (e.g.
     * {@code ')'})
     *
     * @param token the token
     *
     * @return true if the given {@code token} is a closing parentheses,
     *         otherwise false
     */
    public static boolean isClosingParentheses(CharSequence token) {
        return Objects.requireNonNullElse(token, "")
                .equals(CLOSING_PARENTHESES);
    }

    /**
     * return true if the given {@code token} is an opening square bracket (e.g.
     * {@code '['})
     *
     * @param token the token
     *
     * @return true if the given {@code token} is an opening square bracket,
     *         otherwise false
     */
    public static boolean isOpeningBracket(CharSequence token) {
        return Objects.requireNonNullElse(token, "")
                .equals(OPENING_SQUARE_BRACKET);
    }

    /**
     * return true if the given {@code token} is a closing square bracket (e.g.
     * {@code ']'})
     *
     * @param token the token
     *
     * @return true if the given {@code token} is a closing square bracket,
     *         otherwise false
     */
    public static boolean isClosingBracket(CharSequence token) {
        return Objects.requireNonNullElse(token, "")
                .equals(CLOSING_SQUARE_BRACKET);
    }

    /**
     * return true if the given {@code token} is an opening curly bracket (e.g.
     * '{')
     *
     * @param token the token
     *
     * @return true if the given {@code token} is a closing curly bracket,
     *         otherwise false
     */
    public static boolean isOpeningCurlyBracket(CharSequence token) {
        return Objects.requireNonNullElse(token, "")
                .equals(OPENING_CURLY_BRACKET);
    }

    /**
     * return true if the given {@code token} is a closing curly bracket (e.g.
     * '}')
     *
     * @param token the token
     *
     * @return true if the given {@code token} is a closing curly bracket,
     *         otherwise false
     */
    public static boolean isClosingCurlyBracket(CharSequence token) {
        return Objects.requireNonNullElse(token, "")
                .equals(CLOSING_CURLY_BRACKET);
    }

    /**
     * returns true if a {@link FunctionToken} is represented by the token, that
     * is if the {@code token} is identical to a function symbol.
     *
     * @param token the given token to query (a function name in case of a
     *        function)
     * @param context the calculation context on which to look for the function
     * @return true if a {@link FunctionToken} is represented by the token, that
     *         is if the {@code token} is identical to a function symbol,
     *         otherwise false
     */
    public static boolean isFunction(CharSequence token,
            CalculationContext context) {
        return context.getFunctionsRegistry().stream()
                .anyMatch(f -> f.getSymbol().equals(token));
    }

    /**
     * returns true if an {@link OperatorToken} is represented by the token,
     * that is if the {@code token} is identical to an operator symbol.
     *
     * @param token the given token to query (an arithmetic symbol in case of an
     *        operator)
     * @param context the calculation context on which to look for the function
     * @return true if an {@link OperatorToken} is represented by the token,
     *         that is if the {@code token} is identical to an operator symbol,
     *         otherwise false
     */
    public static boolean isOperator(CharSequence token,
            CalculationContext context) {
        return context.getOperatorRegistry().stream()
                .anyMatch(equality.apply(token));
    }

    /**
     * returns the token matching operator or function or throws if none found.
     *
     * @param token the token representing the symbol of the operator or
     *        function
     * @param context the calculation context on which to look for the
     *        arithmetic operator
     * @return the token matching operator or function or throws if none found
     *         throws NoSuchElementException if no symbol matching operator or
     *         function is found
     */
    public static ArithmeticToken getArithmeticToken(CharSequence token,
            CalculationContext context) {
        return Stream
                .concat(context.getOperatorRegistry().stream(),
                        context.getFunctionsRegistry().stream())
                .filter(t -> t.getSymbol().equals(token)).findAny()
                .orElseThrow();
    }

    /**
     * returns true if the token matches an operator or a function or else
     * false.
     *
     * @param token the token matching operator or function or throws if none
     *        found throws NoSuchElementException if no symbol
     * @param context the calculation context on which to look for the
     *        arithmetic operator
     * @return true if the token matches an operator or a function or else false
     */
    public static boolean isArithmeticToken(CharSequence token,
            CalculationContext context) {
        return Stream
                .concat(context.getOperatorRegistry().stream(),
                        context.getFunctionsRegistry().stream())
                .anyMatch(t -> t.getSymbol().equals(token));
    }

    /**
     * returns true if the token matches a binary operator or a binary function,
     * otherwise false.
     *
     * @param token the token matching operator or function
     * @param context the calculation context on which to look for the
     *        arithmetic operator
     * @return true if the token matches a binary operator or a binary function,
     *         otherwise false
     */
    public static boolean isBinaryArithmeticToken(CharSequence token,
            CalculationContext context) {
        return Stream
                .concat(context.getOperatorRegistry().stream(),
                        context.getFunctionsRegistry().stream())
                .anyMatch(t -> t.getSymbol().equals(token)
                        && t.getMultiplicity() == BINARY);
    }

    /**
     * returns true if the token matches a many arguments function, otherwise
     * false.
     *
     * @param token the token matching operator or function
     * @param context the calculation context on which to look for the
     *        arithmetic operator
     * @return true if the token matches a multiple arguments function,
     *         otherwise false
     */
    public static boolean isMultiArgArithmeticToken(CharSequence token,
            CalculationContext context) {
        return context.getFunctionsRegistry().stream()
                .anyMatch(t -> t.getSymbol().equals(token)
                        && t.getMultiplicity() == MULTI);
    }

    /**
     * returns true if the token matches an unary operator or an unary function,
     * otherwise false.
     *
     * @param token the token matching operator or function
     * @param context the calculation context on which to look for the
     *        arithmetic operator
     * @return true if the token matches an unary operator or an unary function,
     *         otherwise false
     */
    public static boolean isUnaryArithmeticToken(CharSequence token,
            CalculationContext context) {
        return Stream
                .concat(context.getOperatorRegistry().stream(),
                        context.getFunctionsRegistry().stream())
                .anyMatch(t -> t.getSymbol().equals(token)
                        && t.getMultiplicity() == UNARY);
    }

    /**
     * returns true if the {@link OperatorToken} represented by the token is
     * {@code BINARY}.
     *
     * @param token the given token to query (an arithmetic symbol in case of an
     *        operator)
     * @param context the calculation context on which to look for the operator
     * @return true if the {@link OperatorToken} represented by the token is
     *         {@code BINARY}, otherwise false
     */
    public static boolean isBinaryOperator(CharSequence token,
            CalculationContext context) {
        return context.getOperatorRegistry().stream()
                .anyMatch(equality.apply(token).and(binarity.apply(token)));
    }

    /**
     * returns true if the {@link OperatorToken} represented by the token is a
     * many operands operator.
     *
     * @param token the given token to query (an arithmetic symbol in case of an
     *        operator)
     * @param context the calculation context on which to look for the operator
     * @return true if the {@link OperatorToken} represented by the token is a
     *         many operands operator, otherwise false
     */
    public static boolean isMultiOperator(CharSequence token,
            CalculationContext context) {
        return context.getOperatorRegistry().stream()
                .anyMatch(equality.apply(token).and(multiplicity.apply(token)));
    }

    /**
     * returns true if the {@link OperatorToken} represented by the token is
     * {@code UNARY}.
     *
     * @param token the given token to query (an arithmetic symbol in case of an
     *        operator)
     * @param context the calculation context on which to look for the operator
     * @return true if the {@link OperatorToken} represented by the token is
     *         {@code UNARY}, otherwise false
     */
    public static boolean isUnaryOperator(CharSequence token,
            CalculationContext context) {
        return context.getOperatorRegistry().stream()
                .anyMatch(equality.apply(token).and(unarity.apply(token)));
    }

    /**
     * returns true if the {@link OperatorToken} represented by the token is
     * {@code UNARY} and has a {@code RIGHT} associativity.
     *
     * @param token the given token to query (an arithmetic symbol in case of an
     *        operator)
     * @param context the calculation context on which to look for the operator
     * @return true if the {@link OperatorToken} represented by the token is
     *         {@code UNARY} and has a {@code RIGHT} associativity, otherwise
     *         false
     */
    public static boolean isPostfixUnaryOperator(CharSequence token,
            CalculationContext context) {
        return context.getOperatorRegistry().stream()
                .anyMatch(equality.apply(token).and(unarity.apply(token))
                        .and(rightAssociativity.apply(token)));
    }

    /**
     * returns true if the {@link OperatorToken} represented by the token is
     * {@code UNARY} and has a {@code LEFT} associativity.
     *
     * @param token the given token to query (an arithmetic symbol in case of an
     *        operator)
     * @param context the calculation context on which to look for the operator
     * @return true if the {@link OperatorToken} represented by the token is
     *         {@code UNARY} and has a {@code LEFT} associativity, otherwise
     *         false
     */
    public static boolean isPrefixUnaryOperator(CharSequence token,
            CalculationContext context) {
        return context.getOperatorRegistry().stream()
                .anyMatch(equality.apply(token).and(unarity.apply(token))
                        .and(leftAssociativity.apply(token)));
    }

    /**
     * returns true if the {@link OperatorToken} represented by the token has a
     * {@code LEFT} associativity.
     *
     * @param token the given token to query (an arithmetic symbol in case of an
     *        operator)
     * @param context the calculation context on which to look for the operator
     * @return true if the {@link OperatorToken} represented by the token has a
     *         {@code LEFT} associativity, otherwise false
     */
    public static boolean isPrefixBinaryOperator(CharSequence token,
            CalculationContext context) {
        return context.getOperatorRegistry().stream()
                .anyMatch(equality.apply(token).and(binarity.apply(token))
                        .and(leftAssociativity.apply(token)
                        /* .or(leftRightAssociativity.apply(token)) */));
    }

    /**
     * returns true if the {@link OperatorToken} represented by the token has a
     * {@code LEFT} or {@code LEFT_RIGHT} associativity and has many operands.
     *
     * @param token the given token to query (an arithmetic symbol in case of an
     *        operator)
     * @param context the calculation context on which to look for the operator
     * @return true if the {@link OperatorToken} represented by the token has a
     *         {@code LEFT} or {@code LEFT_RIGHT} associativity and has many
     *         operands, otherwise false
     */
    public static boolean isPrefixMultiOperator(CharSequence token,
            CalculationContext context) {
        return context.getOperatorRegistry().stream()
                .anyMatch(equality.apply(token).and(multiplicity.apply(token))
                        .and(leftAssociativity.apply(token)
                        /* .or(leftRightAssociativity.apply(token)) */));
    }

    /**
     * returns true if the {@link OperatorToken} represented by the token has a
     * {@code RIGHT} associativity.
     *
     * @param token the given token to query (an arithmetic symbol in case of an
     *        operator)
     * @param context the calculation context on which to look for the operator
     * @return true if the {@link OperatorToken} represented by the token has a
     *         RIGHT associativity, otherwise false
     */
    public static boolean isPostfixBinaryOperator(CharSequence token,
            CalculationContext context) {
        return context.getOperatorRegistry().stream()
                .anyMatch(equality.apply(token).and(binarity.apply(token))
                        .and(rightAssociativity.apply(token)
                        /* .or(rightLeftAssociativity.apply(token)) */));
    }

    /**
     * returns true if the {@link OperatorToken} represented by the token has a
     * {@code RIGHT_LEFT} associativity and is a many operands operator.
     *
     * @param token the given token to query (an arithmetic symbol in case of an
     *        operator)
     * @param context the calculation context on which to look for the operator
     * @return true if the {@link OperatorToken} represented by the token has a
     *         {@code RIGHT_LEFT} associativity and is a many operands operator,
     *         otherwise false.
     */
    public static boolean isPostfixMultiOperator(CharSequence token,
            CalculationContext context) {
        return context.getOperatorRegistry().stream()
                .anyMatch(equality.apply(token).and(multiplicity.apply(token))
                        .and(rightAssociativity.apply(token)
                        /* .or(rightLeftAssociativity.apply(token)) */));
    }

    /**
     * returns the precedence of the {@link OperatorToken} represented by the
     * given token.
     *
     * @param token the given token to query (an arithmetic symbol in case of an
     *        operator)
     * @param context the calculation context on which to look for the operator
     * @return the precedence of the operator represented by the given
     *         {@code token}
     * @throws NoSuchElementException if the token does not represent the symbol
     *         of a known (registered) {@link OperatorToken}
     */
    public static Precedence getOperatorPrecedence(CharSequence token,
            CalculationContext context) throws NoSuchElementException {
        return context.getOperatorRegistry().stream()
                .filter(equality.apply(token)).map(OperatorToken::getPrecedence)
                .findFirst().orElseThrow();
    }

    /**
     * returns the {@link OperatorToken} represented by the given token.
     *
     * @param token the given token to query (an arithmetic symbol in case of an
     *        operator)
     * @param context the calculation context on which to look for the operator
     * @return the operator represented by the given {@code token}
     * @throws NoSuchElementException if the token does not represent the symbol
     *         of a known (registered) {@link OperatorToken}
     */
    public static OperatorToken getOperator(CharSequence token,
            CalculationContext context) throws NoSuchElementException {
        return context.getOperatorRegistry().stream()
                .filter(equality.apply(token)).findAny().orElseThrow();
    }

    /**
     * returns the {@link Associativity} of the {@link OperatorToken}
     * represented by the given token.
     *
     * @param token the given token to query (an arithmetic symbol in case of an
     *        operator)
     * @param context the calculation context on which to look for the operator
     * @return the associativity of the operator represented by the given
     *         {@code token}
     * @throws NoSuchElementException if the token does not represent the symbol
     *         of a known (registered) {@link OperatorToken}
     */
    public static Associativity getOperatorAssociativity(CharSequence token,
            CalculationContext context) throws NoSuchElementException {
        return context.getOperatorRegistry().stream()
                .filter(equality.apply(token))
                .map(OperatorToken::getAssociativity).findAny().orElseThrow();
    }

    /**
     * returns true if the token is a numeric value or an arithmetic constant.
     *
     * @param token the given token to query (an arithmetic symbol in case of an
     *        operator or name of a function)
     * @param context the calculation context on which to look for the operand
     * @return true if the token represents a numeric or arithmetic constant
     *         expression
     */
    public static boolean isOperand(CharSequence token,
            CalculationContext context) {
        if (null == token) {
            throw new IllegalArgumentException(
                    "argument 'token' cannot be null");
        }
        DecimalFormatSymbols currentLocaleSymbols =
            DecimalFormatSymbols.getInstance();
        char localeMinusSign = currentLocaleSymbols.getMinusSign();
        if (isNumericConstant(token, context)) {
            return true;
        }
        if (token.equals("-") || token.equals("−")
                || !Character.isDigit(token.charAt(0))
                        && token.charAt(0) != localeMinusSign) {
            return false;
        }
        boolean isDecimalSeparatorFound = false;
        char localeDecimalSeparator =
            currentLocaleSymbols.getDecimalSeparator();
        for (char c : token.toString().substring(1).toCharArray()) {
            if (!Character.isDigit(c)) {
                if (c == localeDecimalSeparator && !isDecimalSeparatorFound) {
                    isDecimalSeparatorFound = true;
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    /**
     * returns true if the given {@code token} is a known token type, otherwise
     * false.
     *
     * @param token the token to check
     * @param context the calculation context on which to look for the token
     * @return true if the given {@code token} is a known token type, otherwise
     *         false
     */
    public static boolean isToken(CharSequence token,
            CalculationContext context) {
        return isOperand(token, context)
                || isArithmeticToken(token, context)
                || isOpener(token)
                || isClosing(token)
                || isFunctionArgSeparator(token);
    }

}
