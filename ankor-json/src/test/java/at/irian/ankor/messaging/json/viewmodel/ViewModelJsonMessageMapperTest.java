package at.irian.ankor.messaging.json.viewmodel;

import at.irian.ankor.action.Action;
import at.irian.ankor.change.Change;
import at.irian.ankor.messaging.*;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Manfred Geiler
 */
public class ViewModelJsonMessageMapperTest {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ViewModelJsonMessageMapperTest.class);

    private MessageFactory messageFactory;
    private ViewModelJsonMessageMapper msgMapper;

    @org.junit.Before
    public void setUp() throws Exception {
        messageFactory = new MessageFactory(new CounterMessageIdGenerator(""));
        msgMapper = new ViewModelJsonMessageMapper();
    }

    @Test
    public void testSimpleAction() throws Exception {
        Action action = new Action("test");
        Message msg = messageFactory.createActionMessage("sid", "context.model", action);
        String json = msgMapper.serialize(msg);
        LOG.info("JSON: {}", json);

        Message desMsg = msgMapper.deserialize(json);
        LOG.info("Message: {}", desMsg);

        Assert.assertEquals(ActionMessage.class, desMsg.getClass());
        ActionMessage actionMsg = (ActionMessage) desMsg;
        Assert.assertEquals(Action.class, actionMsg.getAction().getClass());
        Action simpleAction = actionMsg.getAction();
        Assert.assertEquals("test", simpleAction.getName());
    }

    @Test
    public void testChange() throws Exception {
        Message msg = messageFactory.createChangeMessage("sid", "root.test1", new Change("new-value"));
        String json = msgMapper.serialize(msg);
        LOG.info("JSON: {}", json);

        Message desMsg = msgMapper.deserialize(json);
        LOG.info("Message: {}", desMsg);

        Assert.assertEquals(ChangeMessage.class, desMsg.getClass());
        ChangeMessage changeMsg = (ChangeMessage) desMsg;
        Assert.assertEquals("root.test1", changeMsg.getProperty());
    }

    @Test
    public void testChangeRoot() throws Exception {
        Message msg = messageFactory.createChangeMessage("sid", "root", new Change("new-value"));
        String json = msgMapper.serialize(msg);
        LOG.info("JSON: {}", json);

        Message desMsg = msgMapper.deserialize(json);
        LOG.info("Message: {}", desMsg);

        Assert.assertEquals(ChangeMessage.class, desMsg.getClass());
        ChangeMessage changeMsg = (ChangeMessage) desMsg;
        Assert.assertEquals("root", changeMsg.getProperty());
    }


}
