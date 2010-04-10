/*
 * Copyright 2010 John Hopper
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.jps.lioc.context;

import net.jps.lioc.ContextReferenceNotFoundException;

/**
 *
 * @author zinic
 */
public interface IContext {

    /**
     * Registers a given Object into the context. If this is a class it will
     * register the Object as a class, otherwise it will be directly input
     * into the dependency tree management set.
     *
     * @param thingToRegister
     * @return
     */
    ContextReferenceAliaser register(Object thingToRegister);

    String getContextAlias();

    void setContextAlias(String alias);

    <T> T findByAlias(String alias) throws ContextReferenceNotFoundException;

    <T> T findByClass(Class<T> classToFind) throws ContextReferenceNotFoundException;
}
