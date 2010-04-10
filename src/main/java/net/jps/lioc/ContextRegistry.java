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

import java.util.LinkedList;
import java.util.List;
import net.jps.lioc.context.IContext;
import net.jps.util.Join;

/**
 *
 * @author zinic
 */
public class ContextRegistry implements IContextRegistry {

    private final List<IContext> contextLibrary;

    public ContextRegistry() {
        this.contextLibrary = new LinkedList<IContext>();
    }

    @Override
    public void use(ContextBuilder... contextBuilders) {
        for (ContextBuilder contextBuilder : contextBuilders) {
            contextLibrary.add(contextBuilder.context);
        }
    }

    @Override
    public <T> T getByAlias(String alias) {
        for (IContext context : contextLibrary) {
            final T objectRef = context.<T>findByAlias(alias);

            if (objectRef != null) {
                return objectRef;
            }
        }

        throw new ContextReferenceNotFoundException(Join.strings("Unable to locate a reference named, '", alias, "'"));
    }

    @Override
    public <T> T getByClass(Class<T> classToRetrieve) {
        for (IContext context : contextLibrary) {
            final T objectRef = context.<T>findByClass(classToRetrieve);

            if (objectRef != null) {
                return objectRef;
            }
        }

        throw new ContextReferenceNotFoundException(Join.strings("Unable to locate a reference by class, '", classToRetrieve.getCanonicalName(), "'"));
    }
}
