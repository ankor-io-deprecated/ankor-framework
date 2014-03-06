package at.irian.ankor.application;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author Manfred Geiler
 */
public abstract class SimpleSingleRootApplication<M> extends BaseApplication {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleSingleRootApplication.class);

    private final String modelName;

    public SimpleSingleRootApplication(String applicationName, String modelName) {
        super(applicationName);
        this.modelName = modelName;
    }

    protected abstract M createRoot(Ref rootRef);

    protected void beforeInitInstance(RefContext refContext) {}

    protected void afterInitInstance(RefContext refContext, M root) {}

    protected void beforeReleaseInstance(RefContext refContext, M root) {}

    protected void afterReleaseInstance(RefContext refContext) {}

    @Override
    public ApplicationInstance getApplicationInstance(Map<String, Object> connectParameters) {
        return new ApplicationInstance() {

            private RefContext refContext;
            private M root;

            @Override
            public void init(RefContext refContext) {
                beforeInitInstance(refContext);
                this.refContext = refContext;
                Ref rootRef = refContext.refFactory().ref(modelName);
                this.root = createRoot(rootRef);
                afterInitInstance(refContext, root);
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
                beforeReleaseInstance(refContext, root);
                this.root = null;
                afterReleaseInstance(refContext);
            }
        };
    }
}
