package at.irian.ankorsamples.animals.domain;

import at.irian.ankor.base.ObjectUtils;
import at.irian.ankorsamples.animals.viewmodel.animal.AnimalSearchFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
* @author Thomas Spiegl
*/
@SuppressWarnings("UnusedDeclaration")
public class AnimalRepository {

    private List<Animal> animals;

    public static int MAX_NAME_LEN = 10;

    public AnimalRepository() {
        animals = new ArrayList<>();
        animals.add(new Animal("Trout", AnimalType.Fish, AnimalFamily.Salmonidae));
        animals.add(new Animal("Salmon", AnimalType.Fish, AnimalFamily.Salmonidae));
        animals.add(new Animal("Pike", AnimalType.Fish, AnimalFamily.Esocidae));
        animals.add(new Animal("Eagle", AnimalType.Bird, AnimalFamily.Accipitridae));
        animals.add(new Animal("Blue Whale", AnimalType.Mammal, AnimalFamily.Balaenopteridae));
        animals.add(new Animal("Tiger", AnimalType.Mammal, AnimalFamily.Felidae));
        for (int i = 0; i < 1000; i++) {
            animals.add(new Animal("Bird " + i, AnimalType.Bird, AnimalFamily.Accipitridae));
        }
    }

    public List<AnimalType> getAnimalTypes() {
        return Arrays.asList(AnimalType.values());
    }

    public List<AnimalFamily> getAnimalFamilies(AnimalType type) {
        List<AnimalFamily> families;
        if (type != null) {
            families = new ArrayList<>();
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
            families = new ArrayList<>(0);
        }
        return families;
    }

    public List<Animal> searchAnimals(AnimalSearchFilter filter, int first, int maxResults) {
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

        int from = Math.max(first, 0);
        int to = Math.min(first + maxResults, animals.size());

        List<Animal> result = animals.subList(from, to);

        try {
            // simulate database latency
            Thread.sleep(300L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return result;
    }

    private List<Animal> getAnimals() {
        List<Animal> result = new ArrayList<>(animals.size());
        for (Animal animal : animals) {
            result.add(new Animal(animal));
        }
        return result;
    }

    public boolean isAnimalNameAlreadyExists(String name) {
        for (Animal animal : animals) {
            if (animal.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void saveAnimal(Animal animal) {
        if (ObjectUtils.isEmpty(animal.getName())) {
            throw new IllegalArgumentException("Animal name is empty");
        }
        if (animal.getType() == null) {
            throw new IllegalArgumentException("Animal type is empty");
        }
        if (animal.getFamily() == null) {
            throw new IllegalArgumentException("Animal family is empty");
        }
        if (animal.getName().length() > MAX_NAME_LEN) {
            throw new IllegalArgumentException("Animal name is too long");
        }
        for (Animal a : animals) {
            if (!a.getUuid().equals(animal.getUuid()) && a.getName().equals(animal.getName())) {
                throw new IllegalStateException("Animal with name '" + animal.getName() + "' already exists");
            }
        }
        int i = 0;
        for (Animal a : animals) {
            if (a.getUuid().equals(animal.getUuid())) {
                animals.set(i, new Animal(animal));
                return;
            }
            i++;
        }
        animals.add(new Animal(animal));
    }

    public Animal findAnimal(String animalUUID) {
        for (Animal animal : animals) {
            if (animal.getUuid().equals(animalUUID)) {
                return new Animal(animal);
            }
        }
        return null;
    }

    public void deleteAnimal(String uuid) {
        Iterator<Animal> iterator = animals.iterator();
        while (iterator.hasNext()) {
            Animal next = iterator.next();
            if (next.getUuid().equals(uuid)) {
                iterator.remove();
                break;
            }
        }
    }
}
