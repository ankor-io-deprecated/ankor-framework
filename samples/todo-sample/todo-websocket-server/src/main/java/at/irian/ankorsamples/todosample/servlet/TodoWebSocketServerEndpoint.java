package at.irian.ankorsamples.todosample.servlet;

import at.irian.ankor.application.Application;
import at.irian.ankor.system.WebSocketServerEndpoint;
import at.irian.ankorsamples.todosample.server.TodoServerApplication;

/**
 * @author Manfred Geiler
 */
public class TodoWebSocketServerEndpoint extends WebSocketServerEndpoint {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TodoWebSocketServerEndpoint.class);

    @Override
    protected Application createApplication() {
        return new TodoServerApplication();
    }

    @Override
    protected String getPath() {
        return "/websocket/ankor/{clientId}";
    }
}
