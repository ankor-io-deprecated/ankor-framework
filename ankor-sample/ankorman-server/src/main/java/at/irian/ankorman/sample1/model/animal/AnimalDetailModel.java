package at.irian.ankorman.sample1.model.animal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Thomas Spiegl
 */
public class AnimalDetailModel {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalDetailModel.class);

    @JsonIgnore
    private boolean saved = false;

    private Animal animal;

    private String nameStatus;

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

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public String getNameStatus() {
        return nameStatus;
    }

    public void setNameStatus(String nameStatus) {
        this.nameStatus = nameStatus;
    }
}
