package at.irian.ankorman.sample1.server;

import at.irian.ankor.application.Application;
import at.irian.ankor.event.ChangeListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankorman.sample1.model.model.animal.Animal;
import at.irian.ankorman.sample1.model.model.animal.AnimalFamily;
import at.irian.ankorman.sample1.model.model.animal.search.AnimalSearchFilter;
import at.irian.ankorman.sample1.model.model.animal.AnimalType;
import at.irian.ankorman.sample1.model.model.animal.edit.AnimalDetailModel;
import at.irian.ankorman.sample1.model.model.animal.search.AnimalSearchModel;
import at.irian.ankorman.sample1.model.model.ModelRoot;
import at.irian.ankorman.sample1.model.model.Tab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
* @author Thomas Spiegl
*/
@SuppressWarnings("UnusedDeclaration")
public class ServiceBean {

    private Application application;

    public void setApplication(Application application) {
        this.application = application;
    }

    public ModelRoot init() {
        ModelRoot model = new ModelRoot();
        model.setUserName("John Doe");
        return model;
    }

    public List<Animal> searchAnimals(AnimalSearchFilter filter) {
        String nameFilter = filter.getName() != null && filter.getName().trim().length() > 0 ? filter.getName().toLowerCase() : null;
        List<Animal> animals = AnimalRepository.getAnimals();
        if (filter.getType() != null || nameFilter != null) {
            for (Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
                Animal current = it.next();
                if (nameFilter != null && !current.getName().toLowerCase().contains(nameFilter)) {
                    it.remove();
                    continue;
                }
                if (filter.getType() != null && current.getType() != filter.getType()) {
                    it.remove();
                }
            }
        }
        return animals;
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
        Tab<AnimalSearchModel> tab = new Tab<AnimalSearchModel>(tabId);
        tab.setModel(new AnimalSearchModel());
        List<AnimalType> types = new ArrayList<AnimalType>(AnimalType.values().length + 1);
        types.addAll(Arrays.asList(AnimalType.values()));
        types.add(null);
        tab.getModel().getFilter().setTypes(types);
        tab.getModel().getFilter().setFamilies(new ArrayList<AnimalFamily>(0));
        application.getListenerRegistry()
                   .registerRemoteChangeListener(tabsRef.sub(tabId).sub("model.filter.type"), new ChangeListener() {
            @Override
            public void processChange(Ref modelContext, Ref watchedProperty, Ref changedProperty) {
                AnimalType type = changedProperty.getValue();
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
                    families.add(null);
                } else {
                    families = new ArrayList<AnimalFamily>(0);
                }
                tabsRef.sub(tabId).sub("model.filter.families").setValue(families);
            }
        });
        return tab;
    }

    public Tab createAnimalDetailTab(String tabId, String animalUUID) {
        Tab<AnimalDetailModel> tab = new Tab<AnimalDetailModel>(tabId);
        tab.setModel(new AnimalDetailModel());
        tab.getModel().setAnimal(AnimalRepository.findAnimal(animalUUID));
        return tab;
    }

    public Tab createAnimalDetailTab(String tabId) {
        Tab<AnimalDetailModel> tab = new Tab<AnimalDetailModel>(tabId);
        tab.setModel(new AnimalDetailModel());
        tab.getModel().setAnimal(new Animal());
        return tab;
    }

    public void saveAnimals(List<Animal> animals) {
        for (Animal animal : animals) {
            AnimalRepository.saveAnimal(animal);
        }
    }
}
