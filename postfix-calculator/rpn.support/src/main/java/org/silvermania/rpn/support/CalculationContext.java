/*
 * File: CalculationContext.java
 * Creation Date: Jul 9, 2019
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

import static java.math.MathContext.DECIMAL128;
import static java.math.MathContext.DECIMAL32;
import static java.math.MathContext.DECIMAL64;
import static java.util.Map.entry;
import static java.util.stream.Collectors.joining;
import static org.silvermania.rpn.support.Associativity.LEFT;
import static org.silvermania.rpn.support.Associativity.RIGHT;
import static org.silvermania.rpn.support.Multiplicity.BINARY;
import static org.silvermania.rpn.support.Multiplicity.MULTI;
import static org.silvermania.rpn.support.Multiplicity.UNARY;
import static org.silvermania.rpn.support.Precedence.HIGH;
import static org.silvermania.rpn.support.Precedence.HIGHEST;
import static org.silvermania.rpn.support.Precedence.LOW;
import static org.silvermania.rpn.support.Precedence.LOWEST;
import static org.silvermania.rpn.support.TokenUtil.getOperator;
import static org.silvermania.rpn.support.TokenUtil.isFunction;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class CalculationContext is a registry storage for RPN infix and postfix
 * calculations related parameters.
 *
 * @author T.N.Silverman
 */
public final class CalculationContext implements Printable {

    /** The logger. */
    private static final Logger logger = LoggerFactory.getLogger(CalculationContext.class);

    /** The default rounding mode. */
    public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_EVEN;

    /** The default precision. */
    public static final int DEFAULT_PRECISION = 7;

    /**
     * the math context with which to apply division operations and functions on
     * {@code java.math.BigDecimal} operands
     */
    private MathContext mathContext;

    /**
     * A {@code CalculationContext} object with a precision setting matching the
     * IEEE 754R Decimal32 format, 7 digits, and a rounding mode of
     * {@link java.math.RoundingMode#HALF_EVEN HALF_EVEN}, the IEEE 754R default.
     */
    public static final CalculationContext DECIMAL32_CONTEXT = newInstance().withMathContext(DECIMAL32);
    /**
     * A {@code CalculationContext} object with a precision setting matching the
     * IEEE 754R Decimal64 format, 16 digits, and a rounding mode of
     * {@link java.math.RoundingMode#HALF_EVEN HALF_EVEN}, the IEEE 754R default.
     */
    public static final CalculationContext DECIMAL64_CONTEXT = newInstance().withMathContext(DECIMAL64);
    /**
     * A {@code CalculationContext} object with a precision setting matching the
     * IEEE 754R Decimal128 format, 34 digits, and a rounding mode of
     * {@link java.math.RoundingMode#HALF_EVEN HALF_EVEN}, the IEEE 754R default.
     */
    public static final CalculationContext DECIMAL128_CONTEXT = newInstance().withMathContext(DECIMAL128);

    /** The Constant HUNDRED used in percentage calculation. */
    public static final Double HUNDRED = Double.valueOf(100);

    /**
     * internal method to configure this instance {@code mathContext}.
     *
     * @param mathContext the new math context
     * @return this configured instance;
     */
    private CalculationContext withMathContext(MathContext mathContext) {
        this.mathContext = mathContext;
        return this;
    }

