package at.irian.ankorsamples.fxrates.server;

import at.irian.ankor.application.SimpleSingleRootApplication;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class RatesServerApplication extends SimpleSingleRootApplication {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RatesServerApplication.class);

    private static final String APPLICATION_NAME = "Rates Server";
    private static final String MODEL_NAME = "root";

    public RatesServerApplication() {
        super(APPLICATION_NAME, MODEL_NAME);
    }

    @Override
    public Object createModel(Ref rootRef) {
        RatesRepository animalRepository = new RatesRepository();
        RatesViewModel root = new RatesViewModel(rootRef, animalRepository);
        root.startRatesUpdate(2);
        LOG.info("Rates update started");
        return root;
    }

    @Override
    public void releaseModel(Object model) {
        RatesViewModel root = (RatesViewModel) model;
        root.stopRatesUpdate();
        LOG.info("Rates update stopped");
    }

}
