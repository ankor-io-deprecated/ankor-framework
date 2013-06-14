package at.irian.ankorman;

import at.irian.ankor.impl.application.AnkorService;
import at.irian.ankor.impl.msgbus.MockClientMessage;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class AnkormanTest {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkormanTest.class);


    public void test() throws Exception {

        AnkorService ankorService = new AnkorService();

        ankorService.messageReceived(new MockClientMessage())


    }

}
