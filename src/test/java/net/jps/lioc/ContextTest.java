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

import net.jps.lioc.context.ContextResolutionException;
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
public class ContextTest {

    public static class WhenRegisteringContexts {

        @Test
        public void shouldRegisterClasses() {
            final IContextRegistry appContext = new ContextRegistry();

            appContext.use(new ContextBuilder() {

                {
                    register(TestDAO.class);
                }
            });

            assertNotNull(appContext.getByClass(TestDAO.class));
        }

        @Test
        public void shouldRegisterClassesUsingReferenceNames() {
            final IContextRegistry appContext = new ContextRegistry();

            appContext.use(new ContextBuilder() {

                {
                    register(TestDAO.class).as("ref");
                }
            });

            assertNotNull(appContext.getByAlias("ref"));
        }

        @Test
        public void shouldFindObjectsFromMultipleContexts() {
            final IContextRegistry appContext = new ContextRegistry();

            appContext.use(
                    new ContextBuilder("A Context") {

                        {
                            register(TestDAO.class).as("test");
                        }
                    },
                    new ContextBuilder() {

                        {
                            register(TestDAO.class).as("ref");
                        }
                    });

            assertNotNull(appContext.getByAlias("ref"));
        }

        @Test
        public void shouldRegisterObjects() {
            final IContextRegistry appContext = new ContextRegistry();
            final TestDAO test = new TestDAO();

            appContext.use(new ContextBuilder() {

                {
                    register(test);
                }
            });

            assertNotNull(appContext.getByClass(TestDAO.class));
        }
    }

    public static class WhenRegisteringDependencies {

        @Test(expected = ContextResolutionException.class)
        public void shouldDetectUnresolvableClassDependencies() {
            final IContextRegistry appContext = new ContextRegistry();

            appContext.use(new ContextBuilder() {

                {
                    register(ExtendedTestDAO.class).as("Extended DAO");
                }
            });

            appContext.getByAlias("Extended DAO");
        }

        @Test
        public void shouldCorrectlyResolveClassDependencies() {
            final IContextRegistry appContext = new ContextRegistry();

            appContext.use(new ContextBuilder() {

                {
                    register(TestDAO.class);
                    register(ExtendedTestDAO.class).as("Extended DAO");
                    register(SuperExtendedTestDAO.class).as("Super Extendo");
                }
            });

            final ExtendedTestDAO eDao = appContext.getByAlias("Extended DAO");

            assertNotNull(eDao);
            assertNotNull(eDao.myTestDao);

            assertEquals(appContext.getByClass(TestDAO.class), eDao.myTestDao);

            final SuperExtendedTestDAO sDao = appContext.getByAlias("Super Extendo");

            assertNotNull(sDao);
            assertNotNull(sDao.myExtendedTestDao);
            assertNotNull(sDao.myTestDao);

            assertEquals(sDao.myExtendedTestDao, eDao);
            assertEquals(sDao.myTestDao, appContext.getByClass(TestDAO.class));
        }
    }

    @Ignore
    public static class TestDAO {

        public void methodOne() {
        }

        public void methodTwo() {
        }
    }

    @Ignore
    public static class ExtendedTestDAO {

        private final TestDAO myTestDao;

        public ExtendedTestDAO(TestDAO myTestDao) {
            this.myTestDao = myTestDao;
        }

        public void methodOne() {
        }

        public void methodTwo() {
        }
    }

    @Ignore
    public static class SuperExtendedTestDAO {

        private final TestDAO myTestDao;
        private final ExtendedTestDAO myExtendedTestDao;

        public SuperExtendedTestDAO(ExtendedTestDAO myExtendedTestDAO) {
            this.myExtendedTestDao = myExtendedTestDAO;
            myTestDao = null;
        }

        public SuperExtendedTestDAO(TestDAO myTestDao, ExtendedTestDAO myExtendedTestDao) {
            this.myTestDao = myTestDao;
            this.myExtendedTestDao = myExtendedTestDao;
        }

        public void methodOne() {
        }

        public void methodTwo() {
        }
    }
}
