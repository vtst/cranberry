package net.vtst.cranberry.oauth2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * An HTTP Servlet that serves static files stored in the WAR archive, requiring OAuth2 login for any request.
 * If OAuth2 credentials are not present in the request, the user is automatically redirected to a login page.
 */
public class FileServlet extends AbstractOAuth2Servlet {
  
  private static final long serialVersionUID = 1L;

  private static final Map<String, String> fileExtensionToMimeType = new ConcurrentHashMap<>();
  static {
    setMimeTypeForExtension("html", "text/html");
  }
  
  /**
   * This method can be overridden to specify a mapping between request paths and file paths in the WAR archive.
   * By default, this method is the identity.
   * @param path  The path in the request.
   * @return  The translated path, corresponding to a file in the WAR file.
   */
  protected String translatePath(String path) {
    return path;
  }

  /**
   * Set the MIME type associated with a file extension.
   * @param extension  The file extension (without dot).
   * @param mimeType  The associated MIME type.
   */
  public static void setMimeTypeForExtension(String extension, String mimeType) {
    fileExtensionToMimeType.put(extension, mimeType);    
  }
  
  /**
   * Return the MIME type to be associated with a file extension.  By default, this method looks into the
   * file extension map.
   * @param extension  The file extension (without the dot).
   * @return  The MIME type.
   */
  protected String getMimeTypeForExtension(String extension) {
    return fileExtensionToMimeType.get(extension);
  }

  /**
   * Return the MIME type to be associated with a file of the WAR archive.  By default, this method
   * calls getMimeTypeForExtension on the file extension.
   * @param file
   * @return  The MIME type.
   */
  protected String getMimeType(File file) {
    return getMimeTypeForExtension(getFileExtension(file));
  }

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String path = this.translatePath(request.getRequestURI());
    if (path == null) {
      response.sendError(403, "Not found");
      return;
    }
    File file = new File(getServletContext().getRealPath(path));
    response.setContentType(getMimeType(file));
    response.setCharacterEncoding("UTF-8");
    try {
      Reader input = new InputStreamReader(new FileInputStream(file));
      copy(input, response.getWriter());
      input.close();
    } catch (FileNotFoundException e) {
      response.sendError(403, "Not found");    
    }
  }
    
  private static int copy(Reader input, Writer output) throws IOException {
    char[] buffer = new char[1024];
    int count = 0;
    int n = 0;
    while (-1 != (n = input.read(buffer))) {
      output.write(buffer, 0, n);
      count += n;
    }
    return count;
  }
  
  private static String getFileExtension(File file) {
    String fileName = file.getName();
    int i = fileName.lastIndexOf('.');
    if (i > 0) return fileName.substring(i + 1);
    else return "";
  }
  
}