    /**
     * The default registered functions that are recognized by the
     * {@code InfixTokenHandler}.
     */
    private final List<FunctionToken> defaultFunctionRegistry = List.of(
            /* trigonometric sine of an angle */
            FunctionToken.create("sin", UNARY, (arr) -> round(Math.sin((Math.toRadians(arr[0].doubleValue()))))),
            /* trigonometric cosine of an angle */
            FunctionToken.create("cos", UNARY, (arr) -> round(Math.cos((Math.toRadians(arr[0].doubleValue()))))),
            /* trigonometric tangent of an angle */
            FunctionToken.create("tan", UNARY, (arr) -> round(Math.tan((Math.toRadians(arr[0].doubleValue()))))),
            /* minimal value in the arguments array */
            FunctionToken.create("min", BINARY, (arr) -> round(Math.min(arr[0].doubleValue(), arr[1].doubleValue()))),
            /* maximal value in the arguments array */
            FunctionToken.create("max", BINARY, (arr) -> round(Math.max(arr[0].doubleValue(), arr[1].doubleValue()))),
            /* average value of the arguments array */
            FunctionToken.create("avg", BINARY,
                    (arr) -> round(arr[0].add(arr[1]).divide(new BigDecimal(2), getPrecision(), getRoundingMode()))),
            /* percent value of a in b */
            FunctionToken
                    .create("pct", BINARY, RIGHT,
                            (arr) -> round(BigDecimal.valueOf(HUNDRED).divide(
                                    arr[0].divide(arr[1], getPrecision(), getRoundingMode()), getPrecision(),
                                    getRoundingMode()))),
            /* experimental sum function of multi arguments */
            FunctionToken.create("sum", MULTI,
                    (arr) -> round(Arrays.stream(arr).reduce(BigDecimal.ZERO, (a, b) -> a.add(b)))),
            /* rounded base 10 logarithm of a */
            FunctionToken.create("log", UNARY, (arr) -> {
                if (arr[0].compareTo(BigDecimal.ZERO) <= 0) {
                   throw new IllegalArgumentException("log base 10 argument cannot be equal or less than 0!");
                }
                return round(Math.log10(arr[0].doubleValue()));
            })

    );

    /** The default operators registry. */
    private final List<OperatorToken> defaultOperatorsRegistry = List.of(
            /* a in the power of b */
            OperatorToken.create("^", HIGHEST, RIGHT, (arr) -> round(arr[0].pow(arr[1].intValue()))),
            /* a factorial */
            OperatorToken.create("!", HIGH, RIGHT, Multiplicity.UNARY, (arr) -> {
                if (arr[0].compareTo(BigDecimal.valueOf(20)) > 0 || arr[0].compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException(arr[0].toString() + " is out of range!");
                }
                return BigDecimal.valueOf((LongStream.rangeClosed(1, arr[0].longValue()).reduce(1, (x, y) -> x * y)))
                        .setScale(mathContext.getPrecision());
            }),
            /* square root of a */
            OperatorToken.create("√", HIGH, LEFT, Multiplicity.UNARY, (arr) -> round(Math.sqrt(arr[0].doubleValue()))),
            /* a multiply by b */
            OperatorToken.create("*", LOW, (arr) -> round(arr[0].multiply(arr[1]))),
            /* a multiply by b */
            OperatorToken.create("×", LOW, (arr) -> round(arr[0].multiply(arr[1]))),
            /* a divide by b */
            OperatorToken.create("/", LOW, LEFT,
                    (arr) -> round(arr[0].divide(arr[1], getPrecision(), getRoundingMode()))),
            /* a divide by b */
            OperatorToken.create("÷", LOW, LEFT,
                    (arr) -> round(arr[0].divide(arr[1], getPrecision(), getRoundingMode()))),
            /* remainder of a divide by b */
            OperatorToken.create("%", LOW, LEFT, (arr) -> round(arr[0].remainder(arr[1]))),
            /* a plus b */
            OperatorToken.create("+", LOWEST, LEFT, (arr) -> round(arr[0].add(arr[1]))),
            /* a minus b */
            OperatorToken.create("−", LOWEST, LEFT, (arr) -> round(arr[0].subtract(arr[1]))),
            /* a minus b */
            OperatorToken.create("-", LOWEST, LEFT, (arr) -> round(arr[0].subtract(arr[1]))));

    /** the registry of default constants and their values as big decimals. */
    private final Map<CharSequence,
            BigDecimal> defaultConstantsRegistry = Map.ofEntries(entry("π", new BigDecimal(Math.PI)), // PI
                    entry("PI", new BigDecimal(Math.PI)), // PI
                    entry("e", new BigDecimal(2.71828))// base of the natural logarithm
    );

    /** The functions registry. */
    private List<FunctionToken> functionsRegistry;

    /** The operator registry. */
    private List<OperatorToken> operatorRegistry;

    /** The constants registry. */
    private Map<CharSequence, BigDecimal> constantsRegistry;

