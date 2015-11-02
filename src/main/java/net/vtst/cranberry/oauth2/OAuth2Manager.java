package net.vtst.cranberry.oauth2;

import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.appengine.datastore.AppEngineDataStoreFactory;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.utils.SystemProperty;

/**
 * Public API to access OAuth2 identification.
 */
public class OAuth2Manager {

  private OAuth2Config config;

  public OAuth2Manager() {
    this(new OAuth2DefaultConfig());
  }
  
  public OAuth2Manager(OAuth2Config config) {
    this.config = config;
  }
  
  // **************************************************************************
  // Public interface.
  
  /**
   * Get the credential for a given user identified by its GAIA ID.
   * @param userId  The GAIA ID of the user to get credential for.  Throws UnauthorizedException if null.
   * @return  The user credential, never null.
   * @throws IOException
   * @throws UnauthorizedException  If the user is not authenticated.
   */
  public Credential getAndCheckCredential(String userId) throws IOException, UnauthorizedException {
    if (userId == null) throw new UnauthorizedException("Login required");
    AuthorizationCodeFlow flow = this.newFlow();
    Credential credential = flow.loadCredential(userId);
    if (credential == null) {
      if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development && "0".equals(userId)) {
        throw new UnauthorizedException("No OAuth2 token found. The development server set user ID to 0 for all API calls. You may want to run /oauth2/devapi");
      }
      throw new UnauthorizedException("No OAuth2 token found");
    }
    return credential;
  }
  
  /**
   * Get the credential for a user, as returned by the user service.
   * @param user  The user to get credential for.  Throws UnauthorizedException if null.
   * @return  The user credential, never null.
   * @throws IOException
   * @throws UnauthorizedException  If the user is not authenticated.
   */
  public Credential getAndCheckCredential(User user) throws IOException, UnauthorizedException {
    if (user == null) throw new UnauthorizedException("Login required");
    return this.getAndCheckCredential(user.getUserId());
  }
  
  /**
   * Get the credential for the current user, as returned by the user service.
   * @return  The user credential, never null.
   * @throws IOException
   * @throws UnauthorizedException  If the user is not authenticated.
   */
  public Credential getAndCheckCredential() throws IOException, UnauthorizedException {
    return getAndCheckCredential(UserServiceFactory.getUserService().getCurrentUser());
  }
  
  // **************************************************************************
  // Package-only interface.
  
  private final GoogleClientSecrets clientSecrets = getClientSecrets();
  
  private GoogleClientSecrets getClientSecrets() {
    try {
      return GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(OAuth2Manager.class.getResourceAsStream(this.config.getClientSecretsPath())));
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
  
  String getRedirectUri(HttpServletRequest req) {
    GenericUrl url = new GenericUrl(req.getRequestURL().toString());
    url.setRawPath(this.config.getCallbackPath());
    return url.build();
  }
  
  GoogleAuthorizationCodeFlow newFlow() throws IOException {
    return new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, this.clientSecrets, this.config.getScopes())
      .setDataStoreFactory(DATA_STORE_FACTORY)
      .setAccessType(this.config.getAccessType())
      .setApprovalPrompt("force")  // This is required to get a refresh token in the case the datastore has been cleared.
      .build();
  }
  
  void storeCredentialForDevApi(Credential credential) throws IOException {
    DataStore<StoredCredential> credentialDataStore = StoredCredential.getDefaultDataStore(DATA_STORE_FACTORY);
    credentialDataStore.set("0", new StoredCredential(credential));
  }
 
  // Static members.

  private static final AppEngineDataStoreFactory DATA_STORE_FACTORY = AppEngineDataStoreFactory.getDefaultInstance();
  private static final HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  static final String SESSION_ATTRIBUTE = "oauth2_origin_url";

  // Singleton instance.
  
  private static OAuth2Manager instance = null;
  
  public static OAuth2Manager getInstance() {
    if (instance == null) instance = new OAuth2Manager();
    return instance;
  }

}
