package at.irian.ankor.context;

/**
 * @author Manfred Geiler
 */
public class ModelContextFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelContextFactory.class);

    private int cnt = 0;

    public ModelContext createModelContext() {
        return new DefaultModelContext("" + (++cnt));
    }

}
