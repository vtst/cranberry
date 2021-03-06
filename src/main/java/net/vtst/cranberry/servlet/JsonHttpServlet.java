package net.vtst.cranberry.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.server.spi.ServiceException;
import com.google.gson.Gson;

/**
 * A base class for implementing an HTTP servlet returning JSON objects.
 */
// TODO: Do not override each individual doX.
public class JsonHttpServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  
  private static final Gson gson = new Gson();

  static void writeJson(HttpServletResponse response, Object obj) throws IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    gson.toJson(obj, response.getWriter());
  }
  
  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public final void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    try {
      writeJson(response, this.fGet(request, response));
    } catch (ServiceException exn) {
      writeJson(response, new JsonResponse<Object>(exn));
    }    
  }
  
  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public final void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    try {
      writeJson(response, this.fPost(request, response));
    } catch (ServiceException exn) {
      writeJson(response, new JsonResponse<Object>(exn));
    }    
  }
  
  static final int METHOD_NOT_ALLOWED_STATUS = 405;
  static final String METHOD_NOT_ALLOWED_STATUS_TEXT = "Method Not Allowed";
  
  /**
   * Override this method in sub-classes to answer GET requests.  This is equivalent to <code>doGet</code> in <code>HttpServlet</code>,
   * except you must return the JSON object instead of writing it to the response.
   * @return  The JSON object.  This should typically be a JsonResponse<?>, but this is not mandatory.
   * @throws ServiceException  If this method throws a <code>ServiceException</code>, the servlet will return a JSON object representing
   *     the error.
   */
  protected Object fGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ServiceException {
    throw new ServiceException(METHOD_NOT_ALLOWED_STATUS, METHOD_NOT_ALLOWED_STATUS_TEXT);
  }

  /**
   * Override this method in sub-classes to answer POST requests.  This is equivalent to <code>doGet</code> in <code>HttpServlet</code>,
   * except you must return the JSON object instead of writing it to the response.
   * @return  The JSON object.  This should typically be a JsonResponse<?>, but this is not mandatory.
   * @throws ServiceException  If this method throws a <code>ServiceException</code>, the servlet will return a JSON object representing
   *     the error.
   */
  protected Object fPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ServiceException {
    throw new ServiceException(METHOD_NOT_ALLOWED_STATUS, METHOD_NOT_ALLOWED_STATUS_TEXT);
  }

}
