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

package net.jps.lioc;

/**
 *
 * @author zinic
 */
public interface IContextRegistry {

    public void use(ContextBuilder... contextBuilders);

    /**
     * Iterates through all loaded contexts to find a registered object that
     * matches the given class signature.
     * 
     * @param <T>
     * @param classToRetrieve
     * @return
     * @throws ContextReferenceNotFoundException
     */
    <T> T getByClass(Class<T> classToRetrieve) throws ContextReferenceNotFoundException;


    /**
     * Iterates through all loaded contexts to find a registered object that
     * carries the same alias as the alias passed in
     *
     * @param <T>
     * @param alias
     * @return
     * @throws ContextReferenceNotFoundException
     */
    <T> T getByAlias(String alias) throws ContextReferenceNotFoundException;
}
