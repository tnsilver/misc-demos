/*
 * File: Converter.java
 * Creation Date: Jul 2, 2019
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
 * The Interface Converter defines a conversion method to convert type S source
 * objects to type T target objects.
 *
 * @author T.N.Silverman
 * @param <S> the generic type of the source object
 * @param <T> the generic type of the target object
 */
@FunctionalInterface
public interface Converter<S, T extends Serializable> extends Serializable {

    /**
     * Converts a type S object into a type T target
     *
     * @param source the source object
     * @return the converted target object
     */
    T convert(S source);
}
