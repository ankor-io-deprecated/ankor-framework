package at.irian.ankor.core.ref.el;

import at.irian.ankor.core.application.DefaultActionNotifier;
import at.irian.ankor.core.application.DefaultChangeNotifier;
import at.irian.ankor.core.el.ELSupport;
import at.irian.ankor.core.ref.RefContext;

import javax.el.ELContext;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ELRefContext implements RefContext {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELRefContext.class);

    private final ELSupport elSupport;
    private final ELContext elContext;
    private final DefaultChangeNotifier changeNotifier;
    private final DefaultActionNotifier actionNotifier;

    public ELRefContext(ELSupport elSupport,
                        ELContext elContext,
                        DefaultChangeNotifier changeNotifier,
                        DefaultActionNotifier actionNotifier) {
        this.elSupport = elSupport;
        this.elContext = elContext;
        this.changeNotifier = changeNotifier;
        this.actionNotifier = actionNotifier;
    }

    public ELRefContext with(ELContext elContext) {
        return new ELRefContext(elSupport, elContext, changeNotifier, actionNotifier);
    }

    public ELRefContext withNoModelChangeNotifier() {
        return new ELRefContext(elSupport, elContext, null, actionNotifier);
    }

    public ELSupport getELSupport() {
        return elSupport;
    }

    public ELContext getELContext() {
        return elContext;
    }

    public DefaultChangeNotifier getChangeNotifier() {
        return changeNotifier;
    }

    public DefaultActionNotifier getActionNotifier() {
        return actionNotifier;
    }

}
