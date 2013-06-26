package at.irian.ankor.messaging;

import at.irian.ankor.path.PathSyntax;

/**
 * @author Manfred Geiler
 */
public class ReducingMessageSender extends BufferedMessageSender {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ReducingMessageSender.class);

    private final PathSyntax pathSyntax;

    public ReducingMessageSender(MessageSender original, PathSyntax pathSyntax) {
        super(original);
        this.pathSyntax = pathSyntax;
    }

    @Override
    public void flush() {
        for (Message message : getBufferedMessages()) {
            if (!isSkipMessage(message)) {
                getOriginalSender().sendMessage(message);
            }
        }
    }

    protected boolean isSkipMessage(Message message) {
        for (Message m : getBufferedMessages()) {
            if (m != message) {
                if (isIncludedIn(message, m)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isIncludedIn(Message skipCandidate, Message masterMessage) {
        if (skipCandidate.equals(masterMessage)) {
            return true;
        }

        if (skipCandidate instanceof ChangeMessage && masterMessage instanceof ChangeMessage) {
            String changedSkip = ((ChangeMessage) skipCandidate).getChange().getChangedProperty();
            String changedMaster = ((ChangeMessage) masterMessage).getChange().getChangedProperty();
            if (pathSyntax.isDescendant(changedSkip, changedMaster)) {
                // skip
                LOG.debug("skip because included in higher level message: {} is included in {}", skipCandidate, masterMessage);
                return true;
            }
        }

        return false;
    }

}
