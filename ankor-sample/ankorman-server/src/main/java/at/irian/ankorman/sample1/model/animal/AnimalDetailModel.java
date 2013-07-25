package at.irian.ankorman.sample1.model.animal;

import at.irian.ankor.annotation.AnnotationAwareViewModelBase;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.model.ViewModelProperty;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.util.ObjectUtils;
import at.irian.ankorman.sample1.server.AnimalRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Thomas Spiegl
 */
public class AnimalDetailModel extends AnnotationAwareViewModelBase {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalDetailModel.class);

    @JsonIgnore
    private final ViewModelProperty<String> tabName;

    @JsonIgnore
    private final AnimalRepository animalRepository;

    @JsonIgnore
    private boolean saved = false;

    private Animal animal;

    private boolean editable;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE, defaultImpl = AnimalSelectItems.class)
    private AnimalSelectItems selectItems;

    private ViewModelProperty<String> nameStatus;

    /**
     * client side constructor
     */
    protected AnimalDetailModel() {
        super(null);
        this.tabName = null;
        this.animalRepository = null;
    }

    /**
     * server side constructor
     */
    public AnimalDetailModel(AnimalRepository animalRepository,
                             Animal animal, AnimalSelectItems selectItems,
                             Ref myRef, ViewModelProperty<String> tabName) {
        super(myRef);
        this.tabName = tabName;
        this.animalRepository = animalRepository;
        this.selectItems = selectItems;
        this.animal = animal;
        this.editable = true;
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

    public ViewModelProperty<String> getNameStatus() {
        return nameStatus;
    }

    public void setNameStatus(ViewModelProperty<String> nameStatus) {
        this.nameStatus = nameStatus;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @ChangeListener(pattern = "**.<AnimalDetailModel>.animal.name")
    public void onNameChanged() {
        String name = animal.getName();

        tabName.set(tabName("New Animal", name));

        if (animalRepository.isAnimalNameAlreadyExists(name)) {
            nameStatus.set("name already exists");
        } else if (name.length() > AnimalRepository.MAX_NAME_LEN) {
            nameStatus.set("name is too long");
        } else {
            nameStatus.set("ok");
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
