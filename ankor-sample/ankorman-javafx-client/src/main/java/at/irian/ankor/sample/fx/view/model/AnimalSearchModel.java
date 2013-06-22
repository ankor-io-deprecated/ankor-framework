package at.irian.ankor.sample.fx.view.model;

import at.irian.ankor.sample.fx.server.model.Animal;
import at.irian.ankor.sample.fx.server.model.AnimalSearchFilter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;

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
