package at.irian.ankorsamples.animals.viewmodel.animal;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.AnkorInit;
import at.irian.ankor.annotation.AutoSignal;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.TypedRef;
import at.irian.ankorsamples.animals.domain.Animal;
import at.irian.ankorsamples.animals.domain.AnimalFamily;
import at.irian.ankorsamples.animals.domain.AnimalRepository;
import at.irian.ankorsamples.animals.domain.AnimalType;
import at.irian.ankorsamples.animals.viewmodel.PanelNameCreator;

import java.util.Collections;
import java.util.List;

import static at.irian.ankor.viewmodel.factory.BeanFactories.newPropertyInstance;

/**
 * @author Thomas Spiegl
 */
@SuppressWarnings("UnusedDeclaration")
@AutoSignal
public class AnimalDetailModel {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalDetailModel.class);

    private TypedRef<String> panelNameRef;
    private TypedRef<String> serverStatusRef;
    private Ref i18nResourcesRef;
    private AnimalRepository animalRepository;

    private Animal animal;

    private AnimalSelectItems selectItems;

    private boolean editable;

    private boolean saved = false;

    private String nameStatus;

    @AnkorInit
    public void init(TypedRef<String> panelNameRef,
                     TypedRef<String> serverStatusRef,
                     AnimalRepository animalRepository,
                     Ref i18nResourcesRef,
                     Animal animal) {
        this.i18nResourcesRef = i18nResourcesRef;
        this.animal = animal;
        this.selectItems = newPropertyInstance(AnimalSelectItems.class, this, "selectItems");
        this.selectItems.init(animalRepository.getAnimalTypes(),
                              animal != null
                              ? animalRepository.getAnimalFamilies(animal.getType())
                              : Collections.<AnimalFamily>emptyList());
        this.animalRepository = animalRepository;
        this.panelNameRef = panelNameRef;
        this.serverStatusRef = serverStatusRef;
        this.editable = true;
        this.nameStatus = "ok";
    }

    @ChangeListener(pattern = ".animal.name")
    public void onNameChanged() {

        panelNameRef.setValue(getPanelName());

        String name = animal.getName();
        if (animalRepository.isAnimalNameAlreadyExists(name)) {
            setNameStatus("name already exists");
        } else if (name.length() > AnimalRepository.MAX_NAME_LEN) {
            setNameStatus("name is too long");
        } else {
            setNameStatus("ok");
        }
    }

    @ChangeListener(pattern = "root.locale")
    public void onLocaleChanged() {
        panelNameRef.setValue(getPanelName());
    }

    public String getPanelName() {
        String name = animal.getName();
        return new PanelNameCreator().createName(i18nResourcesRef.appendLiteralKey("EditAnimal").<String>getValue(), name);
    }

    AnimalDetailModel changed(Object obj) {
        return null;
    }

    AnimalDetailModel on(Object obj, Object obj2) {
        return null;
    }

    AnimalDetailModel call(Object obj) {
        return null;
    }

    private static class Types {
        public static <T> T any() {
            return null;
        }
    }

    @ChangeListener(pattern = ".animal.type")
    @AutoSignal(".animal.family")
    public void animalTypeChanged() {
        AnimalType type = animal.getType();
        List<AnimalFamily> families;
        if (type != null) {
            families = animalRepository.getAnimalFamilies(type);
        } else {
            families = Collections.emptyList();
        }
        selectItems.setFamilies(families);

        //noinspection SuspiciousMethodCalls
        if (!families.contains(animal.getFamily())) {
            //myRef.appendPath("animal.family").setValue(null);
            animal.setFamily(null);
        }
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
                setEditable(false);
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
