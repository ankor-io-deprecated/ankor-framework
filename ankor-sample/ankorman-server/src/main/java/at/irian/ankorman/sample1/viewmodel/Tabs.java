package at.irian.ankorman.sample1.viewmodel;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelMapBase;
import at.irian.ankorman.sample1.domain.animal.Animal;
import at.irian.ankorman.sample1.domain.animal.AnimalFamily;
import at.irian.ankorman.sample1.domain.animal.AnimalType;
import at.irian.ankorman.sample1.server.AnimalRepository;
import at.irian.ankorman.sample1.viewmodel.animal.AnimalDetailModel;
import at.irian.ankorman.sample1.viewmodel.animal.AnimalSearchModel;
import at.irian.ankorman.sample1.viewmodel.animal.AnimalSelectItems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Thomas Spiegl
 */
public class Tabs extends ViewModelMapBase<String, Tab> {

    private AnimalRepository animalRepository;

    protected Tabs(Ref viewModelRef, AnimalRepository animalRepository) {
        super(viewModelRef, new HashMap<String, Tab>());
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

        Ref tabRef = thisRef().append(tabId);

        Tab<AnimalSearchModel> tab = new Tab<>(tabId, tabRef, "Animal Search", "animalSearchTab");
        AnimalSearchModel model = new AnimalSearchModel(tabRef.append("model"), animalRepository, getAnimalSelectItems(), tab.getName());
        tab.setModel(model);

        tabRef.setValue(tab);

    }

    @ActionListener
    public void createAnimalDetailTab() {
        String tabId = TabIds.next();

        Ref tabRef = thisRef().append(tabId);
        ModelRoot root = thisRef().root().getValue();

        Tab<AnimalDetailModel> tab = new Tab<>(tabId, tabRef, "New Animal", "animalDetailTab");
        AnimalDetailModel model = new AnimalDetailModel(tabRef.append("model"),
                                                        new Animal(),
                                                        getAnimalSelectItems(),
                                                        animalRepository,
                                                        tab.getName(),
                                                        root.getServerStatus());
        tab.setModel(model);

        tabRef.setValue(tab);
    }

    private AnimalSelectItems getAnimalSelectItems() {
        List<AnimalType> types = new ArrayList<>(AnimalType.values().length + 1);
        types.addAll(Arrays.asList(AnimalType.values()));
        return new AnimalSelectItems(types, new ArrayList<AnimalFamily>());
    }
}
