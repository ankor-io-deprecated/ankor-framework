package at.irian.ankor.messaging.json;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.SimpleAction;
import at.irian.ankor.messaging.*;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Manfred Geiler
 */
public class JsonViewModelMessageMapperTest {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JsonViewModelMessageMapperTest.class);

    private MessageFactory messageFactory;
    private JsonViewModelMessageMapper msgMapper;

    @org.junit.Before
    public void setUp() throws Exception {
        messageFactory = new MessageFactory(new CounterMessageIdGenerator());
        msgMapper = new JsonViewModelMessageMapper();
    }

    @Test
    public void testSimpleAction() throws Exception {
        Action action = new SimpleAction("test");
        Message msg = messageFactory.createActionMessage("sid", "context.model", action);
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
        Message msg = messageFactory.createChangeMessage("sid", "root.test1", "new-value");
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
        Message msg = messageFactory.createChangeMessage("sid", "root", "new-value");
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
