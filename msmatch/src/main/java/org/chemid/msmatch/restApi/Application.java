package org.chemid.msmatch.restApi;

import org.chemid.msmatch.common.Constants;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Application {
    private Application() {
        //
    }
    /**
     * This method starts an embedded instance of the jetty server and deploys the chemical structure service on it.
     *
     * @param args Arguments to main method
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        Server jettyServer = new Server(Constants.PORT_NO);
        jettyServer.setHandler(context);
        ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(Constants.INIT_ORDER);
        // Tells the Jersey Servlet which REST service/class to load.
        jerseyServlet.setInitParameter("jersey.config.server.provider.classnames",
                MsMatchApi.class.getCanonicalName());
        try {
            jettyServer.start();
            jettyServer.join();
        } finally {
            jettyServer.destroy();
        }
    }
}
