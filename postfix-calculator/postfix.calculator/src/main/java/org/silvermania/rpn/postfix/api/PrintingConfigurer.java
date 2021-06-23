/*
 * File: PrintingConfigurer.java
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
package org.silvermania.rpn.postfix.api;

/**
 * The Interface PrintingConfigurer defines the {@link #doPrint()} method to
 * print/debug the calculation parameters. The printing message will only appear
 * at {@code INFO} logging level.
 *
 * @author T.N.Silverman
 */
public interface PrintingConfigurer extends Configurer {

    /**
     * Marks this configurer's state to print/debug the calculation parameters
     *
     * @implNote implementations may ignore this instruction until their state
     *           contains calculation parameters that are printable
     *
     * @return a reference to a CalculationConfigurer for further configuration and chaining
     *         if operations.
     */
    CalculationConfigurer doPrint();

}
