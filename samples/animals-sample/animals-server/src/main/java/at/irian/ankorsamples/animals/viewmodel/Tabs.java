package at.irian.ankorsamples.animals.viewmodel;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.messaging.AnkorIgnore;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelMapBase;
import at.irian.ankorsamples.animals.domain.animal.Animal;
import at.irian.ankorsamples.animals.domain.animal.AnimalRepository;
import at.irian.ankorsamples.animals.viewmodel.animal.AnimalDetailModel;
import at.irian.ankorsamples.animals.viewmodel.animal.AnimalSearchModel;

import java.util.HashMap;

/**
 * @author Thomas Spiegl
 */
public class Tabs extends ViewModelMapBase<String, Tab> {

    @AnkorIgnore
    private final Ref thisRef;
    @AnkorIgnore
    private final AnimalRepository animalRepository;

    protected Tabs(Ref viewModelRef, AnimalRepository animalRepository) {
        super(viewModelRef, new HashMap<String, Tab>());
        this.thisRef = viewModelRef;
        this.animalRepository = animalRepository;
    }

    @Override
    public Tab put(String key, Tab value) {
        if (value == null) {
            return map.remove(key);
        } else {
            return map.put(key, value);
        }
    }

    @ActionListener
    public void createAnimalSearchTab() {
        String tabId = TabIds.next();

        Ref tabRef = thisRef.appendPath(tabId);

        Tab<AnimalSearchModel> tab = new Tab<>(tabId, tabRef, "Animal Search", "animalSearchTab");
        AnimalSearchModel model = new AnimalSearchModel(tabRef.appendPath("model"), animalRepository,
                                                        tabRef.appendPath("name").<String>toTypedRef());
        tab.setModel(model);

        tabRef.setValue(tab);

    }

    @ActionListener
    public void createAnimalDetailTab() {
        String tabId = TabIds.next();

        Ref tabRef = thisRef.appendPath(tabId);
        ModelRoot root = thisRef.root().getValue();

        Tab<AnimalDetailModel> tab = new Tab<>(tabId, tabRef, "New Animal", "animalDetailTab");
        AnimalDetailModel model = new AnimalDetailModel(tabRef.appendPath("model"),
                                                        new Animal(),
                                                        animalRepository,
                                                        tab.getName(),
                                                        root.getServerStatus());
        tab.setModel(model);

        tabRef.setValue(tab);
    }

}
