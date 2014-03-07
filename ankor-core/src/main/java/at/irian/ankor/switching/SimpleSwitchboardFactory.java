package at.irian.ankor.switching;

/**
 * @author Manfred Geiler
 */
public class SimpleSwitchboardFactory implements SwitchboardFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleSwitchboardFactory.class);

    @Override
    public Switchboard createSwitchboard() {
        return new SimpleSwitchboard();
    }
}
