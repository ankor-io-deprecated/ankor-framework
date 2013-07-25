package at.irian.ankorman.sample1.fxclient;

/**
 * @author Thomas Spiegl
 */
public enum TabType {

    animalSearchTab("createAnimalSearchTab", "animal_search_tab.fxml"),
    animalDetailTab("createAnimalDetailTab", "animal_detail_tab.fxml"),

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
