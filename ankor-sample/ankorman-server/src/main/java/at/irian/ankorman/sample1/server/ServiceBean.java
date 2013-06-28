package at.irian.ankorman.sample1.server;

import at.irian.ankor.ref.ChangeListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankorman.sample1.model.ModelRoot;
import at.irian.ankorman.sample1.model.Tab;
import at.irian.ankorman.sample1.model.animal.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
* @author Thomas Spiegl
*/
@SuppressWarnings("UnusedDeclaration")
public class ServiceBean {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ServiceBean.class);

    public ModelRoot init() {
        ModelRoot model = new ModelRoot();
        model.setUserName("John Doe");
        return model;
    }

    public Data<Animal> searchAnimals(AnimalSearchFilter filter, Paginator paginator) {
        return AnimalRepository.searchAnimals(filter, paginator.getFirst(), paginator.getMaxResults());
    }

    public void saveAnimal(Animal animal) {
        AnimalRepository.saveAnimal(animal);
    }

    private static class AnimalRepository {
        private static List<Animal> animals;
        static {
            animals = new ArrayList<Animal>();
            animals.add(new Animal("Trout", AnimalType.Fish, AnimalFamily.Salmonidae));
            animals.add(new Animal("Salmon", AnimalType.Fish, AnimalFamily.Salmonidae));
            animals.add(new Animal("Pike", AnimalType.Fish, AnimalFamily.Esocidae));
            animals.add(new Animal("Eagle", AnimalType.Bird, AnimalFamily.Accipitridae));
            animals.add(new Animal("Blue Whale", AnimalType.Mammal, AnimalFamily.Balaenopteridae));
            animals.add(new Animal("Tiger", AnimalType.Mammal, AnimalFamily.Felidae));
            for (int i = 0; i < 20; i++) {
                animals.add(new Animal("Bird " + i, AnimalType.Bird, AnimalFamily.Accipitridae));
            }
        }

        public static Data<Animal> searchAnimals(AnimalSearchFilter filter, int first, int maxResults) {
            String nameFilter = filter.getName() != null && filter.getName().trim().length() > 0 ? filter.getName().toLowerCase() : null;
            List<Animal> animals = getAnimals();
            if (filter.getType() != null || nameFilter != null) {
                for (Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
                    Animal current = it.next();
                    if (nameFilter != null && !current.getName().toLowerCase().contains(nameFilter)) {
                        it.remove();
                    } else if (filter.getType() != null && current.getType() != filter.getType()) {
                        it.remove();
                    } else if (filter.getFamily() != null && current.getFamily() != filter.getFamily()) {
                        it.remove();
                    }
                }
            }
            if (first >= animals.size()) {
                return new Data<Animal>(new Paginator(animals.size(), maxResults));
            }
            if (first < 0) {
                first = 0;
            }
            int last = first + maxResults;
            if (last > animals.size()) {
                last = animals.size();
            }

            Data<Animal> data = new Data<Animal>(new Paginator(first, maxResults));

            data.getRows().addAll(animals.subList(first, last));

            try {
                Thread.sleep(300L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return data;
        }

        private static List<Animal> getAnimals() {
            List<Animal> result = new ArrayList<Animal>(animals.size());
            for (Animal animal : animals) {
                result.add(new Animal(animal));
            }
            return result;
        }

        public static void saveAnimal(Animal animal) {
            int i = 0;
            for (Animal a : animals) {
                if (a.getUuid().equals(animal.getUuid())) {
                    animals.set(i, animal);
                    return;
                }
                i++;
            }
            animals.add(new Animal(animal));
        }

        public static Animal findAnimal(String animalUUID) {
            for (Animal animal : animals) {
                if (animal.getUuid().equals(animalUUID)) {
                    return new Animal(animal);
                }
            }
            return null;
        }
    }

    public void createAnimalSearchTab(final Ref tabsRef, final String tabId) {
        AnimalSearchModel model = new AnimalSearchModel(getAnimalSelectItems());

        Tab<AnimalSearchModel> tab = new Tab<AnimalSearchModel>(tabId);
        tab.setModel(model);

        Ref tabRef = tabsRef.append(tabId);
        tabRef.setValue(tab);

        tabRef.append("model.filter.type").addPropChangeListener(new AnimalTypeChangeListener());

        tabRef.append("model.filter").addTreeChangeListener(new ChangeListener() {
            @Override
            public void processChange(Ref filterRef, Ref changedProperty) {
                reloadAnimals(filterRef);
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

    private void reloadAnimals(Ref filterRef) {
        LOG.info("RELOADING animals ...");
        Ref modelRef = filterRef.ancestor("model");
        AnimalSearchModel model = modelRef.getValue();
        model.getAnimals().getPaginator().reset();
        Data<Animal> animals = searchAnimals(model.getFilter(),
                                             model.getAnimals().getPaginator());
        modelRef.append("animals").setValue(animals);
        LOG.info("... finished RELOADING");
    }

    private AnimalSelectItems getAnimalSelectItems() {
        List<AnimalType> types = new ArrayList<AnimalType>(AnimalType.values().length + 1);
        types.addAll(Arrays.asList(AnimalType.values()));
        return new AnimalSelectItems(types, new ArrayList<AnimalFamily>());
    }

    public void createAnimalDetailTab(final Ref tabsRef, String tabId) {

        AnimalDetailModel model = new AnimalDetailModel(new Animal(), getAnimalSelectItems());

        Tab<AnimalDetailModel> tab = new Tab<AnimalDetailModel>(tabId);
        tab.setModel(model);

        Ref tabRef = tabsRef.append(tabId);
        tabRef.setValue(tab);

        tabRef.append("model.animal.type").addPropChangeListener(new AnimalTypeChangeListener());
    }

    public void saveAnimals(List<Animal> animals) {
        for (Animal animal : animals) {
            AnimalRepository.saveAnimal(animal);
        }
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
            familiesRef.ancestor("model").append("filter.family").setValue(null);
        }
    }

}
