package at.irian.ankorman.sample1.fxclient;

import at.irian.ankorman.sample1.model.animal.AnimalDetailModel;
import at.irian.ankorman.sample1.model.animal.AnimalSearchModel;

/**
 * @author Thomas Spiegl
 */
public enum TabType {

    animalSearchTab(AnimalSearchModel.class, "animal_search_tab.fxml"), // TODO action name instead of Model class
    animalDetailTab(AnimalDetailModel.class, "animal_detail_tab.fxml"),

    ;

    private final Class modelType;
    private final String fxmlResource;

    private TabType(Class modelType, String fxmlResource) {
        this.modelType = modelType;
        this.fxmlResource = fxmlResource;
    }

    public Class getModelType() {
        return modelType;
    }

    public String getFxmlResource() {
        return fxmlResource;
    }
}
