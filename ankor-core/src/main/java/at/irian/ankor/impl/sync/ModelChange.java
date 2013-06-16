package at.irian.ankor.impl.sync;

import at.irian.ankor.impl.ref.ModelPropertyRef;

/**
 * @author Manfred Geiler
 */
public class ModelChange {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelChange.class);

    private final ModelPropertyRef ref;
    private final Object newValue;

    public ModelChange(ModelPropertyRef ref, Object newValue) {
        this.ref = ref;
        this.newValue = newValue;
    }

    public ModelPropertyRef getRef() {
        return ref;
    }

    public Object getNewValue() {
        return newValue;
    }
}


