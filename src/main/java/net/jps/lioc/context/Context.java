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

import net.jps.lioc.context.resolution.ResolutionResult;
import java.util.HashMap;
import java.util.Map;
import net.jps.util.Is;

import net.jps.util.Join;

import static net.jps.lioc.context.resolution.ContextResolutionHandler.resolveReferenceFromContext;

/**
 *
 * @author zinic
 */
public class Context implements IContext {

    private final Map<IContextReference, Object> referenceLookaside, instsanceCache;
    private String contextAlias;

    public Context() {
        referenceLookaside = new HashMap<IContextReference, Object>();
        instsanceCache = new HashMap<IContextReference, Object>();

        contextAlias = Join.strings("[", Thread.currentThread().getName(), "]::Application Context: ", hashCode());
    }

    @Override
    public String getContextAlias() {
        return contextAlias;
    }

    @Override
    public void setContextAlias(String contextAlias) {
        this.contextAlias = contextAlias;
    }

    @Override
    public synchronized ContextReferenceAliaser register(Object thingToRegister) {
        final ContextReference newReference = new ContextReference(thingToRegister);
        referenceLookaside.put(newReference, thingToRegister);

        return newReference;
    }

    @Override
    public <T> T findByAlias(String alias) {
        final IContextReference filter = ContextReference.newFilter(alias);
        final Object retrievedObject = mergeReferenceMaps().get(filter);

        return (T) (Is.aClass(retrievedObject) ? resolveClass(filter, (Class) retrievedObject) : retrievedObject);
    }

    @Override
    public <T> T findByClass(Class<T> classToFind) {
        final Map.Entry<IContextReference, Object> ref = locateReferenceAliasByClass(classToFind);

        return ref == null ? null : (T) (Is.aClass(ref.getValue()) ? resolveClass(ref.getKey(), classToFind) : ref.getValue());
    }

    private Object resolveClass(IContextReference alias, Class classToResolve) {
        ResolutionResult resolutionResult = resolveReferenceFromContext(this, alias, classToResolve);

        synchronized (instsanceCache) {
            instsanceCache.put(alias, resolutionResult.getResolutionResult());
        }

        return resolutionResult.getResolutionResult();
    }

    private Map.Entry<IContextReference, Object> locateReferenceAliasByClass(Class classToFind) {
        for (Map.Entry<IContextReference, Object> entry : mergeReferenceMaps().entrySet()) {
            final Class classToTest = Is.aClass(entry.getValue()) ? (Class) entry.getValue() : entry.getValue().getClass();

            if (classToFind.equals(classToTest)) {
                return entry;
            }
        }

        return null;
    }

    /**
     * Merges the maps to provide a unified search view. Should take into account
     * registered object lifecycle choices.
     *
     * @return
     */
    private Map<IContextReference, Object> mergeReferenceMaps() {
        final Map<IContextReference, Object> mergedMap = new HashMap<IContextReference, Object>();
        mergedMap.putAll(referenceLookaside);

        synchronized (instsanceCache) {
            mergedMap.putAll(instsanceCache);
        }

        return mergedMap;
    }
}
