package net.vtst.cranberry.oauth2;

import java.util.Arrays;
import java.util.Collection;

public class OAuth2DefaultConfig implements OAuth2Config {
    
  private Collection<String> scopes;
  private String accessType;
  private String callbackPath = "/oauth2callback";
  private String clientSecretsPaths = "/client_secrets.json";

  public OAuth2DefaultConfig() {
    this.scopes = Arrays.asList(getProperty("net.vtst.cranberry.oauth2.scopes", "").split(" "));
    this.accessType = getProperty("net.vtst.cranberry.oauth2.accessType", "offline");
    this.callbackPath = getProperty("net.vtst.cranberry.oauth2.callbackPath", "/oauth2callback");
    this.clientSecretsPaths = getProperty("net.vtst.cranberry.oauth2.clientSecretsPaths", "/client_secrets.json");
  }
  
  private String getProperty(String key, String dflt) {
    String value = System.getProperty(key);
    if (value == null) return dflt;
    return value;
  }
  
  @Override
  public Collection<String> getScopes() {
    return scopes;
  }

  @Override
  public String getAccessType() {
    return accessType;
  }

  @Override
  public String getCallbackPath() {
    return callbackPath;
  }

  @Override
  public String getClientSecretsPath() {
    return this.clientSecretsPaths;
  }

}
