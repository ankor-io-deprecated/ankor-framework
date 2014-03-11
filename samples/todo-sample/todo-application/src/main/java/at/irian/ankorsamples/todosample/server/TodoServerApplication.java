package at.irian.ankorsamples.todosample.server;

import at.irian.ankor.application.SimpleSingleRootApplication;
import at.irian.ankor.ref.Ref;
import at.irian.ankorsamples.todosample.domain.TaskRepository;
import at.irian.ankorsamples.todosample.viewmodel.ModelRoot;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class TodoServerApplication extends SimpleSingleRootApplication {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalsServerApplication.class);

    private static final String APPLICATION_NAME = "Todo Server";
    private static final String MODEL_NAME = "root";

    public TodoServerApplication() {
        super(APPLICATION_NAME, MODEL_NAME);
    }

    @Override
    public Object createModel(Ref rootRef, Map<String, Object> connectParameters) {
        TaskRepository animalRepository = new TaskRepository();
        return new ModelRoot(rootRef, animalRepository);
    }

}
