
package net.ontopia.topicmaps.classify;

import javax.servlet.http.*;

/**
 * INTERNAL: Interface implemented by ClassifyPluginIFs that want
 * access to the current HTTP request in a servlet environment.
 */
public interface HttpServletRequestAwareIF {

  /**
   * INTERNAL: Callback method handing over the current servlet
   * request. This method will be called once per HTTP request.
   */ 
  public void setRequest(HttpServletRequest request);
  
}