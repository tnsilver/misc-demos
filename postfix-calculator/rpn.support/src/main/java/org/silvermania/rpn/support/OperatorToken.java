/*
 * File: OperatorToken.java
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

import static org.silvermania.rpn.support.Associativity.LEFT;
import static org.silvermania.rpn.support.Multiplicity.BINARY;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * The Class OperatorToken is a {@link Token} representing an arithmetic
 * operator.
 *
 * @author T.N.Silverman
 */
public final class OperatorToken extends ArithmeticToken {

    /**
     * The precedence of the operator.
     *
     * @see Precedence
     */
    private Precedence precedence;

    /**
     * Instantiates a new operator token.
     *
     * @param symbol the symbol
     * @param precedence the precedence
     * @param associativity the associativity
     * @param operation the operation
     */
    private OperatorToken(final CharSequence symbol, final Precedence precedence, final Associativity associativity,
            final Function<BigDecimal[], BigDecimal> operation) {
        super(symbol, BINARY, operation);
        this.precedence = precedence;
        this.associativity = associativity;
    }

    /**
     * Instantiates a new operator token.
     *
     * @param symbol the symbol
     * @param precedence the precedence
     * @param associativity the associativity
     * @param multiplicity the multiplicity
     * @param operation the operation
     */
    private OperatorToken(final CharSequence symbol, final Precedence precedence, final Associativity associativity,
            final Multiplicity multiplicity, final Function<BigDecimal[], BigDecimal> operation) {
        super(symbol, multiplicity, operation);
        this.precedence = precedence;
        this.associativity = associativity;
    }

    /**
     * factory method for creation of an operator token.
     *
     * @param symbol a unique symbol representing the operator
     * @param precedence the precedence of the operator
     * @param associativity the associativity of the operator to define how it
     *        relates to it's operands
     * @param multiplicity the multiplicity of the operator, which is {@code UNARY}
     *        or {@code BINARY}
     * @param operation the arithmetic operation, expresses as a {@code BiFunction}
     *        to invoke on the operand/operands of this operator
     * @return a new OperatorToken
     */
    public static OperatorToken create(CharSequence symbol, Precedence precedence, Associativity associativity,
            Multiplicity multiplicity, Function<BigDecimal[], BigDecimal> operation) {
        return new OperatorToken(symbol, precedence, associativity, multiplicity, operation);
    }

    /**
     * factory method for creation of an operator token.
     *
     * @param symbol a unique symbol representing the operator
     * @param precedence the precedence of the operator
     * @param associativity the associativity of the operator to define how it
     *        relates to it's operands
     * @param operation the arithmetic operation, expresses as a {@code BiFunction}
     *        to invoke on the operand/operands of this operator
     * @return a new OperatorToken with default {@code BINARY} operand multiplicity
     */
    public static OperatorToken create(CharSequence symbol, Precedence precedence, Associativity associativity,
            Function<BigDecimal[], BigDecimal> operation) {
        return new OperatorToken(symbol, precedence, associativity, operation);
    }

    /**
     * factory method for creation of an operator token.
     *
     * @param symbol a unique symbol representing the operator
     * @param precedence the precedence of the operator
     * @param operation the arithmetic operation, expresses as a {@code BiFunction}
     *        to invoke on the operand/operands of this operator
     * @return a new OperatorToken with default {@code BINARY} operand multiplicity
     *         and default {@code LEFT} associativity
     */
    public static OperatorToken create(CharSequence symbol, Precedence precedence,
            Function<BigDecimal[], BigDecimal> operation) {
        return new OperatorToken(symbol, precedence, LEFT, operation);
    }

    /**
     * Gets the precedence.
     *
     * @return the precedence
     */
    public Precedence getPrecedence() {
        return precedence;
    }

    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((associativity == null) ? 0 : associativity.hashCode());
        result = prime * result + ((precedence == null) ? 0 : precedence.hashCode());
        return result;
    }

    /**
     * Equals.
     *
     * @param obj the obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        OperatorToken other = (OperatorToken) obj;
        if (associativity != other.associativity) {
            return false;
        }
        if (precedence == null) {
            if (other.precedence != null) {
                return false;
            }
        } else if (!precedence.equals(other.precedence)) {
            return false;
        }
        return true;
    }
}
