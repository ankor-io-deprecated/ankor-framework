package at.irian.ankorman.sample1.model.model;

import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.messaging.json.JsonMessageMapper;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.system.SimpleAnkorSystem;
import at.irian.ankorman.sample1.model.ModelRoot;
import at.irian.ankorman.sample1.model.Tab;
import at.irian.ankorman.sample1.model.animal.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        SimpleAnkorSystem system = SimpleAnkorSystem.create("test", ModelRoot.class, false);
        messageFactory = system.getMessageFactory();
        rf = system.getRefContextFactory().createRefContext().refFactory();
        mapper = new JsonMessageMapper();
    }

    @Test
    public void test() throws Exception {
        ModelRoot root = new ModelRoot();
        root.setUserName("Max Muster");

        Tab<AnimalSearchModel> tab = new Tab<AnimalSearchModel>("A1");
        root.getTabs().put("A1", tab);

        AnimalSearchModel model = new AnimalSearchModel(new AnimalSelectItems(new ArrayList<AnimalType>(), new ArrayList<AnimalFamily>()));
        tab.setModel(model);
        List<Animal> animals = Arrays.asList(new Animal("fish", AnimalType.Fish, AnimalFamily.Accipitridae),
                                             new Animal("bird", AnimalType.Bird, AnimalFamily.Balaenopteridae));
        Data<Animal> data = new Data<Animal>(new Paginator(0, 5));
        data.setRows(animals);
        model.setAnimals(data);

        String json = mapper.serialize(messageFactory.createChangeMessage(rf.rootRef().path(), rf.ref("root").path(), root));
        LOG.info(json);

        Message message = mapper.deserialize(json);
        LOG.info(message.toString());

        json = mapper.serialize(messageFactory.createChangeMessage(rf.rootRef().path(), rf.ref("root.tabs.A1.model.animals").path(), animals));
        LOG.info(json);
    }
}
