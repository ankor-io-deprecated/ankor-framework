package at.irian.ankorsamples.todosample.spring;

import at.irian.ankor.application.Application;
import at.irian.ankor.system.WebSocketServerEndpoint;
import at.irian.ankorsamples.todosample.application.TodoServerApplication;

// Note: Same as in todo-websocket-server
public class TodoWebSocketServerEndpoint extends WebSocketServerEndpoint {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TodoWebSocketServerEndpoint.class);

    @Override
    protected Application createApplication() {
        return new TodoServerApplication();
    }

    @Override
    public String getPath() {
        return "/websocket/ankor/{clientId}";
    }
}

