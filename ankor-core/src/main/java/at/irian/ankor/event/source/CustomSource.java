package at.irian.ankor.event.source;

/**
 * @author Manfred Geiler
 */
public class CustomSource implements Source {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CustomSource.class);

    private final Object customSourceObject;

    public CustomSource(Object customSourceObject) {
        this.customSourceObject = customSourceObject;
    }

    public Object getCustomSourceObject() {
        return customSourceObject;
    }
}
