package net.vtst.cranberry.gapi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import net.vtst.cranberry.util.concurrent.ExecutorsAE;
import net.vtst.cranberry.util.concurrent.ExecutorsExn;
import net.vtst.cranberry.util.concurrent.FutureExn1;
import net.vtst.cranberry.util.concurrent.NoThreadExecutor;
import net.vtst.cranberry.util.concurrent.RunnableExn1;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.services.AbstractGoogleClient;

public class ThreadedBatchRequest {
  
  private static final boolean MULTI_THREADED = true;
  
  private AbstractGoogleClient client;
  private int maxNumberOfThreads;
  private int minNumberOfRequestsPerThread;
  private List<BatchRequest> batchRequests;
  private int currentIndex = -1;

  public ThreadedBatchRequest(AbstractGoogleClient client, int maxNumberOfThreads, int minNumberOfRequestsPerThread) {
    this.client = client;
    this.maxNumberOfThreads = maxNumberOfThreads;
    this.minNumberOfRequestsPerThread = minNumberOfRequestsPerThread;
    this.batchRequests = new ArrayList<>(maxNumberOfThreads);
  }
  
  private BatchRequest addBatchRequest() {
    BatchRequest batchRequest = client.batch();
    batchRequests.add(batchRequest);
    return batchRequest;
  }
  
  public BatchRequest next() {
    if (batchRequests.size() == 0) {
      return this.addBatchRequest();
    } else if (currentIndex == -1) {
     BatchRequest batchRequest = this.batchRequests.get(this.batchRequests.size() - 1);
     if (batchRequest.size() < this.minNumberOfRequestsPerThread) return batchRequest;
     if (this.batchRequests.size() < this.maxNumberOfThreads) return this.addBatchRequest();
    }
    this.currentIndex = (this.currentIndex + 1) % this.maxNumberOfThreads;
    return this.batchRequests.get(this.currentIndex);
  }
  
  private static class BatchRequestRunnable implements RunnableExn1<IOException> {

    private BatchRequest batchRequest;

    public BatchRequestRunnable(BatchRequest batchRequest) {
      this.batchRequest = batchRequest;
    }
    
    @Override
    public void run() throws IOException {
      this.batchRequest.execute();      
    }
    
  }
  
  public void execute() throws IOException {
    if (this.batchRequests.size() == 0) return;
    ExecutorService executor = MULTI_THREADED ? ExecutorsAE.newFixedThreadPool(this.batchRequests.size()) : new NoThreadExecutor();
    try {
      List<FutureExn1<Void, IOException>> futures = new ArrayList<>(this.batchRequests.size());
      for (BatchRequest batchRequest: this.batchRequests) {
        futures.add(ExecutorsExn.submit(executor, new BatchRequestRunnable(batchRequest)));
      }
      try {
        for(FutureExn1<Void, IOException> future: futures) future.get();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    } finally {
      executor.shutdownNow();
    }
  }

}
