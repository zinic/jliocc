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
package net.jps.lioc.context.inerceptor;

import java.lang.reflect.Method;
import net.jps.lioc.ContextBuilder;
import net.jps.lioc.ContextRegistry;
import net.jps.lioc.IContextRegistry;
import net.jps.lioc.context.interceptor.InterceptorHandler;
import net.jps.lioc.context.interceptor.IMethodInterceptor;
import net.jps.lioc.context.support.SimpleTestObject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;


import static org.junit.Assert.*;

/**
 *
 * @author zinic
 */
@RunWith(Enclosed.class)
public class InterceptorTest {

    public static class WhenUsingInterceptors {

        @Test
        @Ignore
        public void shouldAcceptRegexMatchers() {
            final IContextRegistry appContext = new ContextRegistry();

            appContext.use(new ContextBuilder("My First Context") {

                {
                    register(SimpleTestObject.class);

                    intercept("*SimpleTestObject.*").with(new IMethodInterceptor() {

                        public void called(InterceptorHandler myHandler, Method originMethod, Object[] parameters) {
                            myHandler.willReturn(true);
                        }
                    });
                }
            });

            assertTrue(appContext.getByClass(SimpleTestObject.class).returnsFalse());
        }

        @Test
        @Ignore
        public void shouldHonorInterceptorPriority() {
            final IContextRegistry appContext = new ContextRegistry();

            appContext.use(new ContextBuilder("My First Context") {

                {
                    register(SimpleTestObject.class);

                    intercept("*SimpleTestObject.*").with(new IMethodInterceptor() {

                        public void called(InterceptorHandler myHandler, Method originMethod, Object[] parameters) {
                            myHandler.willReturn(false);
                        }
                    }).priority(10);

                    intercept("*SimpleTestObject.*").with(new IMethodInterceptor() {

                        public void called(InterceptorHandler myHandler, Method originMethod, Object[] parameters) {
                            myHandler.willReturn(true);
                        }
                    }).priority(100);
                }
            });

            assertTrue(appContext.getByClass(SimpleTestObject.class).returnsFalse());
        }
    }
}
