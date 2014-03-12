package at.irian.ankor.worker;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class WorkerContext {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(WorkerContext.class);

    private static final ThreadLocal<WorkerContext> INSTANCE = new ThreadLocal<WorkerContext>();

    public static WorkerContext getCurrentInstance() {
        WorkerContext workerContext = INSTANCE.get();
        if (workerContext == null) {
            throw new IllegalStateException("No WorkerContext");
        }
        return workerContext;
    }

    public static void setCurrentInstance(WorkerContext workerContext) {
        INSTANCE.set(workerContext);
    }

    private final Map<String, Object> attributes = new HashMap<String, Object>();

    public Map<String, Object> getAttributes() {
        return attributes;
    }

}
