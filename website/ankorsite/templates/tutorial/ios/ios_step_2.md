### Bind Tasks

We will now explain how to bind the tasks list to our server side View Model.

### Todo View Model

First let's take a look at our View Model. The client will receive the following serialized model on
connect to the Ankor server.

    :::json
    {"model": {
        "filter":"all",
        "tasks":[],
        "footerVisibility":false,
        "itemsLeft":0,
        "itemsLeftText":"items left",
        "clearButtonVisibility":false,
        "itemsComplete":0,
        "itemsCompleteText":"Clear completed (0)",
        "filterAllSelected":true,
        "filterActiveSelected":false,
        "filterCompletedSelected":false,
        "toggleAll":false
        }
    }

The model property `model.tasks` holds a reference to all the tasks. We want to get notified whenever
there is a change to this property in order to re-render the tasks list.

Ankor always uses a Ref (Reference) to refer to a model property. The model name which we passed
to our ANKSystem needs to be prepended. Ref `root.model.tasks` refers to our list of tasks.

(Note: root is the model name we defined for our application
on server side. Ankor may provide multiple model roots and multiple applications).

#### Change Listener

Now we will show how to register a Change Listener to get notified on changes to `root.model.tasks`.
We have 2 options to register a Change Listener:

#### Change Listener Option 1 - Selector

`[ANKRefs observe:@"root.model.tasks" target:self listener:@selector(tasksChanged:)]`

`ANKRefs` binds a Change Listener to the method `tasksChanged`. The new model value will be
passed as a method parameter whenever there was a change to the model (client or server side).

Open `AppDelegate.m` within `TodoList` folder in Xcode and uncomment the line below Option 1 as shown here:

    :::objectivec
    - (void)viewDidLoad
    {
        [super viewDidLoad];
        self.toDoItems = [[NSMutableArray alloc] init];
        // Option 1: Register observer using selector
        [ANKRefs observe:@"root.model.tasks" target:self listener:@selector(tasksChanged:)];
    }

    // The tasksChanged will be called whenever there is a change to the tasks property
    // within the model (see viewDidLoad for listener registration via @selector)
    - (void)tasksChanged:(id) value {
        [[self toDoItems]removeAllObjects];
        [[self toDoItems]addObjectsFromArray:value];
        [self.tableView reloadData];
    }


#### Change Listener Option 2 - Objective-C Block

As an alternative we may also use Objective-C Blocks to register our Change Listener.

Open `AppDelegate.m` within `TodoList` folder in Xcode and uncomment the lines below Option 2 as shown here:

    :::objectivec
    - (void)viewDidLoad
    {
        [super viewDidLoad];
        self.toDoItems = [[NSMutableArray alloc] init];
        // Option 2: Register observer using Block
        [ANKRefs observe:@"root.model.tasks" listener:^(id value) {
            [[self toDoItems]removeAllObjects];
            [[self toDoItems]addObjectsFromArray:value];
            [self.tableView reloadData];
        }];
    }

You are now able to display tasks, but still the list is empty.
To see how adding a new task works, follow the next step.
