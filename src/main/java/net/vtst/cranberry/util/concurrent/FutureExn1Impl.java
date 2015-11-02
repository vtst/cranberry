package net.vtst.cranberry.util.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class FutureExn1Impl<V, Exn extends Exception> implements FutureExn1<V, Exn> {
  
  private Future<V> future;

  FutureExn1Impl(Future<V> future) {
    this.future = future;
  }

  @Override
  public boolean cancel(boolean arg0) {
    return this.future.cancel(arg0);
  }

  @SuppressWarnings("unchecked")
  @Override
  public V get() throws InterruptedException, Exn {
    try {
      return this.future.get();
    } catch (ExecutionException e) {
      e.printStackTrace();
      Throwable cause = e.getCause();
      throw (Exn) cause;
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public V get(long arg0, TimeUnit arg1) throws InterruptedException, TimeoutException, Exn {
    try {
      return this.future.get(arg0, arg1);
    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      throw (Exn) cause;
    }
  }

  @Override
  public boolean isCancelled() {
    return this.future.isCancelled();
  }

  @Override
  public boolean isDone() {
    return this.future.isDone();
  }

}
