package net.vtst.cranberry.servlet;

import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.google.api.server.spi.ServiceException;

/**
 * Utility methods for HTTP servlets.
 */
public class ServletUtils {
  
  static ServletContext servletContext;
  
  public static String readResource(String path) throws IOException {
    if (servletContext == null) throw new IOException("No servlet context found");
    InputStreamReader reader = new InputStreamReader(ServletUtils.getServletContext().getResourceAsStream(path));
    StringBuilder builder = new StringBuilder();
    char[] cbuf = new char[1024];
    while (true) {
      int len = reader.read(cbuf);
      if (len < 0) break;
      builder.append(cbuf, 0, len);
    }
    return builder.toString();
  }
  
  public static ServletContext getServletContext() {
    if (servletContext == null) throw new RuntimeException("No servlet context found.");
    return servletContext;
  }

  /**
   * Interface to parse typed parameters from servlet requests.
   * @param <T>
   */
  private static interface Parser<T> {
    /**
     * Parses a string value into a typed value
     * @param value  The string to be parsed.
     * @return  The typed value.
     * @throws IllegalArgumentException  If the string value cannot be parsed.
     */
    public T parse(String value) throws IllegalArgumentException;
  }

  /**
   * Get a typed parameter by name from a servlet request.
   * @param request  The servlet request to look into.
   * @param parameterName  The name of the parameter.
   * @param parser  The parser to use to conver the string value into a typed value.
   * @param defaultValue  The value to return if the parameter is not set in the request.
   * @return  The parameter value
   * @throws ServiceException  If the parameter value cannot be parsed.
   */
  public static <T> T getTypedParameter(HttpServletRequest request, String parameterName, Parser<T> parser, T defaultValue) throws ServiceException {
    String value = request.getParameter(parameterName);
    if (value == null) return defaultValue;
    try {
      return parser.parse(value);
    } catch (IllegalArgumentException exn) {
      throw new ServiceException(400, "The request parameter '" + parameterName + "' has an illegal value: " + value);
    }
  }

  /**
   * Get a typed parameter by name from a servlet request. Throws a service exception if the parameter is not set in the request.
   * @param request  The servlet request to look into.
   * @param parameterName  The name of the parameter.
   * @param parser  The parser to use to conver the string value into a typed value.
   * @param defaultValue  The returned value if the parameter is not set in the request.
   * @return  The parameter value.  Null if the parameter is not set in the request.
   * @throws ServiceException  If the parameter is not set in the request, or its value cannot be parsed.
   */
  public static <T> T getRequiredTypedParameter(HttpServletRequest request, String parameterName, Parser<T> parser) throws ServiceException {
    String value = request.getParameter(parameterName);
    if (value == null) {
      throw new ServiceException(400, "The request parameter '" + parameterName + "' is required.");
    };
    try {
      return parser.parse(value);
    } catch (IllegalArgumentException exn) {
      throw new ServiceException(400, "The request parameter '" + parameterName + "' has an illegal value: " + value);
    }
  }

  /**
   * Get a typed parameter by name from a servlet request.  Returns null if the parameter is not set in the request.
   * @param request  The servlet request to look into.
   * @param parameterName  The name of the parameter.
   * @param parser  The parser to use to conver the string value into a typed value.
   * @param defaultValue  The returned value if the parameter is not set in the request.
   * @return  The parameter value.  Null if the parameter is not set in the request.
   * @throws ServiceException  If the parameter value cannot be parsed.
   */
  public static <T> T getTypedParameter(HttpServletRequest request, String parameterName, Parser<T> parser) throws ServiceException {
    return getTypedParameter(request, parameterName, parser, null);
  }
  
  private static final Parser<Integer> INT_PARSER = new Parser<Integer>() {
    @Override
    public Integer parse(String value) throws IllegalArgumentException {
      return Integer.parseInt(value);
    }
  };
  
  /**
   * Get an integer parameter from a servlet request.  Returns null if the parameter is not set in the request.
   * @param request  The servlet request to look into.
   * @param parameterName  The name of the parameter.
   * @return  The parameter value.  Null if the parameter is not set in the request.
   * @throws ServiceException
   */
  public static Integer getIntParameter(HttpServletRequest request, String parameterName) throws ServiceException {
    return getTypedParameter(request, parameterName, INT_PARSER);
  }

  /**
   * Get an integer parameter from a servlet request.  Returns null if the parameter is not set in the request.
   * @param request  The servlet request to look into.
   * @param parameterName  The name of the parameter.
   * @return  The parameter value.  Null if the parameter is not set in the request.
   * @throws ServiceException
   */
  public static Integer getRequiredIntParameter(HttpServletRequest request, String parameterName) throws ServiceException {
    return getRequiredTypedParameter(request, parameterName, INT_PARSER);
  }

  /**
   * Get an integer parameter from a servlet request.
   * @param request  The servlet request to look into.
   * @param parameterName  The name of the parameter.
   * @param defaultValue  The returned value if the parameter is not set in the request.
   * @return  The parameter value.
   * @throws ServiceException
   */
  public static int getIntParameter(HttpServletRequest request, String parameterName, int defaultValue) throws ServiceException {
    return getTypedParameter(request, parameterName, INT_PARSER, defaultValue);
  }
    
