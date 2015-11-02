package net.vtst.cranberry.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.vtst.cranberry.oauth2.AbstractOAuth2Servlet;

import com.google.api.server.spi.ServiceException;

/**
 * A base class for implementing an HTTP servlet requiring OAuth2 authentification and returning JSON objects.
 */
public class OAuth2JsonHttpServlet extends AbstractOAuth2Servlet {
  private static final long serialVersionUID = 1L;
  
  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public final void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    try {
      JsonHttpServlet.writeJson(response, this.fGet(request, response));
    } catch (ServiceException exn) {
      JsonHttpServlet.writeJson(response, new JsonResponse<Object>(exn));
    }    
  }
  
  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public final void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    try {
      JsonHttpServlet.writeJson(response, this.fPost(request, response));
    } catch (ServiceException exn) {
      JsonHttpServlet.writeJson(response, new JsonResponse<Object>(exn));
    }    
  }
    
  /**
   * Override this method in sub-classes to answer GET requests.  This is equivalent to <code>doGet</code> in <code>HttpServlet</code>,
   * except you must return the JSON object instead of writing it to the response.
   * @return  The JSON object.  This should typically be a JsonResponse<?>, but this is not mandatory.
   * @throws ServiceException  If this method throws a <code>ServiceException</code>, the servlet will return a JSON object representing
   *     the error.
   */
  protected Object fGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ServiceException {
    throw new ServiceException(JsonHttpServlet.METHOD_NOT_ALLOWED_STATUS, JsonHttpServlet.METHOD_NOT_ALLOWED_STATUS_TEXT);
  }

  /**
   * Override this method in sub-classes to answer POST requests.  This is equivalent to <code>doGet</code> in <code>HttpServlet</code>,
   * except you must return the JSON object instead of writing it to the response.
   * @return  The JSON object.  This should typically be a JsonResponse<?>, but this is not mandatory.
   * @throws ServiceException  If this method throws a <code>ServiceException</code>, the servlet will return a JSON object representing
   *     the error.
   */
  protected Object fPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ServiceException {
    throw new ServiceException(JsonHttpServlet.METHOD_NOT_ALLOWED_STATUS, JsonHttpServlet.METHOD_NOT_ALLOWED_STATUS_TEXT);
  }

}
