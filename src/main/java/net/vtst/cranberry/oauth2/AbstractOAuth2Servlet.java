package net.vtst.cranberry.oauth2;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeServlet;

/**
 * Abstract class for an HTTP servlet requiring OAuth2 authentification.
 */
public abstract class AbstractOAuth2Servlet extends AbstractAppEngineAuthorizationCodeServlet {
  
  private static final long serialVersionUID = 1L;

  private static String getFullURL(HttpServletRequest request) {
    StringBuffer requestURL = request.getRequestURL();
    String queryString = request.getQueryString();

    if (queryString == null) {
        return requestURL.toString();
    } else {
        return requestURL.append('?').append(queryString).toString();
    }
  }
  
  @Override
  protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
    req.getSession().setAttribute(OAuth2Manager.SESSION_ATTRIBUTE, getFullURL(req));
    return getManager().getRedirectUri(req);
  }

  @Override
  protected AuthorizationCodeFlow initializeFlow() throws IOException {
    return getManager().newFlow();
  }
  
  /**
   * The default implementation of this method returns the default instance of OAuth2Manager. You may override to use a
   * specific configuration.
   * @return The <code>OAuth2Manager</code> instance to be used with this servlet.
   */
  protected OAuth2Manager getManager() {
    return OAuth2Manager.getInstance();
  }

}
