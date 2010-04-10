/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
