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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
import net.jps.lioc.context.IContext;
import net.jps.util.Join;

/**
 *
 * @author zinic
 */
public class Resolver implements ResolutionResult {

    private final IContext contextReference;
    private Object resolutionResult;

    public Resolver(IContext contextReference) {
        this.contextReference = contextReference;
    }

    @Override
    public Object getResolutionResult() {
        return resolutionResult;
    }

    public Resolver resolve(Class type) {
        resolutionResult = resolveUsingConstructors(type.getConstructors());
        return this;
    }

    private Object resolveUsingConstructors(Constructor[] constructors) {
        if (constructors == null || constructors.length <= 0) {
            throw new ContextResolutionException("No constructors available for instanstation");
        }

        Arrays.sort(constructors, new Comparator<Constructor>() {

            /**
             * This comparator is designed to put the most greedy constructors
             * first for resolution
             *
             * TODO: Externalize this
             */
            public int compare(Constructor o1, Constructor o2) {
                return o2.getParameterTypes().length - o1.getParameterTypes().length;
            }
        });

        for (Constructor constructor : constructors) {
            final Object ref = resolveFromConstructor(constructor);

            if (ref != null) {
                return ref;
            }
        }

        throw new ContextResolutionException(
                Join.strings("Unable to locate required construction dependencies for class '",
                constructors[0].getDeclaringClass().getCanonicalName(),
                "'"));
    }

    private Object resolveFromConstructor(Constructor constructor) {
        final Class[] constructorRequirements = constructor.getParameterTypes();
        final Object[] parameters = new Object[constructorRequirements.length];

        for (int index = 0; index < constructorRequirements.length; index++) {
            parameters[index] = contextReference.findByClass(constructorRequirements[index]);

            if (parameters[index] == null) {
                return null;
            }
        }

        return newInstance(constructor, parameters);
    }

    private Object newInstance(Constructor constructor, Object[] parameters) {
        try {
            return constructor.newInstance(parameters);
        } catch (IllegalAccessException illegalAccessException) {
            throw new ContextResolutionException(
                    Join.strings("Failed to create instance. Reason: ",
                    illegalAccessException.getMessage()),
                    illegalAccessException);
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new ContextResolutionException(
                    Join.strings("Failed to create instance. ",
                    "This case should not happen and may be a Context Bug. ",
                    "Please report it upstream. Reason: ",
                    illegalArgumentException.getMessage()),
                    illegalArgumentException);
        } catch (InstantiationException instantiationException) {
            throw new ContextResolutionException(
                    Join.strings("Failed to create instance. Reason: ",
                    instantiationException.getMessage()),
                    instantiationException);
        } catch (InvocationTargetException invocationTargetException) {
            throw new DependencyInstantiationException(
                    Join.strings("An internal exception has occured in the constructor. Reason: ",
                    invocationTargetException.getCause().getMessage()),
                    invocationTargetException.getCause());
        }
    }
}
