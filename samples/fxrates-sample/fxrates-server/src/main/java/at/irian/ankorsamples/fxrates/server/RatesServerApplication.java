package at.irian.ankorsamples.fxrates.server;

import at.irian.ankor.application.SimpleSingleRootApplication;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;

/**
 * @author Manfred Geiler
 */
public class RatesServerApplication extends SimpleSingleRootApplication<RatesViewModel> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RatesServerApplication.class);

    private static final String APPLICATION_NAME = "Rates Server";
    private static final String MODEL_NAME = "root";

    public RatesServerApplication() {
        super(APPLICATION_NAME, MODEL_NAME);
    }

    @Override
    protected RatesViewModel createRoot(Ref rootRef) {
        RatesRepository animalRepository = new RatesRepository();
        return new RatesViewModel(rootRef, animalRepository);
    }

    @Override
    protected void afterInitInstance(RefContext refContext, RatesViewModel root) {
        root.startRatesUpdate(2);
        LOG.info("Rates update started");
    }

    @Override
    protected void beforeReleaseInstance(RefContext refContext, RatesViewModel root) {
        root.stopRatesUpdate();
        LOG.info("Rates update stopped");
    }
}
