package net.vtst.cranberry.util.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class NoThreadExecutor implements ExecutorService {

  @Override
  public void execute(Runnable runnable) {
    runnable.run();
  }

  @Override
  public boolean awaitTermination(long arg0, TimeUnit arg1) {
    return false;
  }

  @Override
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> callables) {
    List<Future<T>> futures = new ArrayList<Future<T>>(callables.size());
    for (Callable<T> callable: callables) futures.add(new NoThreadFutureFromCallable<T>(callable));
    return futures;
  }

  @Override
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> callables, long arg1, TimeUnit arg2) {
    return this.invokeAll(callables);
  }

  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> callables) throws ExecutionException {
    for (Callable<T> callable: callables) {
      try {
        return callable.call();
      } catch (Exception e) {
        throw new ExecutionException(e);
      }
    }
    return null;
  }

  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> callables, long arg1, TimeUnit arg2) throws ExecutionException {
    return this.invokeAny(callables);
  }

  @Override
  public boolean isShutdown() {
    return false;
  }

  @Override
  public boolean isTerminated() {
    return false;
  }

  @Override
  public void shutdown() {}

  @Override
  public List<Runnable> shutdownNow() {
    return null;
  }

  @Override
  public <T> Future<T> submit(Callable<T> callable) {
    return new NoThreadFutureFromCallable<T>(callable);
  }

  @Override
  public Future<?> submit(Runnable runnable) {
    return this.submit(runnable, null);
  }

  @Override
  public <T> Future<T> submit(Runnable runnable, T result) {
    return new NoThreadFutureFromRunnable<T>(runnable, result);
  }

  private static abstract class NoThreadFuture<T> implements Future<T> {

    private boolean done = false;

    @Override
    public boolean cancel(boolean arg0) {
      return false;
    }

    protected abstract T getInternal() throws Exception;
    
    @Override
    public T get() throws ExecutionException {
      this.done  = true;
      try {
        return this.getInternal();
      } catch (Exception e) {
        throw new ExecutionException(e);
      }
    }

    @Override
    public T get(long arg0, TimeUnit arg1) throws ExecutionException {
      return this.get();
    }

    @Override
    public boolean isCancelled() {
      return false;
    }

    @Override
    public boolean isDone() {
      return this.done;
    }
  }
  
  private static class NoThreadFutureFromCallable<T> extends NoThreadFuture<T> {
    
    private Callable<T> callable;

    private NoThreadFutureFromCallable(Callable<T> callable) {
      this.callable = callable;
    }

    @Override
    protected T getInternal() throws Exception {
      return this.callable.call();
    }
    
  }

  private static class NoThreadFutureFromRunnable<T> extends NoThreadFuture<T> {
    
    private Runnable runnable;
    private T result;

    private NoThreadFutureFromRunnable(Runnable runnable, T result) {
      this.runnable = runnable;
      this.result = result;
    }

    @Override
    protected T getInternal() throws Exception {
      this.runnable.run();
      return this.result;
    }
    
  }

}
