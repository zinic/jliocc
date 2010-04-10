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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import net.jps.util.Join;

/**
 *
 * @author zinic
 */
public class Resolver implements ResolutionResult {

    public static final ResolutionResult resolveReferenceFromContext(IContext context, IContextReference alias, Class type) {
        return new Resolver(context).resolve(alias, type);
    }
    private final IContext contextReference;
    private Object resolutionResult;

    private Resolver(IContext contextReference) {
        this.contextReference = contextReference;
    }

    @Override
    public Object getResolutionResult() {
        return resolutionResult;
    }

    private Resolver resolve(IContextReference alias, Class type) {
        resolutionResult = resolveFromConstructors(type.getConstructors());
        return this;
    }

    private Object resolveFromConstructors(Constructor[] constructors) {
        if (constructors == null || constructors.length <= 0) {
            throw new ContextResolutionException("No constructors available for instanstation");
        }

        return resolveFromConstructor(getConstructor(constructors));
    }

    private Object resolveFromConstructor(Constructor constructor) {
        final Class[] constructorRequirements = constructor.getParameterTypes();
        final Object[] parameters = new Object[constructorRequirements.length];

        for (int index = 0; index < constructorRequirements.length; index++) {
            parameters[index] = contextReference.findByClass(constructorRequirements[index]);

            if (parameters[index] == null) {
                throw new ContextResolutionException(
                        Join.strings("Unable to locate required class dependency for class '",
                        constructorRequirements[index].getCanonicalName(),
                        "'"));
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
            throw new ContextResolutionException(
                    Join.strings("An internal exception has occured in the constructor. Reason: ",
                    invocationTargetException.getMessage()),
                    invocationTargetException);
        }
    }

    private Constructor getConstructor(Constructor[] constructors) {
        Constructor current = constructors[0];

        for (int i = 1; i < constructors.length; i++) {
            if (constructors[i].getParameterTypes().length > current.getParameterTypes().length) {
                current = constructors[i];
            }
        }

        return current;
    }
}
