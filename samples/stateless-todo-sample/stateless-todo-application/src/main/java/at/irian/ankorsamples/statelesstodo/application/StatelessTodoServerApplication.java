package at.irian.ankorsamples.statelesstodo.application;

import at.irian.ankor.application.Application;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;
import at.irian.ankorsamples.statelesstodo.domain.TaskRepository;
import at.irian.ankorsamples.statelesstodo.viewmodel.ModelRoot;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author Manfred Geiler
 */
public class StatelessTodoServerApplication implements Application {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StatelessTodoServerApplication.class);

    private static final String APPLICATION_NAME = "Stateless Todo Server";
    private static final String MODEL_NAME = "root";
    
    private static final TaskRepository taskRepository = new TaskRepository();

    @Override
    public String getName() {
        return APPLICATION_NAME;
    }

    @Override
    public boolean isStateless() {
        return true;
    }

    @Override
    public Set<String> getKnownModelNames() {
        return Collections.singleton(MODEL_NAME);
    }

    @Override
    public Object lookupModel(String modelName, Map<String, Object> connectParameters) {
        return null;
    }

    @Override
    public Object createModel(String modelName, Map<String, Object> connectParameters, RefContext refContext) {
        Ref rootRef = refContext.refFactory().ref(MODEL_NAME);
        return new ModelRoot(rootRef, taskRepository);
    }

    @Override
    public void releaseModel(String modelName, Object modelRoot) {

    }

    @Override
    public void shutdown() {

    }
}
