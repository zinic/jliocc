/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.jps.lioc.context.support;

/**
 *
 * @author zinic
 */
public class SuperTestObject {
    private ExtendedTestObject extendedDependency;
    private SimpleTestObject dependency;

    public SuperTestObject(ExtendedTestObject extendedDependency, SimpleTestObject dependency) {
        this.extendedDependency = extendedDependency;
        this.dependency = dependency;
    }

    public SuperTestObject(ExtendedTestObject extendedDependency) {
        this.extendedDependency = extendedDependency;
    }

    public SimpleTestObject getDependency() {
        return dependency;
    }

    public ExtendedTestObject getExtendedDependency() {
        return extendedDependency;
    }
}
