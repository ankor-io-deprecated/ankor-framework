package at.irian.ankor.annotation;

import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.action.SimpleAction;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.system.BeanResolver;
import at.irian.ankor.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Thomas Spiegl
 */
public class BeanAnnotationActionEventListener extends ActionEvent.Listener {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BeanAnnotationActionEventListener.class);

    private final BeanResolver beanResolver;
    private final ActionMapper actionMapper;

    public BeanAnnotationActionEventListener(BeanResolver beanResolver) {
        super(null); // always global
        this.beanResolver = beanResolver;
        this.actionMapper = new ActionMapper(beanResolver);
    }

    @Override
    public void process(ActionEvent event) {
        if (event.getAction() instanceof SimpleAction) {
            Object value = event.getActionProperty().getValue();
            String actionPropertyType;
            if (value != null) {
                actionPropertyType = value.getClass().getName();
            } else {
                actionPropertyType = null;
            }
            ActionTarget actionTarget = actionMapper.findTargetFor(((SimpleAction) event.getAction()).getName(), actionPropertyType);
            if (actionTarget != null) {
                actionTarget.invoke(event, beanResolver);
            } else {
                LOG.warn("No AnkorAction listener found for action {} propertyType {} ", ((SimpleAction) event.getAction()).getName(), actionPropertyType);
            }
        }
    }

    static class ActionMapper {

        private final Map<ActionSource, ActionTarget> mappings;

        public ActionMapper(BeanResolver beanResolver) {
            mappings = new HashMap<ActionSource, ActionTarget>();
            for (String beanName : beanResolver.getBeanDefinitionNames()) {
                Object bean = beanResolver.resolveByName(beanName);
                Class<?> beanType = bean.getClass();
                for (Method method : beanType.getMethods()) {
                    for (Annotation methodAnnotation : method.getDeclaredAnnotations()) {
                        if (methodAnnotation instanceof AnkorAction) {
                            AnkorAction action = (AnkorAction) methodAnnotation;
                            mappings.put(new ActionSource(action.name(), action.refType().getName()), new ActionTarget(beanName, method));
                        }
                    }
                }
            }
        }

        public ActionTarget findTargetFor(String actionName, String actionPropertyType) {
            return mappings.get(new ActionSource(actionName, actionPropertyType));
        }
    }

    static final class ActionMethod {
        private final Method method;
        private List<AnkorActionPropertyRef> params;

        public ActionMethod(Method method) {
            this.method = method;
            int paramIdx = 0;
            initMethodParameter(method, paramIdx);
        }

        public void invokeOn(ActionEvent event, Object bean) {
            try {
                if (params != null) {
                    Object[] paramValues = new Object[params.size()];
                    for (int i = 0; i < params.size(); i++) {
                        Ref paramRef;
                        if (ObjectUtils.isEmpty(params.get(i).value())) {
                            paramRef = event.getActionProperty();
                        } else {
                            paramRef = event.getActionProperty().append(params.get(i).value());
                        }
                        paramValues[i] = paramRef;
                    }
                    method.invoke(bean, paramValues);
                } else {
                    method.invoke(bean);
                }
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(
                        String.format("IllegalAccessException invoking action method %s", this), e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(
                        String.format("InvocationTargetException invoking action method %s", this), e);
            }

        }

        private void initMethodParameter(Method method, int paramIdx) {
            for (Annotation[] annotations : method.getParameterAnnotations()) {
                if (paramIdx == 0) {
                    params = new ArrayList<AnkorActionPropertyRef>();
                }
                boolean found = false;
                for (Annotation annotation : annotations) {
                    if (annotation instanceof AnkorActionPropertyRef) {
                        found = true;
                        params.add((AnkorActionPropertyRef) annotation);
                        break;
                    }
                }
                if (!found) {
                    throw new IllegalStateException(
                            String.format("Illegal Action Method Signature %s, found parameter without ankor annotation", this));
                }
                paramIdx++;
            }
        }

        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append(getClass().getSimpleName());
            buf.append("[method=");
            buf.append(method.getDeclaringClass().getName())
                    .append(".")
                    .append(method.getName());
            buf.append("(");
            boolean first = true;
            for (Class<?> paramType : method.getParameterTypes()) {
                if (first) {
                    first = false;
                } else {
                    buf.append(", ");
                }
                buf.append(paramType.getName());
            }
            buf.append(")]");
            return buf.toString();
        }
    }

    static class ActionSource {

        private final String actionName;
        private final String actionPropertyType;

        public ActionSource(String actionName, String actionPropertyType) {
            this.actionName = actionName;
            this.actionPropertyType = actionPropertyType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ActionSource that = (ActionSource) o;

            if (actionName != null ? !actionName.equals(that.actionName) : that.actionName != null) return false;
            //noinspection RedundantIfStatement
            if (actionPropertyType != null ? !actionPropertyType.equals(that.actionPropertyType) : that.actionPropertyType != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = actionName != null ? actionName.hashCode() : 0;
            result = 31 * result + (actionPropertyType != null ? actionPropertyType.hashCode() : 0);
            return result;
        }
    }

    static class ActionTarget {
        private final String beanName;
        private final ActionMethod method;

        public ActionTarget(String beanName, Method method) {
            this.beanName = beanName;
            this.method = new ActionMethod(method);
        }

        public void invoke(ActionEvent event, BeanResolver beanResolver) {
            Object bean = beanResolver.resolveByName(beanName);
            if (bean == null) {
                throw new IllegalStateException(String.format("Action may not be invoked, bean '%s' resolved to null", beanName));
            }
            method.invokeOn(event, bean);
        }
    }
}
