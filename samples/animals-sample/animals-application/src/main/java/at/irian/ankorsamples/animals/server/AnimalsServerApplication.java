package at.irian.ankorsamples.animals.server;

import at.irian.ankor.application.SimpleSingleRootApplication;
import at.irian.ankor.ref.Ref;
import at.irian.ankorsamples.animals.domain.AnimalRepository;
import at.irian.ankorsamples.animals.viewmodel.ModelRoot;

/**
 * @author Manfred Geiler
 */
public class AnimalsServerApplication extends SimpleSingleRootApplication<ModelRoot> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalsServerApplication.class);

    private static final String APPLICATION_NAME = "Animals Server";
    private static final String MODEL_NAME = "root";

    public AnimalsServerApplication() {
        super(APPLICATION_NAME, MODEL_NAME);
    }

    @Override
    protected ModelRoot createRoot(Ref rootRef) {
        AnimalRepository animalRepository = new AnimalRepository();
        return new ModelRoot(rootRef, animalRepository);
    }
}
