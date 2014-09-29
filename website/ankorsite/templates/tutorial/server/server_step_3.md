### Sharing Properties

#### Creating another view model

Let's start off by turning the `TaskListModel` into a view model.
This is similar to the root model.
We do so by calling `initViewModel` inside the constructor.

    :::java
    public class TaskListModel {

        private final Ref modelRef;

        private final TaskRepository taskRepository;

        public TaskListModel(Ref modelRef, TaskRepository taskRepository) {
            AnkorPatterns.initViewModel(this, modelRef);

            this.modelRef = modelRef;
            this.taskRepository = taskRepository;
        }
    }

The first thing you might notice are the two fields 'modelRef' and 'taskRepository'. These are both objects
we need for implementing our server behaviour, but we do not want to send them to the client.
Obviously we can't send the entire task repository (our service layer) to the client.
As goes for the `modelRef` which we only use internally.
To accomplish this we just omit (public) getters for these fields because Ankor (by default) does only send
properties with public getters to the client.

#### Adding view model properties

Let's add some view model properties that Ankor should not ignore:

    :::java
    private Boolean footerVisibility = false;
    private Integer itemsLeft = 0;
    private String itemsLeftText;

    public Boolean getFooterVisibility() {
        return footerVisibility;
    }

    public void setFooterVisibility(Boolean footerVisibility) {
        this.footerVisibility = footerVisibility;
    }

    public Integer getItemsLeft() {
        return itemsLeft;
    }

    public void setItemsLeft(Integer itemsLeft) {
        this.itemsLeft = itemsLeft;
    }

    public String getItemsLeftText() {
        return itemsLeftText;
    }

    public void setItemsLeftText(String itemsLeftText) {
        this.itemsLeftText = itemsLeftText;
    }

Again we need getters and setters for all of these.

For testing purposes we should set some dummy values in the constructor:

    :::java
    footerVisibility = true;
    itemsLeft = 10;
    itemsLeftText = "imaginary items left";

With this we are almost ready to test our server implementation.
But we still need to link our view models with the servlet.

#### Check if the bindings work

Restart the servlet (using your favourite method) and 
point your browser to [`http://localhost:8080`](http://localhost:8080).
Your dummy text should appear in the footer.
