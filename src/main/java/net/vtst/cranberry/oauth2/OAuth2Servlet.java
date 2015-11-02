package net.vtst.cranberry.oauth2;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.vtst.cranberry.servlet.JsonResponse;
import net.vtst.cranberry.servlet.OAuth2JsonHttpServlet;
import net.vtst.cranberry.servlet.ServletUtils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.utils.SystemProperty;

/**
 * An HTTP Servlet to access to OAuth2 identification settings from the client.  This servlets answers to three sub-paths:
 * <ul>
 *   <li><code>/token</code>: Returns a JSON <code>Token</code> object, containing the current OAuth2 token. 
 *     Takes <code>min_lifetime_seconds</code> as an optional URL parameter.</li>
 *   <li><code>/reset</code> (admin only): Reset all credential stored in the datastore.</li>
 *   <li><code>/devapi</code> (development server only): Create credentials for use on the development server.</li>
 * </ul>
 */
public class OAuth2Servlet extends OAuth2JsonHttpServlet {
  
  private static final long serialVersionUID = 1L;
  
  /**
   * The class returned for the <code>/token</code> request.
   */
  public static class Token {
    public String access_token;
    public Long expires;
    public String debug;
    public Token(Credential credential, boolean debug) {
      this.access_token = credential.getAccessToken();
      this.expires = credential.getExpirationTimeMilliseconds();
      if (debug) {
        this.debug = String.format("Token expires in %d seconds, at %s UTC.", 
            credential.getExpiresInSeconds(),
            getUTCISODateFormat().format(credential.getExpirationTimeMilliseconds()));
      }
    }
  }
  
  public JsonResponse<?> fGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ServiceException {
    String subpath = request.getPathInfo();
    if (subpath.equals("/token")) return this.fGetToken(request);
    if (subpath.equals("/reset")) return this.fGetReset(request);
    if (subpath.equals("/devapi")) return this.fGetDevapi(request);
    throw new NotFoundException(request.getRequestURI() + " is not a valid path.");
  }
  
  private JsonResponse<Token> fGetToken(HttpServletRequest request) throws ServletException, IOException, ServiceException {
    Credential credential = OAuth2Manager.getInstance().getAndCheckCredential();

    Integer minLifetimeInSeconds = ServletUtils.getIntParameter(request, "min_lifetime_seconds");
    if (minLifetimeInSeconds != null) {
      Long expiresInSeconds = credential.getExpiresInSeconds();
      if (expiresInSeconds == null || expiresInSeconds < minLifetimeInSeconds) {
        if (!credential.refreshToken()) {
          Logger.getGlobal().log(Level.WARNING, "Cannot refresh OAuth2 token");
        }
      }
    }
    
    boolean debug = ServletUtils.getBooleanParameter(request, "debug", false);    
    return new JsonResponse<Token>(new Token(credential, debug));
  }
  
  private JsonResponse<?> fGetReset(HttpServletRequest request) throws IOException, ForbiddenException {
    checkIsAdmin();
    OAuth2Manager.getInstance().newFlow().getCredentialDataStore().clear();
    return new JsonResponse<Void>((Void) null);
  }
  
  private JsonResponse<?> fGetDevapi(HttpServletRequest request) throws NotFoundException, UnauthorizedException, IOException {
    if (SystemProperty.environment.value() != SystemProperty.Environment.Value.Development) {
      throw new NotFoundException(request.getRequestURI() + " is not a valid path in production.");
    }
    Credential credential = OAuth2Manager.getInstance().getAndCheckCredential();
    OAuth2Manager.getInstance().storeCredentialForDevApi(credential);
    return new JsonResponse<Void>((Void) null);
  }
  
  private static void checkIsAdmin() throws ForbiddenException {
    User user = UserServiceFactory.getUserService().getCurrentUser();
    if (user == null || !UserServiceFactory.getUserService().isUserAdmin())
      throw new ForbiddenException("Login as administrator required");
  }
  
  private static DateFormat getUTCISODateFormat() {
    TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    df.setTimeZone(tz);
    return df;
  }

}
