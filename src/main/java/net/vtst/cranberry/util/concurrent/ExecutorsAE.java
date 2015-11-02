package net.vtst.cranberry.util.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.appengine.api.ThreadManager;

public class ExecutorsAE {
  
  public static ExecutorService newCachedThreadPool() {
    return Executors.newCachedThreadPool(ThreadManager.currentRequestThreadFactory());
  }
  
  public static ExecutorService newFixedThreadPool(int nThreads) {
    return Executors.newFixedThreadPool(nThreads, ThreadManager.currentRequestThreadFactory());
  }

}
