package at.irian.ankor.fx.binding;

/**
 * @author Thomas Spiegl
 */
public interface ClickAction<T> {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Action.class);

    void onClick(T value);
}
