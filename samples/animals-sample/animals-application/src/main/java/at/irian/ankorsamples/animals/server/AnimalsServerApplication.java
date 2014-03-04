package at.irian.ankorsamples.animals.server;

import at.irian.ankor.application.SimpleSingleRootApplication;
import at.irian.ankor.ref.Ref;
import at.irian.ankorsamples.animals.domain.animal.AnimalRepository;
import at.irian.ankorsamples.animals.viewmodel.ModelRoot;

/**
 * @author Manfred Geiler
 */
public class AnimalsServerApplication extends SimpleSingleRootApplication {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalsServerApplication.class);

    private static final String APPLICATION_NAME = "Animals Server";

    public AnimalsServerApplication() {
        super(APPLICATION_NAME);
    }

    @Override
    public Object createRoot(Ref rootRef) {
        AnimalRepository animalRepository = new AnimalRepository();
        return new ModelRoot(rootRef, animalRepository);
    }
}
