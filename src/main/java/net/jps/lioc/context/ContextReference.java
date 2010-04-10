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

import net.jps.util.Is;

/**
 *
 * @author zinic
 */
public class ContextReference implements IContextReference, ContextReferenceAliaser {

    public static IContextReference newFilter(String alias) {
        return new ContextReference(alias);
    }
    
    private final Object registeredObject;
    private String alias;

    public ContextReference(Object registeredObject) {
        this.registeredObject = registeredObject;
        alias = Is.aClass(registeredObject) ? ((Class)registeredObject).getCanonicalName() : registeredObject.getClass().getCanonicalName();
    }

    private ContextReference(String alias) {
        registeredObject = null;
        this.alias = alias;
    }

    @Override
    public void as(String referenceAlias) {
        alias = referenceAlias;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public Object getRegisteredObject() {
        return registeredObject;
    }

    @Override
    public int compareTo(IContextReference o) {
        return alias.compareTo(o.getAlias());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final ContextReference other = (ContextReference) obj;
//        if (this.registeredObject != other.registeredObject && (this.registeredObject == null || !this.registeredObject.equals(other.registeredObject))) {
//            return false;
//        }

        if ((this.alias == null) ? (other.alias != null) : !this.alias.equals(other.alias)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7393;

        hash = 4093 * hash + (this.alias != null ? this.alias.hashCode() : 0);

        return hash;
    }
}
