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
package net.jps.lioc.context.interceptor;

import net.jps.util.Join;

/**
 *
 * @author zinic
 */
public abstract class InterceptorContext implements InterceptorHandler {

    private Action action;
    private Object returnValue;

    public InterceptorContext() {
        action = Action.Default;
        returnValue = null;
    }

    public void setAction(Action actionToSet) {
        if (action != Action.Default) {
            throw new OverloadedInterceptorActionException(Join.strings(
                    "Attempting to force an interceptor to perform an action while another action is already set."
                    + " Current action is: ", action, " - Attempting to overwrite with: ",
                    actionToSet));
        }

        action = actionToSet;
    }

    public void willReturn(Object returningObject) {
        if (returnValue != null) {
            throw new OverloadedInterceptorActionException(Join.strings(
                    "Attempting to force an interceptor to perform an action while another action is already set."
                    + " Current action is to return: ", returnValue, " - Attempting to overwrite with returning: ",
                    returningObject));
        }

        this.returnValue = returningObject;
    }

    public void willResumeCall() {
        setAction(Action.Resume);
    }

    public void willExitCallChain() {
        setAction(Action.Exit);
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public Action shouldDo() {
        return action;
    }
}
