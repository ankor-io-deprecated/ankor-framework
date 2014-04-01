package at.irian.ankor.console;

import at.irian.ankor.application.Application;
import at.irian.ankor.monitor.stats.AnkorSystemStats;
import at.irian.ankor.ref.RefContext;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author Manfred Geiler
 */
public class ConsoleApplication implements Application {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ConsoleApplication.class);

    public static final String MODEL_NAME = "ankorConsole";

    private final AnkorSystemStats stats;
    private ConsoleModelRoot consoleModelRoot;

    public ConsoleApplication(AnkorSystemStats stats) {
        this.stats = stats;
    }

    @Override
    public String getName() {
        return "Ankor Console";
    }

    @Override
    public boolean supportsModel(String modelName) {
        return MODEL_NAME.equals(modelName);
    }

    @Override
    public Set<String> getKnownModelNames() {
        return Collections.singleton(MODEL_NAME);
    }

    @Override
    public Object lookupModel(String modelName, Map<String, Object> connectParameters) {
        if (MODEL_NAME.equals(modelName)) {
            return consoleModelRoot;
        } else {
            throw new IllegalArgumentException("Unknown model " + modelName);
        }
    }

    @Override
    public Object createModel(String modelName, Map<String, Object> connectParameters, RefContext refContext) {
        if (MODEL_NAME.equals(modelName)) {
            consoleModelRoot = new ConsoleModelRoot(refContext.refFactory().ref(MODEL_NAME), stats);
            return consoleModelRoot;
        } else {
            throw new IllegalArgumentException("Unknown model " + modelName);
        }
    }

    @Override
    public void releaseModel(String modelName, Object modelRoot) {
        if (MODEL_NAME.equals(modelName)) {
            if (modelRoot == consoleModelRoot) {
                consoleModelRoot = null;
            }
        } else {
            throw new IllegalArgumentException("Unknown model " + modelName);
        }
    }

    @Override
    public void shutdown() {
        consoleModelRoot = null;
    }
}
