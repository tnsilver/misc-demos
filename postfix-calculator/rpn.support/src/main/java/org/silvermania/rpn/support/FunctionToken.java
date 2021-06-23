/*
 * File: FunctionToken.java
 * Creation Date: Jul 5, 2019
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

import static org.silvermania.rpn.support.Multiplicity.BINARY;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * The Class FunctionToken is a {@link Token} representing an arithmetic
 * function.
 *
 * @author T.N.Silverman
 */
public final class FunctionToken extends ArithmeticToken {

    /**
     * constant for function argument separator. For example, in the function
     * ({@code e.g. max(1,2)}, the comma is the separator between the operands 1 and
     * 2
     */
    public static final CharSequence FUNC_ARG_SEPARATOR = ",";

    /**
     * Instantiates a new function token.
     *
     * @param symbol the symbol
     * @param multiplicity the multiplicity
     * @param operation the operation
     */
    private FunctionToken(final CharSequence symbol, final Multiplicity multiplicity,
            final Function<BigDecimal[], BigDecimal> operation) {
        super(symbol, multiplicity, operation);
    }

    /**
     * Instantiates a new function token.
     *
     * @param symbol the symbol
     * @param multiplicity the multiplicity
     * @param associativity the associativity
     * @param operation the operation
     */
    private FunctionToken(final CharSequence symbol, final Multiplicity multiplicity, Associativity associativity,
            final Function<BigDecimal[], BigDecimal> operation) {
        super(symbol, multiplicity, associativity, operation);
    }

    /**
     * factory method to create new a function.
     *
     * @param symbol a unique symbol representing the operator
     * @param multiplicity the operand multiplicity of the function, which is
     *        {@code UNARY} or {@code BINARY}
     * @param operation the binary operator representing the arithmetic operation
     *        this function has to apply to its operands
     * @return a new FunctionToken
     */
    public static FunctionToken create(CharSequence symbol, Multiplicity multiplicity,
            Function<BigDecimal[], BigDecimal> operation) {
        return new FunctionToken(symbol, multiplicity, operation);
    }

    /**
     * Creates the.
     *
     * @param symbol the symbol
     * @param multiplicity the multiplicity
     * @param associativity the associativity
     * @param operation the operation
     * @return the function token
     */
    public static FunctionToken create(CharSequence symbol, Multiplicity multiplicity, Associativity associativity,
            Function<BigDecimal[], BigDecimal> operation) {
        return new FunctionToken(symbol, multiplicity, associativity, operation);
    }

    /**
     * factory method to create new a function.
     *
     * @param symbol a unique symbol representing the operator
     * @param operation the binary operator representing the arithmetic operation
     *        this function has to apply to its operands
     * @return a new FunctionToken with default {@code BINARY} operand multiplicity
     */
    public static FunctionToken create(CharSequence symbol, Function<BigDecimal[], BigDecimal> operation) {
        return new FunctionToken(symbol, BINARY, operation);
    }
}
