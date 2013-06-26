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

    public Tab createAnimalSearchTab(final Ref tabsRef, final String tabId) {
        Tab<AnimalSearchTabModel> tab = new Tab<AnimalSearchTabModel>(tabId);
        tab.setModel(new AnimalSearchTabModel(getAnimalSelectItems()));

        Ref tabRef = tabsRef.append(tabId);
        tabRef.append("model.filter.name").addChangeListener(new ChangeListener() {
            @Override
            public void processChange(Ref changedProperty, Ref filterNameRef) {
                reloadAnimals(filterNameRef);
            }
        });
        tabRef.append("model.filter.type").addChangeListener(new AnimalTypeChangeListener(tabRef.append(
                "model.selectItems.families")));

        tabRef.append("model.filter.family").addChangeListener(new ChangeListener() {
            @Override
            public void processChange(Ref changedProperty, Ref ref) {
                reloadAnimals(ref);
            }
        });

        tabRef.append("model.animals.paginator.first").addChangeListener(new ChangeListener() {
            @Override
            public void processChange(Ref changedProperty, Ref watchedProperty) {
                Ref animalsRef = watchedProperty.parent().parent();
                Ref modelRef = animalsRef.parent();
                AnimalSearchTabModel model = modelRef.getValue();
                Data<Animal> animals = searchAnimals(model.getFilter(),
                                                     model.getAnimals().getPaginator());
                animalsRef.setValue(animals);
            }
        });

        return tab;
    }

    private void reloadAnimals(Ref filterNameRef) {
        Ref modelRef = parent(AnimalSearchTabModel.class, filterNameRef);
        AnimalSearchTabModel model = modelRef.getValue();
        model.getAnimals().getPaginator().reset();
        Data<Animal> animals = searchAnimals(model.getFilter(),
                                             model.getAnimals().getPaginator());
        modelRef.append("animals").setValue(animals);
    }

    private AnimalSelectItems getAnimalSelectItems() {
        List<AnimalType> types = new ArrayList<AnimalType>(AnimalType.values().length + 1);
        types.addAll(Arrays.asList(AnimalType.values()));
        return new AnimalSelectItems(types, new ArrayList<AnimalFamily>());
    }

    public Tab createAnimalDetailTab(final Ref tabsRef, String tabId) {
        Tab<AnimalDetailTabModel> tab = new Tab<AnimalDetailTabModel>(tabId);
        tab.setModel(new AnimalDetailTabModel(new Animal(), getAnimalSelectItems()));
        tab.getModel().setAnimal(new Animal());

        Ref tabRef = tabsRef.append(tabId);

        tabRef.append("model.animal.type").addChangeListener(new AnimalTypeChangeListener(tabRef.append(
                "model.selectItems.families")));

        return tab;
    }

    public void saveAnimals(List<Animal> animals) {
        for (Animal animal : animals) {
            AnimalRepository.saveAnimal(animal);
        }
    }

    public class AnimalTypeChangeListener implements ChangeListener {

        private final Ref ref;

        public AnimalTypeChangeListener(Ref ref) {
            this.ref = ref;
        }

        @Override
        public void processChange(Ref changedProperty, Ref watchedProperty) {
            AnimalType type = watchedProperty.getValue();
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
            ref.setValue(families);
            parent(AnimalSearchTabModel.class, ref).append("filter.family").setValue(null);
            reloadAnimals(watchedProperty.parent()); // TODO will be called 2x if filter.family changes as well
        }
    }

    static Ref parent(Class clazz, Ref ref) {
        while (ref != null) {
            Object value = ref.getValue();
            if (value != null && value.getClass().getName().equals(clazz.getName())) {
                //noinspection unchecked
                return ref;
            }
            ref = ref.parent();
        }
        throw new IllegalStateException(String.format("Parent not found for type %s(%s)", clazz.getName(), ref));
    }
}
