### Add Tasks

The server side View Model defines an action method `root.model.newTask(String task)`.

The server side method gets called as soon as we fire an action for Ref `root.model`
 with action name `newTask`.

    :::objectivec
    [ANKRefs fireAction:@"root.model" name:@"newTask" params:params]

The parameter `params` is a map containing an element with key 'title'.

On Server side ActionListener will get called.

    :::java
    @ActionListener
    public void newTask(@Param("title") final String title)

Open `AppDelegate.m` within `TodoList` folder in Xcode and uncomment the line as shown here:

    :::objectivec
    - (IBAction)unwindToList:(UIStoryboardSegue *)segue
    {
        AddToDoItemViewController *source = [segue sourceViewController];
        NSDictionary* params = source.toDoItem;
        if (params != nil) {
            [ANKRefs fireAction:@"root.model" name:@"newTask" params:params];
        }
    }


Now start the application again and test if adding a new task works.