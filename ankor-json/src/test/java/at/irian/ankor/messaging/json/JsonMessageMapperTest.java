package at.irian.ankor.messaging.json;

import at.irian.ankor.action.SimpleAction;
import at.irian.ankor.application.ModelHolder;
import at.irian.ankor.messaging.ActionMessage;
import at.irian.ankor.messaging.ChangeMessage;
import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.MessageIdFactory;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.ref.el.ELRefContext;
import at.irian.ankor.ref.el.ELRefFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Manfred Geiler
 */
public class JsonMessageMapperTest {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JsonMessageMapperTest.class);

    private RefFactory refFactory;
    private JsonMessageMapper msgMapper;
    private MessageIdFactory messageIdFactory;

    @org.junit.Before
    public void setUp() throws Exception {
        Config config = ConfigFactory.load();
        ELRefContext refContext = ELRefContext.create(new ModelHolder(Object.class), null, null,
                                                      config);
        refFactory = new ELRefFactory(refContext);
        msgMapper = new JsonMessageMapper();
        messageIdFactory = new MessageIdFactory();
    }

    @Test
    public void testSimpleAction() throws Exception {
        SimpleAction action = SimpleAction.create("test");
        ActionMessage msg = new ActionMessage(messageIdFactory.createId(), action);
        String json = msgMapper.serialize(msg);
        LOG.info("JSON: {}", json);

        Message desMsg = msgMapper.deserialize(json);
        LOG.info("Message: {}", desMsg);

        Assert.assertEquals(ActionMessage.class, desMsg.getClass());
        ActionMessage actionMsg = (ActionMessage) desMsg;
        Assert.assertEquals(SimpleAction.class, actionMsg.getAction().getClass());
        SimpleAction simpleAction = (SimpleAction) actionMsg.getAction();
        Assert.assertEquals("test", simpleAction.name());
    }

    @Test
    public void testChange() throws Exception {
        ChangeMessage msg = new ChangeMessage(messageIdFactory.createId(),
                                              refFactory.rootRef(),
                                              refFactory.ref("root.test1"),
                                              "new-value");
        String json = msgMapper.serialize(msg);
        LOG.info("JSON: {}", json);

        Message desMsg = msgMapper.deserialize(json);
        LOG.info("Message: {}", desMsg);

        Assert.assertEquals(ChangeMessage.class, desMsg.getClass());
        ChangeMessage changeMsg = (ChangeMessage) desMsg;
        ChangeMessage.Change change = changeMsg.getChange();
        Assert.assertEquals("root.test1", change.getChangedProperty().path());
    }

}
