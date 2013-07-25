package at.irian.ankorman.sample1.server;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.ActionSourceRef;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.annotation.Param;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.util.ObjectUtils;
import at.irian.ankorman.sample1.model.ModelRoot;
import at.irian.ankorman.sample1.model.Tab;
import at.irian.ankorman.sample1.model.Tabs;
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

    @ActionListener(name = "init")
    public void init(@ActionSourceRef Ref rootRef) {
        ModelRoot modelRoot = new ModelRoot(rootRef);
        modelRoot.setUserName("John Doe");
        rootRef.setValue(modelRoot);
    }

    @ActionListener(name = "save", refType = AnimalDetailModel.class)
    public void saveAnimal(@ActionSourceRef Ref modelRef) {
        AnimalDetailModel model = modelRef.getValue();
        String status;
        if (model.isSaved()) {
            status = "Error: Animal already saved";
        } else {
            try {
                animalRepository.saveAnimal(model.getAnimal());
                modelRef.append("saved").setValue(true);
                modelRef.append("editable").setValue(false);
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

    @ActionListener(name = "save", refType = AnimalSearchModel.class)
    public void saveAnimals(@ActionSourceRef Ref modelRef) {
        AnimalSearchModel model = modelRef.getValue();

        String status;
        try {
            for (Animal animal : model.getAnimals().getRows()) {
                animalRepository.saveAnimal(animal);
            }
            status = "Animals successfully saved";
        } catch (Exception e) {
            status = "Error: " + e.getMessage();
            if (!(e instanceof IllegalArgumentException || e instanceof IllegalStateException)) {
                LOG.error("Error saving animals ", e);
            }
        }
        modelRef.root().append("serverStatus").setValue(status);
    }

    @ActionListener(name = "createAnimalSearchTab", refType = Tabs.class)
    public void createAnimalSearchTab(@ActionSourceRef final Ref tabsRef, @Param("tabId") final String tabId) {
        AnimalSearchModel model = new AnimalSearchModel(getAnimalSelectItems());

        Ref tabRef = tabsRef.append(tabId);

        Tab<AnimalSearchModel> tab = new Tab<AnimalSearchModel>(tabId, tabRef, "Animal Search");
        tab.setModel(model);

        tabRef.setValue(tab);

        tabRef.append("model.filter").addTreeChangeListener(new at.irian.ankor.ref.ChangeListener() {
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

    }

    @ChangeListener(pattern = {"**.<AnimalSearchModel>.filter.(type)",
                       "**.<AnimalDetailModel>.animal.(type)"})
    public void animalTypeChanged(Ref typeRef) {
        LOG.info("animalTypeChanged");

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

    @ChangeListener(pattern = "**.<AnimalSearchModel>.(filter).**")
    public void animalFilterChanged(Ref filterRef) {
        LOG.info("animalFilterChanged");

        filterRef.root().append("serverStatus").setValue("loading data ...");

        String name = filterRef.append("name").getValue();

        filterRef.ancestor("model").parent().append("name").setValue(tabName("Animal Search", name));
    }

    @ChangeListener(pattern = "**.<AnimalSearchModel>.animals.(paginator)")
    public void paginatorChanged(Ref paginatorRef) {
        LOG.info("paginatorChanged");

        paginatorRef.root().append("serverStatus").setValue("loading data ...");
        Ref modelRef = paginatorRef.ancestor("model");
        AnimalSearchModel model = modelRef.getValue();
        Data<Animal> animals = searchAnimals(model.getFilter(),
                model.getAnimals().getPaginator());
        Ref animalsRef = paginatorRef.ancestor("animals");
        animalsRef.setValue(animals);
        paginatorRef.root().append("serverStatus").setValue("");
    }



    @ActionListener(name = "createAnimalDetailTab", refType = Tabs.class)
    public void createAnimalDetailTab(@ActionSourceRef final Ref tabsRef, @Param("tabId") final String tabId) {

        Ref tabRef = tabsRef.append(tabId);

        Tab<AnimalDetailModel> tab = new Tab<AnimalDetailModel>(tabId, tabRef, "New Animal");

        AnimalDetailModel model = new AnimalDetailModel(animalRepository,
                                                        new Animal(),
                                                        getAnimalSelectItems(),
                                                        tabRef.append("model"),
                                                        tab.getName());

        tab.setModel(model);

        tabRef.setValue(tab);
    }

    //@ChangeListener(pattern = "**.<AnimalDetailModel>.animal.(name)")
    public void animalNameChanged(Ref nameRef) {
        LOG.info("animalNameChanged");

        Ref modelRef = nameRef.ancestor("model");
        AnimalDetailModel model = modelRef.getValue();

        model.onNameChanged();

    }





    private Data<Animal> searchAnimals(AnimalSearchFilter filter, Paginator paginator) {
        return animalRepository.searchAnimals(filter, paginator.getFirst(), paginator.getMaxResults());
    }

    private AnimalSelectItems getAnimalSelectItems() {
        List<AnimalType> types = new ArrayList<AnimalType>(AnimalType.values().length + 1);
        types.addAll(Arrays.asList(AnimalType.values()));
        return new AnimalSelectItems(types, new ArrayList<AnimalFamily>());
    }

    public class AnimalTypeChangeListener implements at.irian.ankor.ref.ChangeListener {
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

    private static final int MAX_LEN = 15;

    private static String tabName(String name, String value) {
        if (ObjectUtils.isEmpty(value)) {
            return name;
        } else {
            if (value.length() > MAX_LEN) {
                value = value.substring(0, MAX_LEN);
            }
            return String.format("%s (%s)", name, value);
        }
    }

}
