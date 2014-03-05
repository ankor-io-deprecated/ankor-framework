package at.irian.ankor.application;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author Manfred Geiler
 */
public abstract class SimpleSingleRootApplication extends BaseApplication {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleSingleRootApplication.class);

    private static final String DEFAULT_ROOT_VAR_NAME = "root";

    private final String modelName;

    public SimpleSingleRootApplication(String applicationName) {
        this(applicationName, DEFAULT_ROOT_VAR_NAME);
    }

    public SimpleSingleRootApplication(String applicationName, String modelName) {
        super(applicationName);
        this.modelName = modelName;
    }

    public abstract Object createRoot(Ref rootRef);

    @Override
    public ApplicationInstance getApplicationInstance(Map<String, Object> connectParameters) {
        return new ApplicationInstance() {

            private RefContext refContext;
            private Object root;

            @Override
            public void init(RefContext refContext) {
                this.refContext = refContext;
                Ref rootRef = refContext.refFactory().ref(modelName);
                this.root = createRoot(rootRef);
            }

            @Override
            public Set<String> getKnownRootNames() {
                return Collections.singleton(modelName);
            }

            @Override
            public Object getModelRoot(String rootVarName) {
                return this.root;
            }

            @Override
            public void setModelRoot(String rootVarName, Object bean) {
                throw new UnsupportedOperationException("SimpleSingleRootApplication does not support custom root beans");
            }

            @Override
            public void release() {
                this.root = null;
            }
        };
    }
}
