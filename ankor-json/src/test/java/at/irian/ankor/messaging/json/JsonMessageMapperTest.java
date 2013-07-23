package at.irian.ankor.messaging.json;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.SimpleAction;
import at.irian.ankor.messaging.ActionMessage;
import at.irian.ankor.messaging.ChangeMessage;
import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.MessageFactory;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Manfred Geiler
 */
public class JsonMessageMapperTest {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JsonMessageMapperTest.class);

    private MessageFactory messageFactory;
    private JsonMessageMapper msgMapper;

    @org.junit.Before
    public void setUp() throws Exception {
        messageFactory = new MessageFactory();
        msgMapper = new JsonMessageMapper();
    }

    @Test
    public void testSimpleAction() throws Exception {
        Action action = new SimpleAction("test");
        Message msg = messageFactory.createActionMessage("root.tabs.A1.tab", "context.model", action);
        String json = msgMapper.serialize(msg);
        LOG.info("JSON: {}", json);

        Message desMsg = msgMapper.deserialize(json);
        LOG.info("Message: {}", desMsg);

        Assert.assertEquals(ActionMessage.class, desMsg.getClass());
        ActionMessage actionMsg = (ActionMessage) desMsg;
        Assert.assertEquals(SimpleAction.class, actionMsg.getAction().getClass());
        SimpleAction simpleAction = (SimpleAction) actionMsg.getAction();
        Assert.assertEquals("test", simpleAction.getName());
    }

    @Test
    public void testChange() throws Exception {
        Message msg = messageFactory.createChangeMessage("roor", "root.test1", "new-value");
        String json = msgMapper.serialize(msg);
        LOG.info("JSON: {}", json);

        Message desMsg = msgMapper.deserialize(json);
        LOG.info("Message: {}", desMsg);

        Assert.assertEquals(ChangeMessage.class, desMsg.getClass());
        ChangeMessage changeMsg = (ChangeMessage) desMsg;
        ChangeMessage.Change change = changeMsg.getChange();
        Assert.assertEquals("root.test1", change.getChangedProperty());
    }

    @Test
    public void testChangeRoot() throws Exception {
        Message msg = messageFactory.createChangeMessage("root", "root", "new-value");
        String json = msgMapper.serialize(msg);
        LOG.info("JSON: {}", json);

        Message desMsg = msgMapper.deserialize(json);
        LOG.info("Message: {}", desMsg);

        Assert.assertEquals(ChangeMessage.class, desMsg.getClass());
        ChangeMessage changeMsg = (ChangeMessage) desMsg;
        ChangeMessage.Change change = changeMsg.getChange();
        Assert.assertEquals("root", change.getChangedProperty());
    }


}
