package at.irian.ankorsamples.statelesstodo.server;

import at.irian.ankor.application.CollaborationSingleRootApplication;
import at.irian.ankor.ref.Ref;
import at.irian.ankorsamples.statelesstodo.domain.TaskRepository;
import at.irian.ankorsamples.statelesstodo.viewmodel.ModelRoot;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
// TODO: Make non collaborative
public class StatelessTodoServerApplication extends CollaborationSingleRootApplication {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalsServerApplication.class);

    private static final String APPLICATION_NAME = "Stateless Todo Server";
    private static final String MODEL_NAME = "root";

    public StatelessTodoServerApplication() {
        super(APPLICATION_NAME, MODEL_NAME);
    }

    @Override
    public Object doCreateModel(Ref rootRef, Map<String, Object> connectParameters) {
        return new ModelRoot(rootRef, new TaskRepository());
    }

    @Override
    public Object lookupModel(Map<String, Object> connectParameters) {
        return null;
    }

    @Override
    public boolean isStateless() {
        return true;
    }
}
