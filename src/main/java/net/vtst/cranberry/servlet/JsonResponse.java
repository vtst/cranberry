package net.vtst.cranberry.servlet;

import com.google.api.server.spi.ServiceException;

/**
 * Standard JSON object to be returned by a JSON servlet.  This object allows to represent success and
 * error responses.
 * @param <T>  The class of the JSON object used as result in a success response.
 */
public class JsonResponse<T> {

  public int status;
  public String statusText;
  public String error;
  public T result;
  
  /**
   * Creates a success response.
   * @param result  The result value.
   */
  public JsonResponse(T result) {
    this.status = STATUS_OK;
    this.result = result;
  }
  
  /**
   * Creates an error response.
   * @param exn  The service exception to wrap in the response.
   */
  public JsonResponse(ServiceException exn) {
    this.status = exn.getStatusCode();
    this.statusText = exn.getMessage();
    this.error = Integer.toString(this.status) + ": " + this.statusText;
  }

  private static final int STATUS_OK = 200;
  
}
