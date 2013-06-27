package at.irian.ankorman.sample1.model.animal;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Thomas Spiegl
 */
public class AnimalDetailModel {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalDetailModel.class);

    private Animal animal;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE, defaultImpl = AnimalSelectItems.class)
    private AnimalSelectItems selectItems;

    @SuppressWarnings("UnusedDeclaration")
    protected AnimalDetailModel() {
    }

    public AnimalDetailModel(Animal animal, AnimalSelectItems selectItems) {
        this.selectItems = selectItems;
        this.animal = animal;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public AnimalSelectItems getSelectItems() {
        return selectItems;
    }

    public void setSelectItems(AnimalSelectItems selectItems) {
        this.selectItems = selectItems;
    }
}
