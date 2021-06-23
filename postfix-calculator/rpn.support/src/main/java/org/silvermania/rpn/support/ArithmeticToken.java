/*
 * File: ArithmeticToken.java
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

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * The Class ArithmeticToken is an intermediate type in the {@link Token}
 * hierarchy and adds a {@code BiConsumer} operation field to sub-classes. This
 * class is meant to be extended by {@code OperandToken} and
 * {@link FunctionToken}, which represent operands and functions. This way, an
 * RPN calculator, can, for example, refer to the {@link #getOperation()} in a
 * polymorphic manner, without retrospectively querying the exact type of the
 * token.
 * <p>
 * The {@code operation} represents the math operation this operand or function
 * token is applying to it's arguments.
 * <p>
 * Regardless of whether this is a {@code BINARY} or {@code UNARY} operand or
 * function, the operation is always a binary operator that accepts two
 * {@code Double} arguments. When one argument isn't required, such with the
 * case of an unary operator, null can be passed to the binary operator
 * operation.
 *
 * @author T.N.Silverman
 */
public abstract class ArithmeticToken extends Token {

    /**
     * The arithmetic operation to invoke on the operand/operands of this operator.
     */
    protected Function<BigDecimal[], BigDecimal> operation;

    /**
     * is this a LEFT or RIGHT associativity operator meaning does it apply to the
     * operand on it's left or it's right.
     */
    protected Associativity associativity;

    /**
     * Instantiates a new arithmetic token.
     *
     * @param symbol the symbol
     * @param multiplicity the multiplicity
     * @param operation the operation
     */
    public ArithmeticToken(final CharSequence symbol, final Multiplicity multiplicity,
            final Function<BigDecimal[], BigDecimal> operation) {
        super(symbol, multiplicity);
        this.associativity = Associativity.LEFT;
        this.operation = operation;
    }

    /**
     * Instantiates a new arithmetic token.
     *
     * @param symbol the symbol
     * @param multiplicity the multiplicity
     * @param associativity the associativity
     * @param operation the operation
     */
    public ArithmeticToken(final CharSequence symbol, final Multiplicity multiplicity,
            final Associativity associativity, final Function<BigDecimal[], BigDecimal> operation) {
        super(symbol, multiplicity);
        this.associativity = associativity;
        this.operation = operation;
    }

    /**
     * Instantiates a new arithmetic token.
     *
     * @return the operation
     */
    /*
     * public ArithmeticToken(final CharSequence symbol, final
     * Function<BigDecimal[], BigDecimal> operation) { super(symbol); this.operation
     * = operation; }
     */

    /**
     * accessor for the operation.
     *
     * @return the operation associated with this token
     */
    public Function<BigDecimal[], BigDecimal> getOperation() {
        return operation;
    }

    /**
     * accessor for the associativity.
     *
     * @return the associativity of this token
     */
    public Associativity getAssociativity() {
        return associativity;
    }

    /**
     * Hash code.
     *
     * @return the hash code for this token
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((associativity == null) ? 0 : associativity.hashCode());
        result = prime * result + ((operation == null) ? 0 : operation.hashCode());
        return result;
    }

    /**
     * Equals.
     *
     * @param obj the object to compare to
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ArithmeticToken other = (ArithmeticToken) obj;
        if (associativity != other.associativity) {
            return false;
        }
        if (operation == null) {
            if (other.operation != null) {
                return false;
            }
        } else if (!operation.equals(other.operation)) {
            return false;
        }
        return true;
    }
}
