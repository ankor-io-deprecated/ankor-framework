[TOC]

# Stateless

Starting with version 0.3 Ankor supports stateless servers.
This means that no view model is kept in memory between requests.
Let's see how we can modify the todo example from the [tutorial][tutorial] to become a stateless server.

This quick tutorial assumes that you are familiar with the basics of Ankor and our todo example app. 

## Application

First of all we need to tell Ankor that we want our application be stateless.
Here is the complete `Application` implementation for the stateless todo app:

    :::java
    public class StatelessTodoServerApplication extends SimpleSingleRootApplication {
    
        private static final TaskRepository db = new TaskRepository();
    
        public StatelessTodoServerApplication() {
            super("Stateless Todo Server", "root");
        }
    
        @Override
        public boolean isStateless() {
            return true;
        }
    
        @Override
        public Object createModel(Ref rootRef, Map<String, Object> connectParameters) {
            return new ModelRoot(rootRef, db);
        }
    }
    
Note that:

* `isStateless` has to be overwritten and has to return `true`.
* `createModel` will be called for every request / message to the server. 

## View Model

This sets the basis for our stateless application, however we need to modify our view model as well.

In our [`TaskListModel`][tasklistmodel] we use the [`@StateHolder`][stateholder] annotation to tell Ankor which properties "carry" the state of the application.
This means that the last state of the view model can be restored by using these properties.

In the case of our todo application it's only the `filter` property:

    :::java
    @StateHolder
    private Filter filter;
    

More specifically the following will happen:

1. Messages from the server to the client will contain an additional `stateProps` field, 
which is a list of all the properties annotated with `@StateHolder`.
2. Messages from the client to the server will contain an additional `stateValues` field, 
containing the current state of all "stateProps".
3. The server will create a new view model by calling `createModel` in your `Application` implementation (see above).
4. The server will call the setter of every state property on the view model.
5. The request is processed by the view model as with a stateful server.

<div class="alert alert-danger">
<strong>Important:</strong> Step 4 implies that all setters for state properties are written so that a consistent state of the model is maintained.
In other words, the setters need to restore the last state of the view model.
</div>

### Example

Let's look at our todo example:

    :::java
    private void initCalculatedFields() {
        this.tasks = queryTaskList(this.filter); // load the todos from from the DB
        
        this.itemsLeft = taskRepository.countTasks(Filter.active);
        this.itemsComplete = taskRepository.countTasks(Filter.completed);

        this.filterAllSelected = calcAllButtonSelected(this.filter);
        this.filterActiveSelected = calcActiveButtonSelected(this.filter);
        this.filterCompletedSelected = calcCompletedButtonSelected(this.filter);
        
        ...
    }
    
As you can see there is a method `initCalculatedFields` that sets the properties of all "calculated" fields. 
Calculated fields are basically all fields thate are not `@StateHolder`s.
Most of them depend directly or indirectly on the current value of `filter`, which is our state holder.

Obviously we use `initCalculatedFields` in the constructor to initialize the fields:
    
    :::java
    public TaskListModel(Ref modelRef, TaskRepository taskRepository) {
        AnkorPatterns.initViewModel(this, modelRef);
        this.modelRef = modelRef;
        this.taskRepository = taskRepository;

        this.filter = Filter.all;
        initCalculatedFields();
    }
    
However, we also need to call `initCalculatedFields` in `setFilter` to keep the state of the calculated fields consistent with the new value of `filter`:

    :::java
    public void setFilter(String filter) {
        this.filter = Filter.valueOf(filter);
        initCalculatedFields();
    }
    
To recap: To activate the stateless feature in Ankor you have to override `isStateless` in your application.
Next you have to find the properties that "carry" the state of the application and annotate them with `@StateHolder`.
In order for it to work correctly it is required to keep the state of the view model consistent when the setters of the state holders are invoked.
    

[tutorial]: http://ankor.io/tutorials/server
[tasklistmodel]: #
[stateholder]: #







