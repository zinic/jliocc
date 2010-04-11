/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.jps.lioc.context.interceptor;

/**
 *
 * @author zinic
 */
public interface InterceptorHandler {

    void willExitCallChain();

    void willResumeCall();

    void willReturn(Object returningObject);

}
