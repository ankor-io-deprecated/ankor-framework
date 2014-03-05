package at.irian.ankor.event.source;

/**
 * @author Manfred Geiler
 */
public class CustomSource implements Source {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CustomSource.class);

    private final Object origination;

    public CustomSource(Object origination) {
        this.origination = origination;
    }

    public Object getOrigination() {
        return origination;
    }

    @Override
    public String toString() {
        return "CustomSource{" +
               "origination=" + origination +
               '}';
    }
}
