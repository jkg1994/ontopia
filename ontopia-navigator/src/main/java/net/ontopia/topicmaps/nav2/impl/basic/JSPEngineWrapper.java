/*
 * #!
 * Ontopia Navigator
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.nav2.impl.basic;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.JspEngineInfo;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: This class is used to hide the differences between the
 * JSP 1.1 and JSP 1.2 APIs so that the OKS can support both. This
 * means we can make use of the improvements in JSP 1.2 if they are
 * there, and if they are not we just make do without, and without
 * getting errors loading the .class files.
 */
public class JSPEngineWrapper {
  private static WrapperIF wrapper;

  // --- Helper methods

  public static String getServletContextName(ServletContext context) {
    return getWrapper().getServletContextName(context);
  }

  public static JspException getJspException(String message,
                                             Exception exception) {
    return getWrapper().getJspException(message, exception);
  }

  public static JspTagException getJspTagException(String message,
                                                   Exception exception) {
    return getWrapper().getJspTagException(message, exception);
  }
  
  public static void setRequestEncoding(ServletRequest request, String encoding)
    throws UnsupportedEncodingException {
    getWrapper().setRequestEncoding(request, encoding);
  }

  public static Map getParameterMap(ServletRequest request) {
    return getWrapper().getParameterMap(request);
  }
  
  // --- Internal methods

  private static WrapperIF getWrapper() {
    if (wrapper != null)
      return wrapper;

    if (JspFactory.getDefaultFactory() == null)
      // ontojsp can't set the default factory, since ontojsp needs to
      // be able to run inside app servers, which will set the default.
      // in the test suite there will therefore be no default factory.
      return new JSP12Wrapper(); 

    JspEngineInfo engine = JspFactory.getDefaultFactory().getEngineInfo();
    String version = engine.getSpecificationVersion();
    if (version.equals("2.0"))
      wrapper = new JSP20Wrapper();
    else if (version.equals("1.2"))
      wrapper = new JSP12Wrapper();
    else
      wrapper = new JSP11Wrapper();

    return wrapper;
  }

  // --- WrapperIF

  interface WrapperIF {

    String getServletContextName(ServletContext context);

    JspException getJspException(String message, Exception exception);

    JspTagException getJspTagException(String message,
                                              Exception exception);
    
    void setRequestEncoding(ServletRequest request, String encoding)
      throws UnsupportedEncodingException;

    Map getParameterMap(ServletRequest request);
  }

  // --- JSP20Wrapper

  static class JSP20Wrapper extends JSP12Wrapper {

    public JspTagException getJspTagException(String message,
                                              Exception exception) {
      try {
        Class theclass = Class.forName("javax.servlet.jsp.JspTagException");
        Class string = "".getClass();
        Class throwable = Class.forName("java.lang.Throwable");
        Class[] types = {string, throwable};
        Constructor c = theclass.getConstructor(types);
        Object[] args = {message, exception};
        return (JspTagException) c.newInstance(args);
      } catch (Exception e) {
        throw new OntopiaRuntimeException(e);
      }
    }
  }
  
  // --- JSP12Wrapper

  static class JSP12Wrapper implements WrapperIF {

    public String getServletContextName(ServletContext context) {
      return context.getServletContextName();
    }

    public JspException getJspException(String message, Exception exception) {
      return new JspException(message, exception);
    }

    public JspTagException getJspTagException(String message,
                                              Exception exception) {
      return new JspTagException(message + ": " + exception);
    }
    
    public void setRequestEncoding(ServletRequest request, String encoding)
      throws UnsupportedEncodingException {
      request.setCharacterEncoding(encoding);
    }

    public Map getParameterMap(ServletRequest request) {
      return request.getParameterMap();
    }
    
  }

  // --- JSP11Wrapper

  static class JSP11Wrapper implements WrapperIF {

    public String getServletContextName(ServletContext context) {
      return context.toString();
    }

    public JspException getJspException(String message, Exception exception) {
      return new JspException(message);
    }
    
    public JspTagException getJspTagException(String message,
                                              Exception exception) {
      return new JspTagException(message + ": " + exception);
    }

    public void setRequestEncoding(ServletRequest request, String encoding)
      throws UnsupportedEncodingException {
      // no way to do this on JSP 1.1. may have to add a workaround if
      // customers get desperate
    }
    
    public Map getParameterMap(ServletRequest request) {
      Map map = new HashMap();
      Enumeration enumeration = request.getParameterNames();
      while (enumeration.hasMoreElements()) {
        String param = (String) enumeration.nextElement();
        map.put(param, request.getParameterValues(param));
      }
      return map;
    }
  }
  
}