    /**
     * Instantiates a new calculation context.
     */
    private CalculationContext() {
        super();
        this.mathContext = new MathContext(DEFAULT_PRECISION, DEFAULT_ROUNDING_MODE);
        this.functionsRegistry = defaultFunctionRegistry;
        this.operatorRegistry = defaultOperatorsRegistry;
        this.constantsRegistry = defaultConstantsRegistry;
    }

    /**
     * A factory method to create a {@code CalculationContext} object with a
     * precision setting of 7 digits, and a rounding mode of
     * {@link java.math.RoundingMode#HALF_UP HALF_UP}. This is a Rounding mode to
     * round towards {@literal "nearest neighbor"} unless both neighbors are
     * equidistant, in which case round up. Behaves as for
     * {@code java.math.RoundingMode.UP} if the discarded fraction is &ge; 0.5;
     * otherwise, behaves as for {@code RoundingMode.DOWN}. <br>
     * Note that this is the rounding mode commonly taught at school.
     *
     * @return new CalculationContext
     */
    public static CalculationContext newInstance() {
        return new CalculationContext();
    }

    /**
     * This method allows users to register new functions that are not yet defined
     * in the default {@link #defaultFunctionRegistry}. Use of this method must be
     * done before the conversion begins.
     *
     * @param symbol a unique symbol representing the operator
     * @param multiplicity the multiplicity of the function, which is {@code UNARY}
     *        or {@code BINARY}
     * @param operation the binary operator representing the arithmetic operation
     *        this function has to apply to its operands
     * @return this calculation context for chainability
     * @throws IllegalArgumentException if the function identified by its
     *         {@code symbol} is already in the registry
     */
    public CalculationContext registerFunction(CharSequence symbol, Multiplicity multiplicity,
            Function<BigDecimal[], BigDecimal> operation) throws IllegalArgumentException {
        if (!isFunction(symbol, this)) {
            List<FunctionToken> registry = new LinkedList<>();
            registry.addAll(defaultFunctionRegistry);
            registry.addAll(functionsRegistry);
            registry.add(FunctionToken.create(symbol, multiplicity, operation));
            functionsRegistry = Collections.unmodifiableList(registry);
            logger.debug("registered function '" + symbol + "'");
            return this;
        } else {
            throw new IllegalArgumentException(String.format("function %s is already registered", symbol));
        }
    }

    /**
     * This method allows users to register new operators that are not yet defined
     * in the default {@link CalculationContext#defaultOperatorsRegistry}. Use of
     * this method must be done before the conversion begins.
     *
     * @param symbol a unique symbol representing the operator
     * @param precedence the precedence of the operator
     * @param associativity the associativity of the operator to define how it
     *        relates to it's operands
     * @param multiplicity the multiplicity of the operator, which is {@code UNARY}
     *        or {@code BINARY}
     * @param operation the arithmetic operation, expresses as a {@code BiFunction}
     *        to invoke on the operand/operands of this operator
     * @return this calculation context for chainability
     * @throws IllegalArgumentException if the operator identified by it
     *         {@code symbol} is already in the registry
     */
    public CalculationContext registerOperator(CharSequence symbol, Precedence precedence, Associativity associativity,
            Multiplicity multiplicity, Function<BigDecimal[], BigDecimal> operation) throws IllegalArgumentException {
        try {
            getOperator(symbol, this);
            throw new IllegalArgumentException(String.format("operator %s already exists", symbol));
        } catch (NoSuchElementException ex) {
            List<OperatorToken> registry = new LinkedList<>();
            registry.addAll(defaultOperatorsRegistry);
            registry.addAll(operatorRegistry);
            registry.add(OperatorToken.create(symbol, precedence, associativity, multiplicity, operation));
            operatorRegistry = Collections.unmodifiableList(registry);
            return this;
        }
    }

