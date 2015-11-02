package net.vtst.cranberry.oauth2;

import java.util.Collection;

public interface OAuth2Config {
  
  public Collection<String> getScopes();
  public String getAccessType();
  public String getCallbackPath();
  public String getClientSecretsPath();

}
