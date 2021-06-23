/*
 * File: SimpleInfixCalculationExample.java
 * Creation Date: Jul 1, 2019
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
package org.silvermania.calculator.example;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.silvermania.rpn.support.Associativity.LEFT;
import static org.silvermania.rpn.support.Multiplicity.UNARY;
import static org.silvermania.rpn.support.Precedence.HIGH;

import java.math.BigDecimal;
import java.util.function.Function;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.silvermania.rpn.postfix.calculator.RPNCalculator;
import org.silvermania.rpn.support.Associativity;
import org.silvermania.rpn.support.CalculationContext;
import org.silvermania.rpn.support.Multiplicity;

/**
 * The class AdvancedCalculationCustomizationExample demonstrates
 * <b>registering</b> new constants, operators and functions with the
 * {@link org.silvermania.rpn.support.CalculationContext}.
 *
 * @author T.N.Silverman
 */
public class AdvancedCalculationCustomizationExample extends BaseExampleTestCase {

    /**
     * Demonstrates how to register a new constant with a
     * {@link org.silvermania.rpn.support.CalculationContext
     * org.silvermania.rpn.support.CalculationContext} and configure an
     * {@link org.silvermania.rpn.postfix.calculator.RPNCalculator
     * org.silvermania.rpn.postfix.calculator.RPNCalculator} with the
     * <b>customized</b> calculation context.
     * <p>
     * The default {@code CalculationContext} stores the default registered math
     * operators, constants and functions.
     * </p>
     * <p>
     * There's only a limit to the constants that can be registered by default in
     * advance, so the calculation context offers the method
     * {@link org.silvermania.rpn.support.CalculationContext#registerConstant(CharSequence, BigDecimal)}
     * to allow users of the calculator to register their own new constants.
     * </p>
     * <p>
     * In this example we'll register the new constant '~' to represent American
     * line voltage of 120 volts and we'll calculate it's rms voltage.
     * </p>
     * <p>
     * The following configurations are used in this example with the {@code infix}
     * expression {@code ~*[1/(√2)]}:
     * </p>
     *
     * <pre>
     * <b>
     * CalculationContext context = CalculationContext.newInstance();
     * context.registerConstant("~", BigDecimal.valueOf(120));
     * BigDecimal rms = RPNCalculator.withContext(context).convert("~*[1/(√2)]").doPrint().thenCalculate();
     * </b>
     * </pre>
     * <p>
     * The second line calls the method <b>context.registerConstant("~",
     * BigDecimal.valueOf(120))</b> with two arguments. The first, is the symbol of
     * the constant, and the second is it's {@link java.math.BigDecimal} value. Once
     * registered, the constant 'lives' inside the calculation context.
     * </p>
     * <p>
     * The rest of the fluent API calls are:
     * </p>
     * <p>
     * <b>{@code withContext(context)}</b> tells the configurer to set our
     * configured calculation context for it's calculator. This is important because
     * the context contains our configurations with the new constant. This also
     * allows to continue performing operations such as convert(infix)...
     * </p>
     * <p>
     * <b>{@code convert(infix)}</b> tells the configurer that the expression
     * provided is an infix and that it requires conversion to postfix. Then it
     * allows for more options, such as thenCalculate() or, <b>doPrint()</b>...
     * </p>
     * <p>
     * <b>{@code doPrint()}</b> tells the configurer to print the configuration
     * parameters for debugging purposes. This will print the configuration
     * parameters just <b>before</b> the thenCalculate() operation and will show in
     * the console (with INFO logging level) as:
     * </p>
     *
     * <pre>
     * CALCULATION PARAMETERS
     * ----------------------
     * CONTEXT
     *   class             org.silvermania.rpn.support.CalculationContext
     *   mathContext       DECIMAL32 precision=7 roundingMode=HALF_EVEN
     *   constants         π,e,PI,~
     *   operators         ^,!,√,*,×,/,÷,%,+,−,-
     *   funtions          sin,cos,tan,min,max,avg,pct,sum,log
     * CALCULATOR
     *   class             org.silvermania.rpn.postfix.calculator.RPNCalculator
     *   context           customized
     * INFIX EXPRESSION    ~*[1/(√2)]
     * POSTFIX EXPRESSION  ~ 1 2 √ / *
     * RESULT              84.8528160
     * </pre>
     * <p>
     * Notice the new constant '~' reported in the console output...
     * </p>
     * <p>
     * <b>{@code thenCalculate()}</b> is a terminal instruction and it tells the
     * configurer to evaluate the expression (always postfix at this stage) and
     * return a big decimal result.
     * </p>
     *
     * @apiNote you can only register a constant once. If an attempt is made to
     *          register the same constant (identified by it's symbol) and it's
     *          already registered - an {@link java.lang.IllegalArgumentException}
     *          will be thrown.
     *
     * @implNote behind the scene, the given {@code infix} is normalized into a
     *           space separated expression, then it's converted to a
     *           {@code postfix} expression, and finally evaluated to provide a
     *           {@link java.math.BigDecimal java.math.BigDecimal} result.
     *           <p>
     *           In fact, if you enable DEBUG level logging, you could see the three
     *           stages involved in the evaluation of each infix expression.
     *           </p>
     *           <p>
     *           First, the given infix is normalize to a space separated
     *           expression, then it's converted to a postfix expression, and
     *           finally, it is evaluated:
     *           </p>
     *
     *           <pre>
     *          InfixNormalizer.normalize -&gt; normalized '1×2' to '1 × 2'
     *          InfixConverter.convert -&gt; converted infix '1 × 2' to postfix '1 2 ×'
     *          RPNCalculator.calculate -&gt; evaluated postfix '1 2 ×' to '2.0000000000000000'
     *           </pre>
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    @DisplayName("register constant example")
    //@Disabled
    public void registerConstantExample() throws Exception {
        context.registerConstant("~", BigDecimal.valueOf(120));
        BigDecimal actual = RPNCalculator.withContext(context).convert("~*[1/(√2)]").doPrint().thenCalculate();
        BigDecimal expected = new BigDecimal("84.8528160");
        assertEquals(expected, actual);
    }

    /**
     * Demonstrates how to add a new variable to a
     * {@link org.silvermania.rpn.support.CalculationContext
     * org.silvermania.rpn.support.CalculationContext} and configure an
     * {@link org.silvermania.rpn.postfix.calculator.RPNCalculator
     * org.silvermania.rpn.postfix.calculator.RPNCalculator} with the
     * <b>customized</b> calculation context.
     * <p>
     * The default {@code CalculationContext} stores the default registered math
     * operators, constants and functions.
     * </p>
     * <p>
     * There's only a limit to the constants that can be registered by default in
     * advance, so the calculation context offers the method
     * {@link org.silvermania.rpn.support.CalculationContext#registerConstant(CharSequence, BigDecimal)}
     * to allow users of the calculator to register their own new constants.
     * </p>
     * <p>
     * Constants are fixed value variables and once defined, they cannot be changed.
     * In fact, trying to redefine an existing constant will throw an
     * {@code IllegalArgumentException}. That's what variables are for. The context
     * offers the methods
     * {@link CalculationContext#addVariable(CharSequence, BigDecimal)} and
     * {@link CalculationContext#addVariable(CharSequence, Double)} so we can define
     * variables.
     *
     * Variables change the way the {@code CalculationContext} treats constants.
     * Adding a variable basically tells the context that it's OK to change the
     * constant value if it exists, or to register it as a new constant if it
     * doesn't.
     * </p>
     * <p>
     * In this example we'll register a variable '~' to represent North American
     * line voltage of 120 volts, and then we will redefine it to be the European
     * equivalent of 230 volts. We will calculate RMS voltage in each case.
     * </p>
     * <p>
     * The following configuration options are used in this example with the
     * {@code infix} expression {@code ~*[1/(√2)]}:
     * </p>
     *
     * <pre>
     * <b>
     * CalculationContext context = CalculationContext.newInstance();
     * context.addVariable("~", 120D); // American line voltage
     * BigDecimal usRms = RPNCalculator.withContext(context).convert("~*[1/(√2)]").doPrint().thenCalculate(); // American RMS voltage
     * context.addVariable("~", 230D); // European line voltage
     * BigDecimal euRms = RPNCalculator.withContext(context).convert("~*[1/(√2)]").doPrint().thenCalculate(); // European RMS voltage
     * </b>
     * </pre>
     * <p>
     * The second line calls the method <b>context.addVariable("~",120D)</b> with
     * two arguments. The first, is the symbol of the variable, and the second is
     * it's {@link java.lang.Double java.lang.Double} (or, the other overloaded
     * method version {@link java.math.BigDecimal}) value. Once added, the variable
     * 'lives' inside the calculation context until it's replaced by another call to
     * method {@code addVariable(...)} or until the context is replaced.
     * </p>
     * <p>
     * The rest of the fluent API calls are:
     * </p>
     * <p>
     * <b>{@code withContext(context)}</b> tells the configurer to set our
     * configured calculation context for it's calculator. This is important because
     * the context contains our configurations with the new constant. This also
     * allows to continue performing operations such as convert(infix)...
     * </p>
     * <p>
     * <b>{@code convert(infix)}</b> tells the configurer that the expression
     * provided is an infix and that it requires conversion to postfix. Then it
     * allows for more options, such as thenCalculate() or, <b>doPrint()</b>...
     * </p>
     * <p>
     * <b>{@code doPrint()}</b> tells the configurer to print the configuration
     * parameters for debugging purposes. This will print the configuration
     * parameters just <b>before</b> the thenCalculate() operation and will show in
     * the console (with INFO logging level) as:
     * </p>
     *
     * <pre>
     * CONTEXT
     *   class             org.silvermania.rpn.support.CalculationContext
     *   mathContext       DECIMAL32 precision=7 roundingMode=HALF_EVEN
     *   constants         PI,π,e,~
     *   operators         ^,!,√,*,×,/,÷,%,+,−,-
     *   funtions          sin,cos,tan,min,max,avg,pct,sum,log
     * CALCULATOR
     *   class             org.silvermania.rpn.postfix.calculator.RPNCalculator
     *   context           customized
     * INFIX EXPRESSION    ~*[1/(√2)]
     * POSTFIX EXPRESSION  ~ 1 2 √ / *
     * RESULT              84.8528160
     * </pre>
     * <p>
     * For the second run, with the European value for '~' we can see:
     * </p>
     *
     * <pre>
     * CALCULATION PARAMETERS
     * ----------------------
     * CONTEXT
     *   class             org.silvermania.rpn.support.CalculationContext
     *   mathContext       DECIMAL32 precision=7 roundingMode=HALF_EVEN
     *   constants         PI,π,e,~
     *   operators         ^,!,√,*,×,/,÷,%,+,−,-
     *   funtions          sin,cos,tan,min,max,avg,pct,sum,log
     * CALCULATOR
     *   class             org.silvermania.rpn.postfix.calculator.RPNCalculator
     *   context           customized
     * INFIX EXPRESSION    ~*[1/(√2)]
     * POSTFIX EXPRESSION  ~ 1 2 √ / *
     * RESULT              162.6345640
     * </pre>
     * <p>
     * Notice the new variable '~' reported in the console output at the constants
     * section... Variables are treated like dynamic constants (as silly as it
     * sounds).
     * </p>
     * <p>
     * <b>{@code thenCalculate()}</b> is a terminal instruction and it tells the
     * configurer to evaluate the expression (always postfix at this stage) and
     * return a big decimal result.
     * </p>
     *
     * @apiNote you can only add a variable if it's symbol does not collide with an
     *          existing constant symbol or function name. If you try to override a
     *          constant or function name with an added variable, a
     *          {@link IllegalArgumentException} will be thrown.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    @DisplayName("add variable example")
    //@Disabled
    public void addVariableExample() throws Exception {
        context.addVariable("~", 120D);
        BigDecimal actual = RPNCalculator.withContext(context).convert("~*[1/(√2)]").doPrint().thenCalculate();
        BigDecimal expected = new BigDecimal("84.8528160");
        assertEquals(expected, actual);
        expected = new BigDecimal("162.6345640");
        context.addVariable("~", 230D);
        actual = RPNCalculator.withContext(context).convert("~*[1/(√2)]").doPrint().thenCalculate();
        assertEquals(expected, actual);
    }

    /**
     * Demonstrates how to register a new function with a
     * {@link org.silvermania.rpn.support.CalculationContext
     * org.silvermania.rpn.support.CalculationContext} and configure an
     * {@link org.silvermania.rpn.postfix.calculator.RPNCalculator
     * org.silvermania.rpn.postfix.calculator.RPNCalculator} with the
     * <b>customized</b> calculation context.
     * <p>
     * The default {@link org.silvermania.rpn.support.CalculationContext
     * org.silvermania.rpn.support.CalculationContext} stores the default registered
     * math operators, constants and functions.
     * </p>
     * <p>
     * The {@link org.silvermania.rpn.support.CalculationContext CalculationContext}
     * offers the method
     * {@link org.silvermania.rpn.support.CalculationContext#registerFunction(CharSequence, org.silvermania.rpn.support.Multiplicity, java.util.function.Function)}
     * to register a custom function with the context.
     * </p>
     * <p>
     * In this example we'll register a new unary function 'rms' to calculate the
     * root mean square voltage of any given AC voltage.
     * </p>
     * <p>
     * The following configuration options are used in this example with the
     * {@code infix} expression {@code rms(~)}:
     * </p>
     *
     * <pre>
     * <b>
     * CalculationContext context = CalculationContext.newInstance();
     * context.addVariable("~", BigDecimal.valueOf(120)); // variable '~' equals 120 volts
     * context.registerFunction("rms", UNARY, arr -&gt; arr[0].multiply(BigDecimal.ONE.divide(BigDecimal.valueOf(Math.sqrt(2)), context.getPrecision(), context.getRoundingMode())); // rms = v * [1/√2]
     * BigDecimal rms = RPNCalculator.withContext(context).convert("rms(~)").doPrint().thenCalculate();
     * </b>
     * </pre>
     * <p>
     * The second line calls the method <b>addVariable("~",
     * BigDecimal.valueOf(120))</b> with two arguments. The first, is the symbol of
     * the variable, and the second is it's {@link java.math.BigDecimal} value. Once
     * registered, the variable 'lives' inside the calculation context until it's
     * replaced.
     * </p>
     * <p>
     * The third line calls the method <b>registerFunction(name, multiplicity,
     * function)</b> with three arguments. The first, is the symbol of the function,
     * and the second is it's {@link org.silvermania.rpn.support.Multiplicity} value
     * (UNARY in this case). The third argument is a
     * {@link java.util.function.Function} which accepts an array of
     * {@code BigDecimal} arguments and returns a {@code BigDecimal} result. In it's
     * full form, this could be defined as:
     * </p>
     *
     * <pre>
     * Function&lt;BigDecimal[], BigDecimal&gt; function = arr -&gt; arr[0].multiply(BigDecimal.ONE
     *         .divide(BigDecimal.valueOf(Math.sqrt(2)), context.getPrecision(), context.getRoundingMode()));
     * </pre>
     * <p>
     * There's a lot of BigDecimal 'lingo' in the expression, which is basically
     * multiplying the value at index 0 of the array by 1/√2. The division part of
     * the assignment requires a decimal precision and a rounding mode, and these
     * parameters are supplied by the calculation context. However, we can actually
     * make the assignment a little shorter by using static imports for
     * {@code BigDecimal.ONE} and {@code BigDecimal.valueOf}:
     * </p>
     *
     * <pre>
     * <b>
     * Function&lt;BigDecimal[], BigDecimal&gt; function = arr -&gt; arr[0].multiply(ONE.divide(valueOf(Math.sqrt(2)),context.getPrecision(),context.getRoundingMode())));
     * </b>
     * </pre>
     *
     * Still, we can make the assignment real concise! We know 1/√2 is actually
     * always 0.7071068 (not a bad idea to define as a constant), so the entire part
     * after the -&gt; can be made:
     *
     * <pre>
     * <b>
     * Function&lt;BigDecimal[], BigDecimal&gt; function = arr -&gt; arr[0].multiply(valueOf(0.7071068))
     * </b>
     * </pre>
     *
     * and finally, using a static import for ({@code Multiplicity.UNARY}, we can
     * drop the redundant function declaration and the entire snippet can become:
     *
     * <pre>
     * <b>
     * CalculationContext context = CalculationContext.newInstance();
     * context.addVariable("~", BigDecimal.valueOf(120)); // variable '~' equals 120 volts
     * context.registerFunction("rms", UNARY, arr -&gt; arr[0].multiply(valueOf(0.7071068))); // rms = v * [1/√2]
     * BigDecimal rms = RPNCalculator.withContext(context).convert("rms(~)").doPrint().thenCalculate();
     * </b>
     * </pre>
     * <p>
     * The rest of the fluent API calls are:
     * </p>
     * <p>
     * <b>{@code withContext(context)}</b> tells the configurer to set the given
     * calculation context for it's calculator. This is important because the
     * context contains our configurations. This also allows to continue
     * configuration operations such as convert(infix)...
     * </p>
     * <p>
     * <b>{@code convert(infix)}</b> tells the configurer that the expression
     * provided is an infix and that it requires conversion to postfix. Then it
     * allows for more options, such as thenCalculate() or, <b>doPrint()</b>...
     * </p>
     * <p>
     * <b>{@code doPrint()}</b> tells the configurer to print the configuration
     * parameters for debugging purposes. This will print the configuration
     * parameters just <b>before</b> the thenCalculate() operation and will show in
     * the console (with INFO logging level) as:
     * </p>
     *
     * <pre>
     * CALCULATION PARAMETERS
     * ----------------------
     * CONTEXT
     *   class             org.silvermania.rpn.support.CalculationContext
     *   mathContext       DECIMAL32 precision=7 roundingMode=HALF_EVEN
     *   constants         PI,e,π,~
     *   operators         ^,!,√,*,×,/,÷,%,+,−,-
     *   funtions          sin,cos,tan,min,max,avg,pct,sum,log,rms
     * CALCULATOR
     *   class             org.silvermania.rpn.postfix.calculator.RPNCalculator
     *   context           customized
     * INFIX EXPRESSION    rms(~)
     * POSTFIX EXPRESSION  ~ rms
     * RESULT              84.8528160
     * </pre>
     * <p>
     * Notice the new variable (dynamic constant actually) '~' and function 'rms'
     * are now reported in the console output...
     * </p>
     * <p>
     * <b>{@code thenCalculate()}</b> is a terminal instruction and it tells the
     * configurer to evaluate the expression (always postfix at this stage) and
     * return a big decimal result.
     * </p>
     *
     * @apiNote you can only register a constant or function once. If an attempt is
     *          made to register the same constant or function, as identified by
     *          it's symbol, and it is already registered, an
     *          {@link java.lang.IllegalArgumentException} is thrown. You can
     *          override a variable value as many times as you want.
     *
     * @implNote behind the scene, the given {@code infix} is normalized into a
     *           space separated expression, then its converted to a {@code postfix}
     *           expression, and finally evaluated to provide a
     *           {@link java.math.BigDecimal} result.
     *           <p>
     *           In fact, if you enable DEBUG level logging, you could see the three
     *           stages involved in the evaluation of each infix expression.
     *           </p>
     *           <p>
     *           First, the given infix is normalize to a space separated
     *           expression, then it's converted to a postfix expression, and
     *           finally, it is evaluated:
     *           </p>
     *
     *           <pre>
     *          InfixNormalizer.normalize -&gt; normalized '1×2' to '1 × 2'
     *          InfixConverter.convert -&gt; converted infix '1 × 2' to postfix '1 2 ×'
     *          RPNCalculator.calculate -&gt; evaluated postfix '1 2 ×' to '2.0000000000000000'
     *           </pre>
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    @DisplayName("register function example")
    //@Disabled
    public void registerFunctionExample() throws Exception {
        context.addVariable("~", BigDecimal.valueOf(120)); // variable '~' equals 120 volts
        context.registerFunction("rms", UNARY, arr -> arr[0].multiply(valueOf(0.7071068))); // rms = v * [1/√2]
        BigDecimal actual = RPNCalculator.withContext(context).convert("rms(~)").doPrint().thenCalculate();
        BigDecimal expected = new BigDecimal("84.8528160");
        assertEquals(expected, actual);
    }

    /**
     * Demonstrates how to register a new operator with a
     * {@link org.silvermania.rpn.support.CalculationContext
     * org.silvermania.rpn.support.CalculationContext} and configure an
     * {@link org.silvermania.rpn.postfix.calculator.RPNCalculator
     * org.silvermania.rpn.postfix.calculator.RPNCalculator} with the
     * <b>customized</b> calculation context.
     * <p>
     * In this example we'll register a new unary operator 'r' to represent
     * <b>reciprocal</b> ({@code 1/x}).
     * </p>
     * <p>
     * The default {@link org.silvermania.rpn.support.CalculationContext
     * org.silvermania.rpn.support.CalculationContext} stores the default registered
     * math operators.
     * </p>
     * <p>
     * There's only a limit to the operators that can be registered by default in
     * advance, so the calculation context offers the method
     * {@link org.silvermania.rpn.support.CalculationContext#registerOperator(CharSequence, org.silvermania.rpn.support.Precedence, Associativity, Multiplicity, Function)}
     * to allow users to register their own new operators.
     * </p>
     * <p>
     * The following configuration options are used in this example with the
     * {@code infix} expression {@code r 4}:
     * </p>
     *
     * <pre>
     * <b>
     * CalculationContext context = CalculationContext.newInstance();
     * context.registerOperator("r", HIGH, LEFT, UNARY, (arr) -&gt; ONE.divide(arr[0], context.getPrecision(), context.getRoundingMode()));
     * BigDecimal result = RPNCalculator.withContext(context).convert("r 4").doPrint().thenCalculate();
     * </b>
     * </pre>
     * <p>
     * The second line calls the method <b>registerOperator("r", HIGH, LEFT,
     * UNARY,(arr) -&gt; ONE.divide(arr[0],
     * context.getPrecision(),context.getRoundingMode())</b> and it's argument list
     * is as followed:
     * </p>
     * <ol>
     * <li>operator symbol - must be a character or word but cannot start with a
     * digit and cannot be one of the already registered operators <br>
     * The default registered operators are:
     * <ul>
     * <li>'<b>^</b>' - exponent, binary operator, (a in the power of b) with the
     * highest precedence of <b>HIGHEST</b></li>
     * <li>'<b>!</b>' - factorial, unary operator (a!) with the precedence of
     * <b>HIGH</b></li>
     * <li>'<b>√</b>' - square root, unary operator with LEFT associativity (√a)
     * with the precedence of <b>HIGH</b></li>
     * <li>'<b>*</b>' - multiplication, binary operator (a * b) with the precedence
     * of <b>LOW</b></li>
     * <li>'<b>×</b>' - multiplication *same as '*', binary operator (a × b) with
     * the precedence of <b>LOW</b></li>
     * <li>'<b>/</b>' - division, binary operator (a / b) with the precedence of
     * <b>LOW</b></li>
     * <li>'<b>÷</b>' - division (same as '/'), binary operator (a ÷ b) with the
     * precedence of <b>LOW</b></li>
     * <li>'<b>%</b>' - modulus, binary operator (a % b) with the precedence of
     * <b>LOW</b></li>
     * <li>'<b>+</b>' - addition, binary operator (a + b) with the precedence of
     * <b>LOWEST</b></li>
     * <li>'<b>−</b>' - subtraction, binary operator (a − b) with the precedence of
     * <b>LOWEST</b></li>
     * <li>'<b>-</b>' - subtraction (same as '−'), binary operator (a - b) with the
     * precedence of <b>LOWEST</b></li>
     * </ul>
     * </li>
     * <li>The <b>precedence</b> of the operator - this is an enum value from
     * {@link org.silvermania.rpn.support.Precedence
     * org.silvermania.rpn.support.Precedence}. The higher the value of the constant
     * ordinal, the higher the precedence. Higher precedence operators get evaluated
     * before lower precedence ones. Plus and minus are the LOWEST precedence
     * operator, while '^' (in the power of) has the HIGHEST precedence.</li>
     * <li><b>Associativity:&nbsp;</b>
     * <ul>
     * <li>for {@link Multiplicity#UNARY UNARY} operators this is either
     * {@link Associativity#RIGHT}, like factorial (x!) or {@link Associativity#LEFT
     * LEFT} like square root (√x). Associativity in this case refers to how the
     * operator relates to the single operand - if it's to the LEFT of it, or to the
     * RIGHT.</li>
     * <li>For {@link Multiplicity#BINARY BINARY} operators it's a little more
     * complex. Binary operators like addition '+' and multiplication '*', are
     * basically none associative. We can evaluate {@code a + b + c} as
     * {@code (a + b) + c} or as {@code a + (b + c)} without affecting the result.
     * This is not the case, though for subtraction or division. The expression
     * {@code a - b - c} can be read as {@code (a - b) - c} (LEFT associativity) or
     * as {@code a - (b - c)} (RIGHT associativity) but the result would not be the
     * same. For imperative programming languages and basic arithmetic operations,
     * these four operators {@code +,-,*,/} have a default LEFT associativity.<br>
     * For the special exponent {@code '^'} operator, the expression
     * {@code 2 ^ 2 ^ 3} can be evaluated LEFT to right as {@code (2 ^ 2) ^ 3},
     * which yields {@code (4 ^ 3 = 64)} or RIGHT to left as {@code 2 ^ (2 ^ 3)},
     * which yields (2 ^ 8 = 256) and is actually <b>correct</b>. The LEFT
     * associativity of the first example does not equivalently express
     * {@code (a ^ b ^ c) = a ^ (b * c)}, therefore, the exponential operator has a
     * RIGHT associativity.</li>
     * </ul>
     * <li>The <b>operation</b>, which is a {@code java.util.function.Function} that
     * accepts an array of {@code BigDecimal} arguments and returns a
     * {@code BigDecimal} result. It is up to you to define the operation and you
     * can manipulate the arguments array as you see fit, as long as you don't flow
     * out of the array length (2 for BINARY operators) and the operation returns an
     * appropriate result. When dividing {@code BigDecimal} arguments, you have to
     * supply the {@code precision} and {@code RoundingMode} for the division
     * operation. The {@code CalculationContext} provides appropriate getters for
     * both. You may also want to use the
     * {@link CalculationContext#round(BigDecimal)} helper method to round the
     * result. There's a lot of BigDecimal 'lingo' in this specific case, which is
     * basically dividing 1 by the value at index 0 of the array. The division part
     * of the assignment requires a decimal precision and a rounding mode, and these
     * parameters are supplied by the calculation context via designated getter
     * methods for precision and rounding mode.</li>
     * </ol>
     * <p>
     * The rest of the fluent API calls are:
     * </p>
     * <p>
     * <b>{@code withContext(context)}</b> tells the configurer to set the given
     * calculation context for it's calculator. This is important because the
     * context contains our configurations. This also allows to continue
     * configuration operations such as convert(infix)...
     * </p>
     * <p>
     * <b>{@code convert(infix)}</b> tells the configurer that the expression
     * provided is an infix and that it requires conversion to postfix. Then it
     * allows for more options, such as thenCalculate() or, <b>doPrint()</b>...
     * </p>
     * <p>
     * <b>{@code doPrint()}</b> tells the configurer to print the configuration
     * parameters for debugging purposes. This will print the configuration
     * parameters just <b>before</b> the thenCalculate() operation and will show in
     * the console (with INFO logging level) as:
     * </p>
     *
     * <pre>
     * CALCULATION PARAMETERS
     * ----------------------
     * CONTEXT
     *   class             org.silvermania.rpn.support.CalculationContext
     *   mathContext       DECIMAL32 precision=7 roundingMode=HALF_EVEN
     *   constants         PI,e,π
     *   operators         ^,!,√,*,×,/,÷,%,+,−,-,r
     *   funtions          sin,cos,tan,min,max,avg,pct,sum,log
     * CALCULATOR
     *   class             org.silvermania.rpn.postfix.calculator.RPNCalculator
     *   context           customized
     * INFIX EXPRESSION    r 4
     * POSTFIX EXPRESSION  4 r
     * RESULT              0.2500000
     * </pre>
     * <p>
     * Notice the new operator 'r' reported in the console output...
     * </p>
     * <p>
     * <b>{@code thenCalculate()}</b> is a terminal instruction and it tells the
     * configurer to evaluate the expression (always postfix at this stage) and
     * return a big decimal result.
     * </p>
     *
     * @apiNote you can only register an operator or function once. If an attempt is
     *          made to register the same operator or function, as identified by
     *          it's symbol, and it is already registered, an
     *          {@link java.lang.IllegalArgumentException} is thrown.
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    @DisplayName("register operator example")
    //@Disabled
    public void registerOperatorExample() throws Exception {
        context.registerOperator("r", HIGH, LEFT, UNARY,
                (arr) -> ONE.divide(arr[0], context.getPrecision(), context.getRoundingMode()));
        BigDecimal actual = RPNCalculator.withContext(context).convert("r 4").doPrint().thenCalculate();
        BigDecimal expected = new BigDecimal("0.2500000");
        assertEquals(expected, actual);
    }

    /**
     * Finding an Angle in a Right Angled Triangle: We can find an unknown angle in
     * a right-angled triangle, as long as we know the lengths of two of its sides.
     * We have a special phrase "SOHCAHTOA" to help us, and we use it like this:
     *
     * <pre>
     * <b>SOH:</b> Sine: sin(θ) = Opposite / Hypotenuse
     * <i>CAH:</i> Cosine: cos(θ) = Adjacent / Hypotenuse
     * <i>TOA:</i> Tangent: tan(θ) = Opposite / Adjacent
     * </pre>
     *
     * We'll define the inverse sine function {@code isin} as the sine in the power
     * of -1 of the ratio between the opposite and hypotenuse:
     *
     * <pre>
     * isin = sin^-1(Opposite/Hypotenuse)
     * </pre>
     *
     * In Java, we can do it as: {@code Math.toDegrees(Math.asin(ratio))}. We will
     * also define two variables {@code oppo} for 'Opposite' and {@code hypo} for
     * 'Hypotenuse'.
     *
     * We'll do all of the above in one fluent code line:
     * <pre>
     * <b>
     * context.addVariable("oppo", 3.33D)
     *        .addVariable("hypo", 6.66D)
     *        .registerFunction("isin", UNARY,
     *                  arr -&gt; context.round(Math.toDegrees(Math.asin(arr[0].doubleValue()))));
     * </b>
     * </pre>
     *
     * Finally, we can evaluate a postfix expression that includes our {@code isin} function and custom variables as:
     *
     * <pre>
     * <b>
     * BigDecimal angle = RPNCalculator.withContext(context).accept("oppo hypo ÷ isin").thenCalculate();
     * </b>
     * </pre>
     *
     * Or, the infix version:
     *
     * <pre>
     * <b>
     * BigDecimal angle = RPNCalculator.withContext(context).convert("isin(oppo÷hypo)").thenCalculate();
     * </b>
     * </pre>
     *
     * @throws Exception if anything goes wrong
     */
    @Test
    @DisplayName("inverse sine chaining example")
    //@Disabled
    public void inverseSineChainingExample() throws Exception {
        context.addVariable("oppo", 3.33D)
               .addVariable("hypo", 6.66D)
               .registerFunction("isin", UNARY,
                       arr -> context.round(Math.toDegrees(Math.asin(arr[0].doubleValue()))));
        BigDecimal actual = RPNCalculator.withContext(context).accept("oppo hypo ÷ isin").thenCalculate();
        //BigDecimal actual = RPNCalculator.withContext(context).convert("isin(oppo÷hypo)").thenCalculate();
        BigDecimal expected = new BigDecimal("30.0000000");
        assertEquals(expected, actual);
    }

}
