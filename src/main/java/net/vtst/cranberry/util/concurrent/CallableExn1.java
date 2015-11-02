package net.vtst.cranberry.util.concurrent;

import java.util.concurrent.Callable;

public interface CallableExn1<V, Exn extends Exception> extends Callable<V> {
  
  @Override
  public V call() throws Exn;
  
}
