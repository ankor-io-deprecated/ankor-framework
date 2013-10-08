package at.irian.ankor.ref.impl;

import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class InvalidRefException extends Exception {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(InvalidRefException.class);

    public InvalidRefException(Ref ref) {
        super("Ref:" + ref.path());
    }

}
