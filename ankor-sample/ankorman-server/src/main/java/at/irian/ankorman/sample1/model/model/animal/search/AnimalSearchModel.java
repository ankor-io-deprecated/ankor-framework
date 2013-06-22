package at.irian.ankorman.sample1.model.model.animal.search;

import at.irian.ankorman.sample1.model.model.animal.Animal;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

/**
* @author Thomas Spiegl
*/
public class AnimalSearchModel {

    private AnimalSearchFilter filter;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE, defaultImpl = Animal.class)
    private List<Animal> animals;

    public AnimalSearchModel() {
        this.filter = new AnimalSearchFilter();
    }

    public AnimalSearchFilter getFilter() {
        return filter;
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    public void setAnimals(List<Animal> animals) {
        this.animals = animals;
    }
}
