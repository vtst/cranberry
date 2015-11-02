package net.vtst.cranberry.datastore;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.google.gson.Gson;

public class JsonDatastore {
  
  public final static String PROPERTY_NAME = "json";
  private static final Gson GSON = new Gson();

  public static Entity createEntity(String kind, String keyName, Object obj) {
    Entity entity = new Entity(kind, keyName);
    setValue(entity, obj);
    return entity;
  }
  
  public static <T> T getValue(Entity entity, Class<T> cls) {
    Object value = entity.getProperty(PROPERTY_NAME);
    if (value instanceof Text) {
      return GSON.fromJson(((Text) value).getValue(), cls);
    } else {
      return null;
    }
  }
  
  public static void setValue(Entity entity, Object obj) {
    entity.setProperty(PROPERTY_NAME, new Text(GSON.toJson(obj)));
    
  }

  public static Entity createEntity(String kind, Object obj) {
    Entity entity = new Entity(kind);
    setValue(entity, obj);
    return entity;
  }
}
