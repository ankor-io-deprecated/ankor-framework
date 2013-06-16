package at.irian.ankor.impl.ref;

/**
 * @author Manfred Geiler
 */
public class ModelPropertyRef {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelPropertyRef.class);

    private final String valueExpression;

    public ModelPropertyRef(String valueExpression) {
        this.valueExpression = valueExpression;
    }

    public String getValueExpression() {
        return valueExpression;
    }
}
