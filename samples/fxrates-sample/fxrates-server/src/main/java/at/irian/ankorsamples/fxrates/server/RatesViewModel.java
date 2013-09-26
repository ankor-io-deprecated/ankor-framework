package at.irian.ankorsamples.fxrates.server;

import at.irian.ankor.messaging.AnkorIgnore;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelListProperty;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Thomas Spiegl
 */
public class RatesViewModel {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RatesViewModel.class);

    @AnkorIgnore
    private final RatesRepository repository;

    private ViewModelListProperty<List<Rate>> rates;

    public RatesViewModel(Ref viewModelRef, RatesRepository repository) {
        this.repository = repository;
        this.rates = new ViewModelListProperty<>(viewModelRef, "rates");
        schedulePeriodicRatesUpdate(4);
    }

    private void schedulePeriodicRatesUpdate(long seconds) {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    // update rates
                    AnkorPatterns.changeValueLater(rates.getRef(), repository.getRates());
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }, 0, seconds, TimeUnit.SECONDS);
    }

    public ViewModelListProperty<List<Rate>> getRates() {
        return rates;
    }
}