  private static final Parser<Long> LONG_PARSER = new Parser<Long>() {
    @Override
    public Long parse(String value) throws IllegalArgumentException {
      return Long.parseLong(value);
    }
  };
  
  /**
   * Get a long parameter from a servlet request.  Returns null if the parameter is not set in the request.
   * @param request  The servlet request to look into.
   * @param parameterName  The name of the parameter.
   * @return  The parameter value.  Null if the parameter is not set in the request.
   * @throws ServiceException
   */
  public static Long getLongParameter(HttpServletRequest request, String parameterName) throws ServiceException {
    return getTypedParameter(request, parameterName, LONG_PARSER);
  }
  
  /**
   * Get a long parameter from a servlet request.  Returns null if the parameter is not set in the request.
   * @param request  The servlet request to look into.
   * @param parameterName  The name of the parameter.
   * @return  The parameter value.  Null if the parameter is not set in the request.
   * @throws ServiceException
   */
  public static Long getRequiredLongParameter(HttpServletRequest request, String parameterName) throws ServiceException {
    return getRequiredTypedParameter(request, parameterName, LONG_PARSER);
  }

  /**
   * Get a long parameter from a servlet request.
   * @param request  The servlet request to look into.
   * @param parameterName  The name of the parameter.
   * @param defaultValue  The returned value if the parameter is not set in the request.
   * @return  The parameter value.
   * @throws ServiceException
   */
  public static long getLongParameter(HttpServletRequest request, String parameterName, long defaultValue) throws ServiceException {
    return getTypedParameter(request, parameterName, LONG_PARSER, defaultValue);
  }

  private static final Parser<Boolean> BOOLEAN_PARSER = new Parser<Boolean>() {
    @Override
    public Boolean parse(String value) throws IllegalArgumentException {
      return Boolean.parseBoolean(value);
    }
  };
  
  /**
   * Get a boolean parameter from a servlet request.  Returns null if the parameter is not set in the request.
   * @param request  The servlet request to look into.
   * @param parameterName  The name of the parameter.
   * @return  The parameter value.  Null if the parameter is not set in the request.
   * @throws ServiceException
   */
  public static Boolean getBooleanParameter(HttpServletRequest request, String parameterName) throws ServiceException {
    return getTypedParameter(request, parameterName, BOOLEAN_PARSER);
  }
  
  /**
   * Get a boolean parameter from a servlet request.  Returns null if the parameter is not set in the request.
   * @param request  The servlet request to look into.
   * @param parameterName  The name of the parameter.
   * @return  The parameter value.  Null if the parameter is not set in the request.
   * @throws ServiceException
   */
  public static Boolean getRequiredBooleanParameter(HttpServletRequest request, String parameterName) throws ServiceException {
    return getRequiredTypedParameter(request, parameterName, BOOLEAN_PARSER);
  }

  /**
   * Get a boolean parameter from a servlet request.  Returns null if the parameter is not set in the request.
   * @param request  The servlet request to look into.
   * @param parameterName  The name of the parameter.
   * @return  The parameter value.  Null if the parameter is not set in the request.
   * @throws ServiceException
   */
  public static boolean getBooleanParameter(HttpServletRequest request, String parameterName, boolean defaultValue) throws ServiceException {
    return getTypedParameter(request, parameterName, BOOLEAN_PARSER, defaultValue);
  }

  private static class EnumParser<T extends Enum<?>> implements Parser<T> {

    private Class<T> cls;

    public EnumParser(Class<T> cls) {
      this.cls = cls;
    }
    
    @Override
    public T parse(String value) throws IllegalArgumentException {
      for (T t : cls.getEnumConstants()) {
        if (t.name().equals(value)) return t;
      }
      throw new IllegalArgumentException();
    }
    
  }
  
  /**
   * Get an enum parameter from a servlet request.  Returns null if the parameter is not set in the request.
   * @param request  The servlet request to look into.
   * @param parameterName  The name of the parameter.
   * @return  The parameter value.  Null if the parameter is not set in the request.
   * @throws ServiceException
   */
  public static <T extends Enum<?>> T getEnumParameter(HttpServletRequest request, String parameterName, Class<T> cls) throws ServiceException {
    return getTypedParameter(request, parameterName, new EnumParser<T>(cls));
  }
  
  /**
   * Get an enum parameter from a servlet request.  Returns null if the parameter is not set in the request.
   * @param request  The servlet request to look into.
   * @param parameterName  The name of the parameter.
   * @return  The parameter value.  Null if the parameter is not set in the request.
   * @throws ServiceException
   */
  public static <T extends Enum<?>> T getRequiredEnumParameter(HttpServletRequest request, String parameterName, Class<T> cls) throws ServiceException {
    return getRequiredTypedParameter(request, parameterName, new EnumParser<T>(cls));
  }

  /**
   * Get an enum parameter from a servlet request.
   * @param request  The servlet request to look into.
   * @param parameterName  The name of the parameter.
   * @param defaultValue  The returned value if the parameter is not set in the request.
   * @return  The parameter value.
   * @throws ServiceException
   */
  public static <T extends Enum<?>> T getEnumParameter(HttpServletRequest request, String parameterName, Class<T> cls, T defaultValue) throws ServiceException {
    return getTypedParameter(request, parameterName, new EnumParser<T>(cls), defaultValue);
  }

}
