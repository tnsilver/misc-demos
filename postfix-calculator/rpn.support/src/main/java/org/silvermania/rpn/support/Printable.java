/*
 * File: Printable.java
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
package org.silvermania.rpn.support;

import java.io.Serializable;

/**
 * The Interface Printable is designed for implementation by any class that
 * needs to print it's description.
 *
 * @author T.N.Silverman
 */
@FunctionalInterface
public interface Printable extends Serializable {

    /**
     * returns the default 2 spaces indentation level
     *
     * @return the default 2 spaces indentation level
     */
    default String indent() {
        return Character.toString(32) + Character.toString(32);
    }

    /**
     * returns a string description of the implementing class.
     *
     * @return a string description of the implementing class
     */
    String print();
}
