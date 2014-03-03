package at.irian.ankor.msg.party;

/**
 * @author Manfred Geiler
 */
public final class SystemParty implements Party {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SystemParty.class);

    private static final Party INSTANCE = new SystemParty();

    public static Party getInstance() {
        return INSTANCE;
    }

    private SystemParty() {}

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
