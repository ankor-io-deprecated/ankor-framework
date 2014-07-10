package at.irian.ankorsamples.statelesstodo.application;

import at.irian.ankor.application.SimpleSingleRootApplication;
import at.irian.ankor.ref.Ref;
import at.irian.ankorsamples.statelesstodo.domain.TaskRepository;
import at.irian.ankorsamples.statelesstodo.viewmodel.ModelRoot;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class StatelessTodoServerApplication extends SimpleSingleRootApplication {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StatelessTodoServerApplication.class);

    private static final String APPLICATION_NAME = "Stateless Todo Server";
    private static final String MODEL_NAME = "root";
    
    private static final TaskRepository taskRepository = new TaskRepository();

    public StatelessTodoServerApplication() {
        super(APPLICATION_NAME, MODEL_NAME);
    }

    @Override
    public Object createModel(Ref rootRef, Map<String, Object> connectParameters) {
        return new ModelRoot(rootRef, taskRepository);
    }

    @Override
    public boolean isStateless() {
        return true;
    }
}
