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
package net.jps.lioc.context.resolution;

import java.util.Stack;
import net.jps.lioc.context.IContext;
import net.jps.lioc.context.IContextReference;

/**
 *
 * @author zinic
 */
public final class ContextResolutionHandler {

    public static final ResolutionResult resolveReferenceFromContext(IContext context, IContextReference alias, Class type) {
        final Stack<Class> localResolutionStack = resolutionStack.get();

        if (localResolutionStack.contains(type)) {
            throw new CircularDependencyResolutionException(
                    "Circular dependency condition in context resolution", (Stack<Class>) localResolutionStack);
        }

        localResolutionStack.push(type);

        try {
            return new Resolver(context).resolve(type);
        } finally {
            localResolutionStack.pop();
        }
    }
    public static final ThreadLocal<Stack<Class>> resolutionStack = new ThreadLocal<Stack<Class>>() {

        @Override
        protected Stack<Class> initialValue() {
            return new Stack<Class>();
        }
    };

    private ContextResolutionHandler() {
    }
}
