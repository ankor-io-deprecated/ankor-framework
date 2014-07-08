package at.irian.ankorsamples.animals.application;

import at.irian.ankor.application.CollaborationSingleRootApplication;
import at.irian.ankor.ref.Ref;
import at.irian.ankorsamples.animals.domain.AnimalRepository;
import at.irian.ankorsamples.animals.viewmodel.ModelRoot;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class AnimalsServerApplication extends CollaborationSingleRootApplication {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalsServerApplication.class);

    private static final String APPLICATION_NAME = "Animals Server";
    private static final String MODEL_NAME = "root";

    public AnimalsServerApplication() {
        super(APPLICATION_NAME, MODEL_NAME);
    }

    @Override
    public Object doCreateModel(Ref rootRef, Map<String, Object> connectParameters) {
        AnimalRepository animalRepository = new AnimalRepository();
        return new ModelRoot(rootRef, animalRepository);
    }
}
