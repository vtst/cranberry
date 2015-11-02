package net.vtst.cranberry.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

// TODO: Check it works without being public.
public class AppServletContextListener implements ServletContextListener {

  @Override
  public void contextDestroyed(ServletContextEvent event) {
    ServletUtils.servletContext = null;
  }

  @Override
  public void contextInitialized(ServletContextEvent event) {
    ServletUtils.servletContext = event.getServletContext();
  }

}
