package net.vtst.cranberry.util.concurrent;

import java.util.concurrent.ExecutorService;

public class ExecutorsExn {
  
  public static <V, Exn extends Exception> FutureExn1<V, Exn> submit(ExecutorService executor, CallableExn1<V, Exn> callable) {
    return new FutureExn1Impl<V, Exn>(executor.submit(callable));
  }

  public static <V, Exn extends Exception> FutureExn1<Void, Exn> submit(ExecutorService executor, RunnableExn1<Exn> runnable) {
    return new FutureExn1Impl<Void, Exn>(executor.submit(asCallable(runnable)));
  }
  
  private static <Exn extends Exception> CallableExn1<Void, Exn> asCallable(final RunnableExn1<Exn> runnable) {
    return new CallableExn1<Void, Exn>() {
      @Override
      public Void call() throws Exn {
        runnable.run();
        return null;
      }
    };
  }

}
