package at.irian.ankorsamples.animals.viewmodel.animal;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.messaging.AnkorIgnore;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.TypedRef;
import at.irian.ankorsamples.animals.domain.animal.Animal;
import at.irian.ankorsamples.animals.domain.animal.AnimalRepository;
import at.irian.ankorsamples.animals.viewmodel.PanelNameCreator;

/**
 * @author Thomas Spiegl
 */
@SuppressWarnings("UnusedDeclaration")
public class AnimalDetailModel {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalDetailModel.class);

    @AnkorIgnore
    private final AnimalRepository animalRepository;
    @AnkorIgnore
    private boolean saved = false;

    private final TypedRef<String> panelNameRef;
    private final TypedRef<String> serverStatusRef;

    private final Ref myRef;

    private Animal animal;

    private AnimalSelectItems selectItems;

    private boolean editable;

    private String nameStatus;

    public AnimalDetailModel(Ref myRef,
                             TypedRef<String> panelNameRef,
                             TypedRef<String> serverStatusRef,
                             AnimalRepository animalRepository,
                             Animal animal) {
        this.myRef = myRef;
        this.animal = animal;
        this.selectItems = AnimalSelectItems.create(animalRepository.getAnimalTypes());
        this.animalRepository = animalRepository;
        this.panelNameRef = panelNameRef;
        this.serverStatusRef = serverStatusRef;
        this.editable = true;
        this.nameStatus = "ok";
        AnkorPatterns.initViewModel(this, myRef);
    }

    @ChangeListener(pattern = ".animal.name")
    public void onNameChanged() {
        String name = animal.getName();

        panelNameRef.setValue(new PanelNameCreator().createName("New Animal", name));

        if (animalRepository.isAnimalNameAlreadyExists(name)) {
            myRef.appendPath("nameStatus").setValue("name already exists");
        } else if (name.length() > AnimalRepository.MAX_NAME_LEN) {
            myRef.appendPath("nameStatus").setValue("name is too long");
        } else {
            myRef.appendPath("nameStatus").setValue("ok");
        }
    }

    @ChangeListener(pattern = "(@).animal.type")
    public void animalTypeChanged(Ref modelRef) {
        Ref familyRef = modelRef.appendPath("animal.family");
        Ref familiesRef = modelRef.appendPath("selectItems.families");
        new AnimalTypeChangeHandler(animalRepository).handleChange(animal.getType(), familyRef, familiesRef);
    }

    @ActionListener
    public void save() {

        LOG.info("save action");

        String status;
        if (saved) {
            status = "Error: Animal already saved";
        } else {
            try {
                animalRepository.saveAnimal(animal);
                saved = true;
                myRef.appendPath("editable").setValue(false);
                status = "Animal successfully saved";
            } catch (Exception e) {
                status = "Error: " + e.getMessage();
                if (!(e instanceof IllegalArgumentException || e instanceof IllegalStateException)) {
                    LOG.error("Error saving animal " + animal.getUuid(), e);
                }
            }
        }
        serverStatusRef.setValue(status);
    }

    public AnimalSelectItems getSelectItems() {
        return selectItems;
    }

    public Animal getAnimal() {
        return animal;
    }

    public boolean isEditable() {
        return editable;
    }

    public String getNameStatus() {
        return nameStatus;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setNameStatus(String nameStatus) {
        this.nameStatus = nameStatus;
    }
}
