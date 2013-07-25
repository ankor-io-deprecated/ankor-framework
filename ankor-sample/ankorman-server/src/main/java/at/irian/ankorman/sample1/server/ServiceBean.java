package at.irian.ankorman.sample1.server;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.ActionSourceRef;
import at.irian.ankor.ref.Ref;
import at.irian.ankorman.sample1.model.ModelRoot;

/**
 * @author Thomas Spiegl
 */
public class ServiceBean {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ServiceBean.class);

    private final AnimalRepository animalRepository;

    public ServiceBean() {
        animalRepository = new AnimalRepository();
    }

    @ActionListener(name = "init")
    public void init(@ActionSourceRef Ref rootRef) {
        ModelRoot modelRoot = new ModelRoot(rootRef, animalRepository);
        modelRoot.setUserName("John Doe");
        rootRef.setValue(modelRoot);
    }
}
