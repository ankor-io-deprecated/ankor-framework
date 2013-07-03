package at.irian.ankorman.sample1.server;

import at.irian.ankor.ref.ChangeListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankorman.sample1.model.ModelRoot;
import at.irian.ankorman.sample1.model.Tab;
import at.irian.ankorman.sample1.model.animal.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
* @author Thomas Spiegl
*/
@SuppressWarnings("UnusedDeclaration")
public class ServiceBean {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ServiceBean.class);

    private final AnimalRepository animalRepository;

    public ServiceBean() {
        animalRepository = new AnimalRepository();
    }

    public ModelRoot init() {
        ModelRoot model = new ModelRoot();
        model.setUserName("John Doe");
        return model;
    }

    public Data<Animal> searchAnimals(AnimalSearchFilter filter, Paginator paginator) {
        return animalRepository.searchAnimals(filter, paginator.getFirst(), paginator.getMaxResults());
    }

    public void saveAnimal(Ref modelRef) {
        AnimalDetailModel model = modelRef.getValue();
        String status;
        if (model.isSaved()) {
            status = "Error: Animal already saved";
        } else {
            try {
                animalRepository.saveAnimal(model.getAnimal());
                modelRef.append("saved").setValue(true);
                status = "Animal successfully saved";
            } catch (Exception e) {
                status = "Error: " + e.getMessage();
                if (!(e instanceof IllegalArgumentException || e instanceof IllegalStateException)) {
                    LOG.error("Error saving animal " + model.getAnimal().getUuid(), e);
                }
            }
        }
        modelRef.root().append("serverStatus").setValue(status);
    }

    public void saveAnimals(List<Animal> animals) {
        for (Animal animal : animals) {
            animalRepository.saveAnimal(animal);
        }
    }

    public void createAnimalSearchTab(final Ref tabsRef, final String tabId) {
        AnimalSearchModel model = new AnimalSearchModel(getAnimalSelectItems());

        Tab<AnimalSearchModel> tab = new Tab<AnimalSearchModel>(tabId);
        tab.setModel(model);
        tab.setName("Animal Search (" + tabId + ")");

        Ref tabRef = tabsRef.append(tabId);
        tabRef.setValue(tab);

        tabRef.append("model.filter.type").addPropChangeListener(new AnimalTypeChangeListener());

        tabRef.append("model.filter").addTreeChangeListener(new ChangeListener() {
            @Override
            public void processChange(Ref filterRef, Ref changedProperty) {
                LOG.info("RELOADING animals ...");
                Ref modelRef = filterRef.ancestor("model");
                AnimalSearchModel model = modelRef.getValue();
                model.getAnimals().getPaginator().reset();
                Data<Animal> animals = searchAnimals(model.getFilter(),
                        model.getAnimals().getPaginator());
                modelRef.append("animals").setValue(animals);
                LOG.info("... finished RELOADING");
                filterRef.root().append("serverStatus").setValue("");
            }
        }, 100L);
        tabRef.append("model.filter").addTreeChangeListener(new ChangeListener() {
            @Override
            public void processChange(Ref filterRef, Ref changedProperty) {
                filterRef.root().append("serverStatus").setValue("loading data ...");
            }
        });

        tabRef.append("model.animals.paginator").addTreeChangeListener(new ChangeListener() {
            @Override
            public void processChange(Ref paginatorRef, Ref changedProperty) {
                paginatorRef.root().append("serverStatus").setValue("loading data ...");
                Ref modelRef = paginatorRef.ancestor("model");
                AnimalSearchModel model = modelRef.getValue();
                Data<Animal> animals = searchAnimals(model.getFilter(),
                        model.getAnimals().getPaginator());
                Ref animalsRef = paginatorRef.ancestor("animals");
                animalsRef.setValue(animals);
                paginatorRef.root().append("serverStatus").setValue("");
            }
        });
    }

    public void createAnimalDetailTab(final Ref tabsRef, String tabId) {

        AnimalDetailModel model = new AnimalDetailModel(new Animal(), getAnimalSelectItems());

        Tab<AnimalDetailModel> tab = new Tab<AnimalDetailModel>(tabId);
        tab.setModel(model);
        tab.setName("New Animal (" + tabId + ")");

        Ref tabRef = tabsRef.append(tabId);
        tabRef.setValue(tab);

        tabRef.append("model.animal.type").addPropChangeListener(new AnimalTypeChangeListener());

        tabRef.append("model.animal.name").addPropChangeListener(new ChangeListener() {
            @Override
            public void processChange(Ref nameRef, Ref changedProperty) {
                Ref nameStatusRef = nameRef.ancestor("model").append("nameStatus");
                String name = nameRef.getValue();
                nameRef.ancestor("model").parent().append("name").setValue("New Animal (" + name + ")");
                if (animalRepository.isAnimalNameAlreadyExists(name)) {
                    nameStatusRef.setValue("name already exists");
                } else if (name.length() > 10) {
                    nameStatusRef.setValue("name is too long");
                } else {
                    nameStatusRef.setValue("");
                }
            }
        });
    }

    public void createDefaultTabs(final Ref tabsRef, String tabId) {
        createAnimalDetailTab(tabsRef, "D1");
        createAnimalSearchTab(tabsRef, "D2");
    }

    private AnimalSelectItems getAnimalSelectItems() {
        List<AnimalType> types = new ArrayList<AnimalType>(AnimalType.values().length + 1);
        types.addAll(Arrays.asList(AnimalType.values()));
        return new AnimalSelectItems(types, new ArrayList<AnimalFamily>());
    }

    public class AnimalTypeChangeListener implements ChangeListener {
        @Override
        public void processChange(Ref typeRef, Ref changedProperty) {
            AnimalType type = typeRef.getValue();
            List<AnimalFamily> families;
            if (type != null) {
                families = new ArrayList<AnimalFamily>();
                switch (type) {
                    case Bird:
                        families.add(AnimalFamily.Accipitridae);
                        break;
                    case Fish:
                        families.add(AnimalFamily.Esocidae);
                        families.add(AnimalFamily.Salmonidae);
                        break;
                    case Mammal:
                        families.add(AnimalFamily.Balaenopteridae);
                        families.add(AnimalFamily.Felidae);
                        break;
                }
            } else {
                families = new ArrayList<AnimalFamily>(0);
            }
            Ref modelRef = typeRef.ancestor("model");
            Ref familiesRef = modelRef.append("selectItems.families");
            familiesRef.setValue(families);
            if (modelRef.append("filter.family").isValid()) {
                modelRef.append("filter.family").setValue(null);
            } else {
                modelRef.append("animal.family").setValue(null);
            }
        }
    }

}
