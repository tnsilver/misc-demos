/*
 * File: Token.java
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

import java.io.Serializable;

/**
 * The Class Token is base class for representing a token that is read left to
 * right from an infix expression. In an infix expression, the read token can be
 * evaluated to be an operand, a function argument separator, an opening or
 * closing bracket (curly, square or parentheses) or an arithmetic operator or
 * function.
 *
 * This class deals with operators and functions, as the rest of the tokens are
 * either numeric or constant literal values.
 *
 * @author T.N.Silverman
 */
public class Token implements Serializable {

    /**
     * The symbol (e.g. {@code +,-,/,*}) and etc... is a unique identifier of
     * the token.
     */
    private CharSequence symbol;

    /**
     * The operand multiplicity multiplicity is either {@code UNARY} or
     * {@code BINARY}.
     */
    private Multiplicity multiplicity;

    /**
     * Instantiates a new token.
     *
     * @param symbol the symbol
     */
    protected Token(final CharSequence symbol) {
        super();
        this.symbol = symbol;
    }

    /**
     * Instantiates a new token.
     *
     * @param symbol the symbol
     * @param multiplicity the multiplicity
     */
    protected Token(final CharSequence symbol,
            final Multiplicity multiplicity) {
        this(symbol);
        this.multiplicity = multiplicity;
    }

    /**
     * Gets the symbol.
     *
     * @return the symbol
     */
    public CharSequence getSymbol() {
        return symbol;
    }

    /**
     * Gets the multiplicity.
     *
     * @return the multiplicity
     */
    public Multiplicity getMultiplicity() {
        return multiplicity;
    }

    /**
     * Hash code.
     *
     * @return the hash code of this token
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
        result = prime * result
                + ((multiplicity == null) ? 0 : multiplicity.hashCode());
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
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Token other = (Token) obj;
        if (symbol != null) {
            if (other.symbol == null) {
                return false;
            }
        }
        if (symbol == null) {
            if (other.symbol != null) {
                return false;
            }
        }
        if (!symbol.toString().equals(other.symbol.toString())) {
            return false;
        }
        if (multiplicity != other.multiplicity) {
            return false;
        }
        return true;
    }
}
