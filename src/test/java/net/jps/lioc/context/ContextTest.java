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

import java.util.Stack;
import net.jps.lioc.context.resolution.ContextResolutionException;
import net.jps.lioc.ContextBuilder;
import net.jps.lioc.ContextReferenceNotFoundException;
import net.jps.lioc.ContextRegistry;
import net.jps.lioc.IContextRegistry;
import net.jps.lioc.context.resolution.CircularDependencyResolutionException;
import net.jps.lioc.context.resolution.DependencyInstantiationException;
import net.jps.lioc.context.support.ExtendedTestObject;
import net.jps.lioc.context.support.ObjectThatThrowsExceptions;
import net.jps.lioc.context.support.ObjectWhichRequiresItself;
import net.jps.lioc.context.support.ObjectWithoutConstructors;
import net.jps.lioc.context.support.SuperTestObject;
import net.jps.lioc.context.support.SimpleTestObject;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 *
 * @author zinic
 */
@RunWith(Enclosed.class)
public class ContextTest {

    public static class WhenSearchingContexts {

        @Test(expected = ContextReferenceNotFoundException.class)
        public void shouldNotUseEmptyContextLibraryForAliasSearches() {
            new ContextRegistry().getByAlias("Bam!");
        }

        @Test(expected = ContextReferenceNotFoundException.class)
        public void shouldNotUseEmptyContextLibraryForClassSearches() {
            new ContextRegistry().getByClass(Object.class);
        }

        @Test
        public void shouldFindObjectsFromMultipleContexts() {
            final IContextRegistry appContext = new ContextRegistry();

            appContext.use(
                    new ContextBuilder("A Context") {

                        {
                            register(SimpleTestObject.class).as("test");
                        }
                    },
                    new ContextBuilder() {

                        {
                            register(SimpleTestObject.class).as("ref");
                        }
                    });

            assertNotNull(appContext.getByAlias("ref"));
        }
    }

    public static class WhenDoingSimpleRegistrations {

        @Test(expected = ContextResolutionException.class)
        public void shouldDetectUnconstructableClasses() {
            final IContextRegistry appContext = new ContextRegistry();

            appContext.use(new ContextBuilder() {

                {
                    register(ObjectWithoutConstructors.class);
                }
            });

            appContext.getByClass(ObjectWithoutConstructors.class);
        }

        @Test(expected = DependencyInstantiationException.class)
        public void shouldThrowUpExceptionsThrownDuringDependencyConstruction() {
            final IContextRegistry appContext = new ContextRegistry();

            appContext.use(new ContextBuilder() {

                {
                    register(ObjectThatThrowsExceptions.class);
                }
            });

            appContext.getByClass(ObjectThatThrowsExceptions.class);
        }

        @Test
        public void shouldRegisterClasses() {
            final IContextRegistry appContext = new ContextRegistry();

            appContext.use(new ContextBuilder() {

                {
                    register(SimpleTestObject.class);
                }
            });

            assertNotNull(appContext.getByClass(SimpleTestObject.class));
        }

        @Test
        public void shouldRegisterClassesUsingReferenceNames() {
            final IContextRegistry appContext = new ContextRegistry();

            appContext.use(new ContextBuilder() {

                {
                    register(SimpleTestObject.class).as("ref");
                }
            });

            assertNotNull(appContext.getByAlias("ref"));
        }

        @Test
        public void shouldRegisterObjects() {
            final IContextRegistry appContext = new ContextRegistry();
            final SimpleTestObject test = new SimpleTestObject();

            appContext.use(new ContextBuilder() {

                {
                    register(test);
                }
            });

            assertNotNull(appContext.getByClass(SimpleTestObject.class));
        }
    }

    public static class WhenFindingCircularDependencies {

        @Test(expected = CircularDependencyResolutionException.class)
        public void shouldDetectCircularClassDependencies() {
            final IContextRegistry appContext = new ContextRegistry();

            appContext.use(new ContextBuilder() {

                {
                    register(ObjectWhichRequiresItself.class).as("Circular");
                }
            });

            appContext.getByAlias("Circular");
        }

        @Test
        public void shouldFormatDependencyStack() {
            final Stack classDepStack = new Stack<Class>();
            classDepStack.push(Object.class);

            final String formattedOutput = new CircularDependencyResolutionException("Testing", classDepStack).toString();

            assertTrue(formattedOutput.equals("Resolution stack trace...\n\tDependency: java.lang.Object"));

        }
    }

    public static class WhenRegisteringDependencies {

        @Test(expected = ContextResolutionException.class)
        public void shouldDetectUnresolvableClassDependencies() {
            final IContextRegistry appContext = new ContextRegistry();

            appContext.use(new ContextBuilder() {

                {
                    register(SuperTestObject.class).as("Super");
                }
            });

            appContext.getByAlias("Super");
        }

        @Test
        public void shouldCorrectlyUseLesserConstructorsToResolveClassDependencies() {
            final IContextRegistry appContext = new ContextRegistry();

            appContext.use(new ContextBuilder() {

                {
                    register(ExtendedTestObject.class).as("Extended");
                    register(SuperTestObject.class).as("Super");
                }
            });

            final ExtendedTestObject eto = appContext.getByAlias("Extended");

            assertNotNull(eto);

            final SuperTestObject sto = appContext.getByAlias("Super");

            assertNotNull(sto);
            assertNotNull(sto.getExtendedDependency());

            assertEquals(sto.getExtendedDependency(), eto);
        }

        @Test
        public void shouldCorrectlyResolveClassDependencies() {
            final IContextRegistry appContext = new ContextRegistry();

            appContext.use(new ContextBuilder() {

                {
                    register(SimpleTestObject.class);
                    register(ExtendedTestObject.class).as("Extended");
                    register(SuperTestObject.class).as("Super");
                }
            });

            final ExtendedTestObject eto = appContext.getByAlias("Extended");

            assertNotNull(eto);
            assertNotNull(eto.getDependency());

            assertEquals(appContext.getByClass(SimpleTestObject.class), eto.getDependency());

            final SuperTestObject sto = appContext.getByAlias("Super");

            assertNotNull(sto);
            assertNotNull(sto.getDependency());
            assertNotNull(sto.getExtendedDependency());

            assertEquals(sto.getExtendedDependency(), eto);
            assertEquals(sto.getDependency(), appContext.getByClass(SimpleTestObject.class));
        }
    }
}
