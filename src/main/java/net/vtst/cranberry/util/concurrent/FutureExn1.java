package net.vtst.cranberry.util.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface FutureExn1<V, Exn extends Exception> {
  
  public boolean cancel(boolean arg0);
  public V get() throws InterruptedException, Exn;
  public V get(long arg0, TimeUnit arg1) throws InterruptedException, TimeoutException, Exn;
  public boolean isCancelled();
  public boolean isDone();

}
