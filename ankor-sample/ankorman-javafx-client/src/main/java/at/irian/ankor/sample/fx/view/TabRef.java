package at.irian.ankor.sample.fx.view;

import at.irian.ankor.core.ref.ModelRef;

/**
 * @author Thomas Spiegl
 */
public class TabRef {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TabRef.class);

    private final ModelRef ref;

    public TabRef(ModelRef ref) {
        this.ref = ref;
    }

    public TabModelRef getModelRef() {
        return new TabModelRef(ref.sub("model"));
    }

    private class TabModelRef {
        private final ModelRef ref;

        private TabModelRef(ModelRef ref) {
            this.ref = ref;
        }
    }
}
