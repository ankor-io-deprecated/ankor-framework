package at.irian.ankor.ref.el;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.el.ELSupport;
import at.irian.ankor.el.StandardELContext;
import at.irian.ankor.event.EventDelaySupport;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.model.ViewModelPostProcessor;
import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.path.el.ELPathSyntax;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.ref.impl.RefContextImplementor;
import com.typesafe.config.Config;

import javax.el.ExpressionFactory;
import java.util.List;

/**
 * @author Manfred Geiler
 */
public class ELRefContext implements RefContext, RefContextImplementor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELRefContext.class);

    private final ExpressionFactory expressionFactory;
    private final StandardELContext elContext;
    private final String modelRootVarName;
    private final ModelContext modelContext;
    private final EventDelaySupport eventDelaySupport;
    private final List<ViewModelPostProcessor> viewModelPostProcessors;

    ELRefContext(ELSupport elSupport,
                 Config config,
                 ModelContext modelContext,
                 EventDelaySupport eventDelaySupport,
                 List<ViewModelPostProcessor> viewModelPostProcessors) {
        this.viewModelPostProcessors = viewModelPostProcessors;
        this.expressionFactory = elSupport.getExpressionFactory();
        this.elContext = elSupport.getELContextFor(refFactory());
        this.modelContext = modelContext;
        this.eventDelaySupport = eventDelaySupport;
        this.modelRootVarName = config.getString("ankor.variable-names.modelRoot");
    }

    @Override
    public RefFactory refFactory() {
        return new ELRefFactory(this);
    }

    ExpressionFactory getExpressionFactory() {
        return expressionFactory;
    }

    public StandardELContext getElContext() {
        return elContext;
    }

    @Override
    public EventListeners modelEventListeners() {
        return modelContext.getModelEventListeners();
    }

    @Override
    public Iterable<ModelEventListener> allEventListeners() {
//        return new Iterable<ModelEventListener>() {
//            @Override
//            public Iterator<ModelEventListener> iterator() {
//
//                final Iterator<ModelEventListener> globalIterator = globalEventListeners().iterator();
//                final Iterator<ModelEventListener> modelIterator = modelEventListeners().iterator();
//
//                return new Iterator<ModelEventListener>() {
//
//                    private int internalState = 0;
//
//                    @Override
//                    public boolean hasNext() {
//                        return globalIterator.hasNext() || modelIterator.hasNext();
//                    }
//
//                    @Override
//                    public ModelEventListener next() {
//                        if (globalIterator.hasNext()) {
//                            internalState = 1;
//                            return globalIterator.next();
//                        } else {
//                            internalState = 2;
//                            return modelIterator.next();
//                        }
//                    }
//
//                    @Override
//                    public void remove() {
//                        if (internalState == 1) {
//                            internalState = 0;
//                            globalIterator.remove();
//                        } else if (internalState == 2) {
//                            internalState = 0;
//                            modelIterator.remove();
//                        } else {
//                            throw new IllegalStateException();
//                        }
//                    }
//                };
//            }
//        };
        return modelEventListeners();
    }

    @Override
    public EventListeners eventListeners() {
        return modelEventListeners();
    }

    String getModelRootVarName() {
        return modelRootVarName;
    }

    @Override
    public PathSyntax pathSyntax() {
        return ELPathSyntax.getInstance();
    }

    public EventDelaySupport eventDelaySupport() {
        return eventDelaySupport;
    }

    @Override
    public List<ViewModelPostProcessor> viewModelPostProcessors() {
        return viewModelPostProcessors;
    }
}
