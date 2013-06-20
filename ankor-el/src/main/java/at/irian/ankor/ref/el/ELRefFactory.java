package at.irian.ankor.ref.el;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefFactory;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ELRefFactory implements RefFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RefFactory.class);

    private final ELRefContext refContext;


    public ELRefFactory(ELRefContext refContext) {
        this.refContext = refContext;
    }

    @Override
    public Ref rootRef() {
        return ELRefUtils.rootRef(refContext);
    }

    @Override
    public Ref rootRef(RefContext newRefContext) {
        return ELRefUtils.rootRef((ELRefContext) newRefContext);
    }

    @Override
    public Ref ref(String path) {
        return ELRefUtils.ref(refContext, path);
    }

    @Override
    public Ref ref(String path, RefContext newRefContext) {
        return ELRefUtils.ref((ELRefContext) newRefContext, path);
    }

}
