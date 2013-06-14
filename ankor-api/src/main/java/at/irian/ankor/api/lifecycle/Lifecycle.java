package at.irian.ankor.api.lifecycle;

import at.irian.ankor.api.context.ServerContext;

/**
 */
public interface Lifecycle {

    void execute(ServerContext context);

}
