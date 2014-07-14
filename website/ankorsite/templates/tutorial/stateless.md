[TOC]

# Stateless

Starting with version 0.3 Ankor supports stateless servers.
This means that on the server there is no view model kept in memory between messages.
Let's see how we can modify the todo example from the [server tutorial][tutorial] to become a stateless server.

<div class="alert alert-info">
    <strong>Note:</strong> This quick tutorial assumes that you are familiar with the basics of Ankor and our todo example app. 
</div>

## Application

First of all we need to tell Ankor that we want our application be stateless.
Basically all we need to do in your `Application` implementation to to overwrite the `isStateless` method.
Take a look at the `Application` implementation for the stateless todo app:

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
What is meant by "carry" is that these properties are sufficient to restore the last state of the view model.

In the case of our todo application it's just the `filter` property:

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
5. Finally the request is processed by the view model just like with a stateful server.

<div class="alert alert-danger">
    <strong>Important:</strong>
    As you might have noticed, step 4 implies that all setters for state properties have to be written so that a consistent state of the model is maintained.
    In other words, the setters need to restore the last state of the view model.
</div>

## Example

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
    
There is a method `initCalculatedFields` that sets the properties of all "calculated" fields. 
Calculated fields are basically all fields that are not annotated with `@StateHolder`.
Most of them depend directly or indirectly on the current value of `filter`.

Obviously we use `initCalculatedFields` in the constructor to initialize the fields:
    
    :::java
    public TaskListModel(Ref modelRef, TaskRepository taskRepository) {
        AnkorPatterns.initViewModel(this, modelRef);
        this.modelRef = modelRef;
        this.taskRepository = taskRepository;

        this.filter = Filter.all;
        initCalculatedFields();
    }
    
However, we also use `initCalculatedFields` in `setFilter` to keep the state of the calculated fields consistent with the new value of `filter`:

    :::java
    public void setFilter(String filter) {
        this.filter = Filter.valueOf(filter);
        initCalculatedFields();
    }
    
And that's it!

## Recap
1. To activate the stateless feature in Ankor we have to override `isStateless` in our application.
2. Then we have to find the properties that "carry" the state of our application and annotate them with `@StateHolder`.
3. In order for this to work correctly it is required to keep the state of the view model consistent when the setters of the state holders are called.
    

[tutorial]: http://ankor.io/tutorials/server
[tasklistmodel]: https://github.com/ankor-io/ankor-todo-tutorial/blob/server-complete/todo-server/src/main/java/io/ankor/tutorial/viewmodel/TaskListModel.java
[stateholder]: http://ankor.io/static/javadoc/apidocs-0.3/index.html







