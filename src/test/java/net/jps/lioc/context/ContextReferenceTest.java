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

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 *
 * @author zinic
 */
@RunWith(Enclosed.class)
public class ContextReferenceTest {

    public static class WhenTestingEquality {

        @Test
        public void shouldGenerateSameHashcodeWhenAliasesAreNull() {
            final ContextReference first = new ContextReference(new Object());
            final ContextReference second = new ContextReference(new Object());

            first.as(null);
            second.as(null);

            assertEquals(first.hashCode(), second.hashCode());
        }

        @Test
        public void shouldNotMatchNull() {
            final ContextReference first = new ContextReference(new Object());

            assertFalse(first.equals(null));
        }

        @Test
        public void shouldNotMatchDifferentClass() {
            final ContextReference first = new ContextReference(new Object());

            assertFalse(first.equals(new Object()));
        }

        @Test
        public void shouldNotMatchDifferentlyNullAliasedContextReferences() {
            final ContextReference first = new ContextReference(new Object());
            final ContextReference second = new ContextReference(new Object());

            first.as("true");
            second.as(null);

            assertFalse(first.equals(second));
        }

        @Test
        public void shouldNotMatchDifferentlyAliasedContextReferences() {
            final ContextReference first = new ContextReference(new Object());
            final ContextReference second = new ContextReference(new Object());

            first.as("true");
            second.as("false");

            assertFalse(first.equals(second));
        }

        @Test
        public void shouldMatchIdenticalContextReference() {
            final ContextReference first = new ContextReference(new Object());
            final ContextReference second = new ContextReference(new Object());

            first.as("ref");
            second.as("ref");

            assertEquals(first, second);
        }
    }
}
