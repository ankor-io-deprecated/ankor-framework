package at.irian.ankorsamples.animals.fxclient;

/**
 * @author Thomas Spiegl
 */
public enum TabType {

    animalSearch("createAnimalSearchPanel", "animal_search_tab.fxml"),
    animalDetail("createAnimalDetailPanel", "animal_detail_tab.fxml"),
    ;

    private final String actionName;
    private final String fxmlResource;

    private TabType(String actionName, String fxmlResource) {
        this.actionName = actionName;
        this.fxmlResource = fxmlResource;
    }

    public String getActionName() {
        return actionName;
    }

    public String getFxmlResource() {
        return fxmlResource;
    }
}
