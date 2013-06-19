package at.irian.ankor.core.ref.el;

import at.irian.ankor.core.application.DefaultChangeNotifier;
import at.irian.ankor.core.application.DefaultActionNotifier;
import at.irian.ankor.core.el.ELSupport;
import at.irian.ankor.core.el.ModelHolderVariableMapper;
import at.irian.ankor.core.ref.Ref;
import at.irian.ankor.core.ref.RefContext;
import at.irian.ankor.core.ref.RefFactory;

import javax.el.ValueExpression;

import static at.irian.ankor.core.ref.el.PathUtils.*;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ELRefFactory implements RefFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RefFactory.class);

    private static final String ROOT_VAR_NAME = ModelHolderVariableMapper.ROOT_VAR_NAME;
    private static final String ROOT_PATH_PREFIX = ROOT_VAR_NAME + '.';

    private final ELRefContext defaultRefContext;

    public ELRefFactory(ELSupport elSupport,
                      DefaultChangeNotifier changeNotifier,
                      DefaultActionNotifier actionNotifier) {
        this.defaultRefContext = new ELRefContext(elSupport, elSupport.getBaseELContext(),
                                                  changeNotifier, actionNotifier);
    }

    @Override public Ref rootRef() {
        return ref(ROOT_VAR_NAME);
    }

    @Override
    public Ref rootRef(RefContext newRefContext) {
        return rootRef((ELRefContext)newRefContext);
    }

    @Override public Ref ref(String path) {
        return ref(path, defaultRefContext);
    }

    @Override
    public Ref ref(String path, RefContext newRefContext) {
        return ref(path, (ELRefContext) newRefContext);
    }


    static Ref rootRef(ELRefContext refContext) {
        return ref(ROOT_VAR_NAME, refContext);
    }

    static Ref ref(String path, ELRefContext refContext) {
        String prefixedPath = prefixPathWithModelBaseIfNecessary(path);
        ValueExpression ve;
        if (isRootPath(path)) {
            ve = refContext.getELSupport().createRootValueExpression(refContext.getELContext());
        } else {
            ve = refContext.getELSupport().createValueExpression(refContext.getELContext(), prefixedPath);
        }
        return new ELRef(refContext, prefixedPath, ve);
    }

    private static String prefixPathWithModelBaseIfNecessary(String path) {
        if (isRootPath(path)) {
            return ROOT_VAR_NAME;
        } else if (path.startsWith(ROOT_PATH_PREFIX)) {
            return path;
        } else {
            return ROOT_PATH_PREFIX + path;
        }
    }

    static boolean isRootPath(String path) {
        return path == null || path.isEmpty() || path.equals(ROOT_VAR_NAME);
    }

    static Ref parentRef(ELRef ref) {
        if (ref.isRoot()) {
            return null;
        } else {
            return ref(parentPath(ref.path()), ref.refContext());
        }
    }

    static Ref subRef(ELRef ref, String subPath) {
        return ref(concat(ref.path(), subPath), ref.refContext());
    }

}
