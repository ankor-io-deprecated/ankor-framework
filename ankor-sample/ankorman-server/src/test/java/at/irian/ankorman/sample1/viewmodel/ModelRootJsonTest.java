package at.irian.ankorman.sample1.viewmodel;

import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.messaging.json.JsonViewModelMessageMapper;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.system.SimpleAnkorSystem;
import at.irian.ankorman.sample1.domain.animal.Animal;
import at.irian.ankorman.sample1.domain.animal.AnimalFamily;
import at.irian.ankorman.sample1.domain.animal.AnimalType;
import at.irian.ankorman.sample1.server.AnimalRepository;
import at.irian.ankorman.sample1.viewmodel.animal.AnimalSearchModel;
import at.irian.ankorman.sample1.viewmodel.animal.AnimalSelectItems;
import at.irian.ankorman.sample1.viewmodel.animal.Data;
import at.irian.ankorman.sample1.viewmodel.animal.Paginator;
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

    private JsonViewModelMessageMapper mapper;
    private MessageFactory messageFactory;
    private RefFactory rf;

    @Before
    public void setUp() throws Exception {
        SimpleAnkorSystem system = SimpleAnkorSystem.create("test", ModelRoot.class, false);
        messageFactory = system.getMessageFactory();
        rf = system.getRefContextFactory().createRefContext().refFactory();
        mapper = new JsonViewModelMessageMapper();
    }

    @Test
    public void test() throws Exception {
        ModelRoot root = new ModelRoot(rf.rootRef(), new AnimalRepository());
        root.setUserName("Max Muster");

        Tab<AnimalSearchModel> tab = new Tab<AnimalSearchModel>("A1", rf.ref("root.tabs").append("A1"), "Test");
        root.getTabs().put("A1", tab);

        AnimalSearchModel model = new AnimalSearchModel(rf.ref("root.tabs.A1"),
                new AnimalRepository(), new AnimalSelectItems(new ArrayList<AnimalType>(), new ArrayList<AnimalFamily>()), tab.getName());
        tab.setModel(model);
        List<Animal> animals = Arrays.asList(new Animal("fish", AnimalType.Fish, AnimalFamily.Accipitridae),
                                             new Animal("bird", AnimalType.Bird, AnimalFamily.Balaenopteridae));
        Data<Animal> data = new Data<Animal>(new Paginator(0, 5));
        data.setRows(animals);
        model.setAnimals(data);

        String json = mapper.serialize(messageFactory.createChangeMessage(rf.ref("root").path(), root));
        LOG.info(json);

        Message message = mapper.deserialize(json);
        LOG.info(message.toString());

        json = mapper.serialize(messageFactory.createChangeMessage(rf.ref("root.tabs.A1.model.animals").path(), animals));
        LOG.info(json);
    }
}