    /**
     * A method allowing users to register new constants by providing the constant
     * {@code symbol} and it's {@code value} as a {@link Double}
     * <p>
     * <b>Note</b> users must call this function prior to any calculation.
     *
     * @param symbol the unique symbol of the constant
     * @param value the value of the constant
     * @return this calculation context for chainability
     * @throws IllegalArgumentException if the symbol already exists in the
     *         constants registry, if it's blank or if the value is not a
     *         representation of Double
     * @throws NullPointerException if either symbol or value are null
     * @apiNote Users who wish to register new constants can do so but are required
     *          to perform the operation prior to any calculation operation
     */
    public CalculationContext registerConstant(CharSequence symbol, BigDecimal value) throws IllegalArgumentException {
        if (null != getConstantsRegistry().get(Objects.requireNonNull(symbol, "constant opernad symbol can't be null"))
                || symbol.toString().isBlank()) {
            String message = "constants registry already contains constant '" + symbol + "'!";
            logger.warn(message);
            throw new IllegalArgumentException(message);
        }
        Map<CharSequence, BigDecimal> registry = new LinkedHashMap<>();
        registry.putAll(defaultConstantsRegistry);
        registry.putAll(constantsRegistry);
        registry.put(symbol, Objects.requireNonNull(value, "constant opernad value cannot be null"));
        constantsRegistry = Collections.unmodifiableMap(registry);
        logger.debug("registered constant '{}' with value '{}'", symbol, value);
        return this;
    }

    /**
     * A method allowing users to register variables by providing the variable
     * {@code symbol} and it's {@code value} as a {@link BigDecimal}
     * <p>
     * <b>Note</b> users must call this function prior to any calculation.
     *
     * @param symbol the unique symbol of the variable. This cannot collide with a
     *        constant symbol.
     * @param value the value of the constant
     * @return this calculation context for chainability
     * @throws IllegalArgumentException if the symbol is null, blank or if the value
     *         is null or not a representation of Double
     * @apiNote Users who wish to register new variables can do so but are required
     *          to perform the operation prior to any calculation operation.
     */
    public CalculationContext addVariable(CharSequence symbol, BigDecimal value) throws IllegalArgumentException {
        if (null == value) {
            String message = "variable value cannot be null!";
            logger.warn(message);
            throw new IllegalArgumentException(message);
        }
        if (null == symbol || symbol.toString().isBlank()) {
            String message = "variable symbol cannot be null or blank!";
            logger.warn(message);
            throw new IllegalArgumentException(message);
        }
        if (defaultConstantsRegistry.containsKey(symbol)) {
            String message = "constants registry already contains constant '" + symbol + "'!";
            logger.warn(message);
            throw new IllegalArgumentException(message);
        }
        if (defaultFunctionRegistry.stream().anyMatch(f -> f.getSymbol().equals(symbol))) {
            String message = "functions registry already contains function '" + symbol + "'!";
            logger.warn(message);
            throw new IllegalArgumentException(message);
        }
        Map<CharSequence, BigDecimal> registry = new LinkedHashMap<>();
        registry.putAll(defaultConstantsRegistry);
        registry.putAll(constantsRegistry);
        registry.put(symbol, Objects.requireNonNull(value, "variable value cannot be null!"));
        constantsRegistry = Collections.unmodifiableMap(registry);
        logger.debug("registered variable '{}' with value '{}'", symbol, value);
        return this;
    }

    /**
     * A method allowing users to register variables by providing the variable
     * {@code symbol} and it's {@code value} as a {@link Double}
     * <p>
     * <b>Note</b> users must call this function prior to any calculation.
     *
     * @param symbol the unique symbol of the variable. This cannot collide with a
     *        constant symbol.
     * @param value the value of the constant
     * @return this calculation context for chainability
     * @throws IllegalArgumentException if the symbol is blank or if the value is
     *         not a representation of Double
     * @throws NullPointerException if either symbol or value are null
     * @apiNote Users who wish to register new variables can do so but are required
     *          to perform the operation prior to any calculation operation.
     */
    public CalculationContext addVariable(CharSequence symbol, Double value) throws IllegalArgumentException {
        if (null == value) {
            String message = "variable value cannot be null!";
            logger.warn(message);
            throw new IllegalArgumentException(message);
        }
        return addVariable(symbol, BigDecimal.valueOf(value));
    }

