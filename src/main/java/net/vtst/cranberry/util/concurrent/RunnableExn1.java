package net.vtst.cranberry.util.concurrent;

public interface RunnableExn1<Exn extends Exception> {

  public void run() throws Exn;

}
