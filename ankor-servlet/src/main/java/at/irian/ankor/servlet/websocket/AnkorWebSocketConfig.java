package at.irian.ankor.servlet.websocket;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

/**
 * This is the base class of a WebSocket configuration that communicates with the Ankor system.
 * It has to be used in conjunction with {@link AnkorEndpoint} and is meant reside on a web server (e.g. GlassFish)
 * that supports JSR 365 (javax.websocket).
 *
 * As a user you need to subtype this class and override the {@link #webSocketEndpointClass()} method.
 * In addition you must annotate your implementation with the {@link javax.servlet.annotation.WebListener} annotation!
 *
 * <pre>
 * {@code
 *  {@literal @}WebListener
 *  MyConfig extends AnkorWebSocketConfig {
 *      {@literal @}Override
 *      protected Class<{@literal ?} extends AnkorEndpoint> webSocketEndpointClass() {
 *          return MyEndpoint.class;
 *      }
 *  }
 * }
 * </pre>
 *
 * @see AnkorEndpoint
 * @author Florian Klampfer
 */
public abstract class AnkorWebSocketConfig implements ServletContextListener {

    /**
     * This method can be overriden to provide a custom url to the WebSocket.
     * Note that you need to specify this string on the client as well.
     *
     * @return a string that starts with '/'
     */
    protected String websocketUrl() {
        return "/websocket/ankor";
    }

    /**
     * This method must be overridden to provide the {@link Class} of your implementation of {@link AnkorEndpoint}
     * @return The {@link Class} of a subtype of {@link AnkorEndpoint}.
     */
    protected abstract Class<? extends AnkorEndpoint> webSocketEndpointClass();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServerContainer sc = (ServerContainer) sce.getServletContext().getAttribute("javax.websocket.server.ServerContainer");
        try {
            sc.addEndpoint(ServerEndpointConfig.Builder.create(webSocketEndpointClass(), websocketUrl()).build());
        } catch (DeploymentException e) {
            throw new  IllegalStateException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // NO-OP
    }

}
