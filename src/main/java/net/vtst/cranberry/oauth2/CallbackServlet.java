package net.vtst.cranberry.oauth2;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeCallbackServlet;

/**
 * Callback servlet for OAuth2 authentification.  This is the servlet the user is redirected to after
 * successfull login in its Google Account.
 */
public class CallbackServlet extends AbstractAppEngineAuthorizationCodeCallbackServlet {

  private static final long serialVersionUID = 1L;

  private String getOriginUri(HttpSession session) {
    Object attribute = session.getAttribute(OAuth2Manager.SESSION_ATTRIBUTE);
    if (attribute instanceof String) return (String) attribute;
    return "/";
  }
  
  @Override
  protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential)
      throws ServletException, IOException {
    HttpSession session = req.getSession();
    String originUri = this.getOriginUri(session);
    session.invalidate();    
    resp.sendRedirect(originUri);
  }

  @Override
  protected void onError(
      HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse)
      throws ServletException, IOException {
    resp.getWriter().print("<h3>Authorization denied</h3>");
    resp.setStatus(200);
    resp.addHeader("Content-Type", "text/html");
  }

  @Override
  protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
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