    /**
     * rounds a given double {@code value} to the {@code decimalPlaces} of this
     * context.
     *
     * @param value the double value to round
     * @return the rounded double value
     * @throws IllegalArgumentException if the given {@code value} is null
     */
    public BigDecimal round(Double value) {
        if (value == null) {
            throw new IllegalArgumentException("cannot round null or NaN value");
        }
        return new BigDecimal(value).setScale(getPrecision(), getRoundingMode());
    }

    /**
     * rounds a given double {@code BigDecimal} to the {@code precision} of this
     * context.
     *
     * @param value the BigDecimal to round
     * @return the rounded double value
     * @throws IllegalArgumentException if the given {@code value} is null
     */
    public BigDecimal round(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("cannot round null or NaN value");
        }
        return value.setScale(getPrecision(), getRoundingMode());
    }

    /**
     * gets the configured {@link java.math.RoundingMode}
     *
     * @return the configured rounding mode
     */
    public RoundingMode getRoundingMode() {
        return mathContext.getRoundingMode();
    }

    /**
     * gets the configured rounding decimal places (the {@code default} is
     * <b>7</b>).
     *
     * @return the configured rounding decimal places
     */
    public int getPrecision() {
        return mathContext.getPrecision();
    }

    /**
     * gets this unmodifiable registry of functions.
     *
     * @return unmodifiable list of the registered functions
     */
    protected List<FunctionToken> getFunctionsRegistry() {
        return functionsRegistry;
    }

    /**
     * gets this unmodifiable registry of operators.
     *
     * @return unmodifiable list of the registered operators
     */
    protected List<OperatorToken> getOperatorRegistry() {
        return operatorRegistry;
    }

    /**
     * gets this unmodifiable map of registered constants.
     *
     * @return unmodifiable map of the registered constants
     */
    protected Map<CharSequence, BigDecimal> getConstantsRegistry() {
        return constantsRegistry;
    }

    /**
     * sets this context rounding mode.
     *
     * @param roundingMode the new rounding mode
     */
    public void setRoundingMode(RoundingMode roundingMode) {
        this.mathContext = new MathContext(mathContext.getPrecision(), roundingMode);
    }

    /**
     * set the number of decimal places to round to.
     *
     * @param precision the number of decimal places to round
     */
    public void setPrecision(int precision) {
        this.mathContext = new MathContext(precision, mathContext.getRoundingMode());
    }

    /**
     * gets a description of the type of math context used.
     *
     * @return a description of the type of math context used
     */
    private String getMathContextType() {
        if (mathContext.equals(MathContext.DECIMAL32)) {
            return "DECIMAL32 ";
        } else if (mathContext.equals(MathContext.DECIMAL64)) {
            return "DECIMAL64 ";
        } else if (mathContext.equals(MathContext.DECIMAL128)) {
            return "DECIMAL128 ";
        }
        return "CUSTOM ";
    }

    /**
     * Hash code.
     *
     * @return the int
     */
    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mathContext == null) ? 0 : mathContext.hashCode());
        result = prime * result + print().hashCode();
        return result;
    }

    /**
     * Equals.
     *
     * @param obj the obj
     * @return true, if successful
     */
    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
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
        CalculationContext other = (CalculationContext) obj;
        return this.print().equals(other.print());
    }

    /**
     * Prints the.
     *
     * @return the string
     */
    /*
     * (non-Javadoc)
     *
     * @see org.silvermania.rpn.support.Printable#print()
     */
    @Override
    public String print() {
        Map<String, String> props = new LinkedHashMap<>();
        props.put(indent() + "class", "org.silvermania.rpn.support.CalculationContext");
        props.put(indent() + "mathContext", getMathContextType() + mathContext);
        props.put(indent() + "constants", constantsRegistry.keySet().stream().collect(joining(",")));
        props.put(indent() + "operators", operatorRegistry.stream().map(o -> o.getSymbol()).collect(joining(",")));
        props.put(indent() + "funtions", functionsRegistry.stream().map(f -> f.getSymbol()).collect(joining(",")));
        return props.entrySet().stream().map(e -> String.format("%n%-20s%s", e.getKey(), e.getValue()))
                .collect(Collectors.joining());
    }

    /**
     * To string.
     *
     * @return the string
     */
    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return print();
    }

}
