package at.irian.ankorsamples.todosample.server;

import at.irian.ankor.application.CollaborationSingleRootApplication;
import at.irian.ankor.ref.Ref;
import at.irian.ankorsamples.todosample.domain.TaskRepository;
import at.irian.ankorsamples.todosample.viewmodel.ModelRoot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Manfred Geiler
 */
public class TodoServerApplication extends CollaborationSingleRootApplication {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalsServerApplication.class);

    private static final String APPLICATION_NAME = "Todo Server";
    private static final String MODEL_NAME = "root";

    private final Map<String, Object> models = new ConcurrentHashMap<>();

    public TodoServerApplication() {
        super(APPLICATION_NAME, MODEL_NAME);
    }

    @Override
    public Object doCreateModel(Ref rootRef, Map<String, Object> connectParameters) {
        String taskListId = (String) connectParameters.get("todoListId");
        if (taskListId != null) {
            Object model = models.get(taskListId);
            if (model == null) {
                model = new ModelRoot(rootRef, new TaskRepository());
                synchronized (models) {
                    models.put(taskListId, model);
                }
            }
            return model;
        } else {
            return new ModelRoot(rootRef, new TaskRepository());
        }
    }

    @Override
    public Object lookupModel(Map<String, Object> connectParameters) {
        String taskListId = (String) connectParameters.get("todoListId");
        if (taskListId != null) {
            return models.get(taskListId);
        } else {
            return null;
        }
    }
}
