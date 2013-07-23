package at.irian.ankor.annotation;

import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.action.SimpleAction;
import at.irian.ankor.action.SimpleParamAction;
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

        private final Map<ActionFilter, ActionTarget> mappings;

        public ActionMapper(BeanResolver beanResolver) {
            mappings = new HashMap<ActionFilter, ActionTarget>();
            scanBeans(beanResolver);
        }

        private void scanBeans(BeanResolver beanResolver) {
            for (String beanName : beanResolver.getBeanDefinitionNames()) {
                Object bean = beanResolver.resolveByName(beanName);
                Class<?> beanType = bean.getClass();
                for (Method method : beanType.getMethods()) {
                    for (Annotation methodAnnotation : method.getDeclaredAnnotations()) {
                        if (methodAnnotation instanceof Action) {
                            Action action = (Action) methodAnnotation;
                            String actionPropertyType;
                            if (!action.refType().getName().equals(Action.class.getName()) &&
                                    !ObjectUtils.isEmpty(action.refType().getName())) {
                                actionPropertyType = action.refType().getName();
                            } else {
                                actionPropertyType = null;
                            }
                            // todo: support multiple equal action filters
                            mappings.put(new ActionFilter(action.name(), actionPropertyType), new ActionTarget(beanName, method));
                        }
                    }
                }
            }
        }

        public ActionTarget findTargetFor(String actionName, String actionPropertyType) {
            ActionTarget actionTarget = mappings.get(new ActionFilter(actionName, actionPropertyType));
            if (actionTarget == null) {
                // try to find action without actionPropertyType
                actionTarget = mappings.get(new ActionFilter(actionName, null));
            }
            return actionTarget;
        }
    }

    static final class ActionMethod {
        private final Method method;
        private List<Annotation> params;

        public ActionMethod(Method method) {
            this.method = method;
            int paramIdx = 0;
            initMethodParameter(method, paramIdx);
        }

        public void invoke(ActionEvent event, Object bean) {
            try {
                if (params != null) {
                    Object[] paramValues = new Object[params.size()];
                    for (int i = 0; i < params.size(); i++) {
                        Annotation annotation = params.get(i);

                        if (annotation instanceof ActionPropertyRef) {
                            Ref paramRef;
                            if (ObjectUtils.isEmpty(((ActionPropertyRef) annotation).value())) {
                                paramRef = event.getActionProperty();
                            } else {
                                paramRef = event.getActionProperty().append(((ActionPropertyRef) annotation).value());
                            }
                            paramValues[i] = paramRef;

                        } else if (annotation instanceof Param) {
                            String paramName = ((Param) annotation).value();
                            if (ObjectUtils.isEmpty(paramName)) {
                                throw new IllegalStateException(String.format("AnkorActionParam has no value %s", this));
                            } else {
                                if (event.getAction() instanceof SimpleParamAction) {
                                    paramValues[i] = ((SimpleParamAction) event.getAction()).getParams().get(paramName);
                                    if (paramValues[i] == null && !((Param) annotation).optional()) {
                                        throw new IllegalStateException(String.format("Parameter %s may not be null (optional=false) for method with @AnkorActionParam %s", paramName, this));
                                    }
                                } else {
                                    throw new IllegalStateException(String.format("Excpected SimpleParamAction for method with @AnkorActionParam %s", this));
                                }
                            }

                        } else {
                            throw new IllegalStateException("Illegal Annotation " + annotation.getClass());
                        }
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
                    params = new ArrayList<Annotation>();
                }
                boolean found = false;
                for (Annotation annotation : annotations) {
                    if (annotation instanceof ActionPropertyRef || annotation instanceof Param)  {
                        found = true;
                        params.add(annotation);
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

    static class ActionFilter {

        private final String actionName;
        private final String actionPropertyType;

        public ActionFilter(String actionName, String actionPropertyType) {
            this.actionName = actionName;
            this.actionPropertyType = actionPropertyType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ActionFilter that = (ActionFilter) o;

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
            method.invoke(event, bean);
        }
    }
}
