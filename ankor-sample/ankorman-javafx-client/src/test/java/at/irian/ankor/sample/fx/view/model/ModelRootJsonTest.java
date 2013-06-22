package at.irian.ankor.sample.fx.view.model;

import at.irian.ankor.application.SimpleApplication;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.messaging.json.JsonMessageMapper;
import at.irian.ankor.ref.RefFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Manfred Geiler
 */
public class ModelRootJsonTest {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelRootJsonTest.class);

    private JsonMessageMapper mapper;
    private MessageFactory messageFactory;
    private RefFactory rf;

    @Before
    public void setUp() throws Exception {
        SimpleApplication application = SimpleApplication.create(ModelRoot.class);
        messageFactory = new MessageFactory();
        rf = application.getRefFactory();
        mapper = new JsonMessageMapper(rf);
    }

    @Test
    public void test() throws Exception {
        ModelRoot root = new ModelRoot();
        root.setUserName("Max Muster");
        String json = mapper.serialize(messageFactory.createChangeMessage(rf.rootRef(), rf.ref("root"), root));
        LOG.info(json);
    }
}
