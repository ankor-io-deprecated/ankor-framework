package at.irian.ankor.annotation;

import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.match.RefMatcher;

import java.lang.reflect.Method;

/**
 * @author Manfred Geiler
 */
public class AnnotationChangeEventListener extends ChangeEventListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnnotationChangeEventListener.class);

    private final Object bean;
    private final RefMatcher[] matchers;
    private final Method method;
    private final Lifeline lifeline;

    public AnnotationChangeEventListener(Ref watchedProperty,
                                         Object bean,
                                         RefMatcher[] matchers,
                                         Method method,
                                         Lifeline lifeline) {
        super(watchedProperty);
        this.bean = bean;
        this.matchers = matchers;
        this.method = method;
        this.lifeline = lifeline;
    }

    @Override
    public void process(ChangeEvent event) {
        Ref changedProperty = event.getChangedProperty();

        RefMatcher.Result match = match(changedProperty, getWatchedProperty(), matchers);
        if (match != null) {
            try {
                method.invoke(bean, match.getBackRefs().toArray());
            } catch (Exception e) {
                throw new RuntimeException(String.format("Error invoking change listener method %s on view model object %s",
                                                         method,
                                                         bean), e);
            }
        }
    }

    @Override
    public boolean isDiscardable() {
        return !lifeline.isAlive();
    }


    private RefMatcher.Result match(Ref propertyToMatch,
                                    Ref contextProperty,
                                    RefMatcher[] matchers) {
        for (RefMatcher matcher : matchers) {
            RefMatcher.Result matchResult = matcher.match(propertyToMatch, contextProperty);
            if (matchResult.isMatch()) {
                return matchResult;
            }
        }
        return null;
    }

}
