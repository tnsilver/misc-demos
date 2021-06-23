/*
 * File: MultiArgOperator.java
 * Creation Date: Jun 23, 2019
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

/**
 * Represents an operation upon many operands of the same type, producing a
 * result of the same type as the operands. This is a specialization for the
 * case where the operands and the result are all of the same type.
 *
 * <p>
 * This is a functional interface whose functional method is
 * {@link #apply(Object[])}.
 *
 * @param <T> the type of the result of the operator
 * @author T.N.Silverman
 */
@FunctionalInterface
public interface MultiArgOperator<T> {

    /**
     * Applies this function to the given array of arguments.
     *
     * @param args the array of arguments
     * @return the function result
     */
    T apply(T[] args);

    /**
     * Returns a {@link MultiArgOperator} which returns the lesser member in the
     * arguments array according to the specified {@code Comparator}.
     *
     * @param <T> the type of the input arguments of the comparator
     * @param comparator a {@code Comparator} for comparing the two values
     * @return a {@code MultiArgOperator} which returns lesser member in the
     *         arguments array, according to the supplied {@code Comparator}
     * @throws NullPointerException if the argument is null
     */
    /*
     * public static <T> MultiArgOperator<T> minBy(Comparator<? super T>
     * comparator) { Objects.requireNonNull(comparator); return array ->
     * Arrays.stream(array).min(comparator) .or(() ->
     * Optional.ofNullable(array[0])).get(); }
     */

    /**
     * Returns a {@link MultiArgOperator} which returns the greater member in
     * the arguments array according to the specified {@code Comparator}.
     *
     * @param <T> the type of the input arguments of the comparator
     * @param comparator a {@code Comparator} for comparing the two values
     * @return a {@code MultiArgOperator} which returns the greater member in
     *         the arguments array, according to the supplied {@code Comparator}
     * @throws NullPointerException if the argument is null
     */
    /*
     * public static <T> MultiArgOperator<T> maxBy(Comparator<? super T>
     * comparator) { Objects.requireNonNull(comparator); return array ->
     * Arrays.stream(array).max(comparator) .or(() ->
     * Optional.ofNullable(array[0])).get(); }
     */
}
