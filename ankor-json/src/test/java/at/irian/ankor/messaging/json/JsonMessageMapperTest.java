package at.irian.ankor.messaging.json;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.SimpleAction;
import at.irian.ankor.application.ModelHolder;
import at.irian.ankor.messaging.*;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.ref.el.ELRefContext;
import at.irian.ankor.ref.el.ELRefFactory;
import at.irian.ankor.rmi.RemoteMethodAction;
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
    private MessageFactory messageFactory;
    private JsonMessageMapper msgMapper;

    @org.junit.Before
    public void setUp() throws Exception {
        Config config = ConfigFactory.load();
        ELRefContext refContext = ELRefContext.create(new ModelHolder(String.class), null, null, config);
        refFactory = new ELRefFactory(refContext);
        messageFactory = new MessageFactory();
        msgMapper = new JsonMessageMapper(refFactory);
    }

    @Test
    public void testSimpleAction() throws Exception {
        Action action = SimpleAction.create("test");
        Message msg = messageFactory.createActionMessage(refFactory.rootRef(), action);
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
        Message msg = messageFactory.createChangeMessage(refFactory.rootRef(),
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

    @Test
    public void testChangeRoot() throws Exception {
        Message msg = messageFactory.createChangeMessage(refFactory.rootRef(),
                                                         refFactory.rootRef(),
                                                         "new-value");
        String json = msgMapper.serialize(msg);
        LOG.info("JSON: {}", json);

        Message desMsg = msgMapper.deserialize(json);
        LOG.info("Message: {}", desMsg);

        Assert.assertEquals(ChangeMessage.class, desMsg.getClass());
        ChangeMessage changeMsg = (ChangeMessage) desMsg;
        ChangeMessage.Change change = changeMsg.getChange();
        Assert.assertEquals("root", change.getChangedProperty().path());
    }

    @Test
    public void testRMA() throws Exception {
        Action action = RemoteMethodAction.create("serviceBean.saveAnimal(context.model, overwrite)")
                                          .withResultIn("context.successMsg")
                                          .onComplete(SimpleAction.create("completeAction"))
                                          .onError(SimpleAction.create("errorAction"))
                                          .setParam("overwrite", true);
        Message msg = messageFactory.createActionMessage(refFactory.rootRef(), action);
        String json = msgMapper.serialize(msg);
        LOG.info("JSON: {}", json);

        Message desMsg = msgMapper.deserialize(json);
        LOG.info("Message: {}", desMsg);

        Assert.assertEquals(ActionMessage.class, desMsg.getClass());
        ActionMessage actionMsg = (ActionMessage) desMsg;
        Assert.assertEquals(RemoteMethodAction.class, actionMsg.getAction().getClass());
        RemoteMethodAction rma = (RemoteMethodAction) actionMsg.getAction();
        Assert.assertEquals("context.successMsg", rma.getResultPath());
        Assert.assertEquals("completeAction", rma.getCompleteAction().name());
    }

}
