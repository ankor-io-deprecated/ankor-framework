### Complete Tasks

A Task has a boolean property completed. We may set this property on client side and
notify the server about the Change.

#### Mark Task as completed

As soon as a row item in the tasks lists gets clicked, we want to flip the tasks completed state
and send a Change Event to the server.

It is very handy to change a value on the client.

    :::objectivec
    [ANKRefs changeValue:@"root.model.someValue" value:@"new-value"];

This line will fire a Change Event and send it to the server.
* `changeValue` refers to the model property
* `value` new value we want to set

Lets go back to our example. Again, just uncomment the line in `AppDelegate.m`.

    :::objectivec
    // Row selected Event Listener
    - (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
    {
        ...
        [ANKRefs changeValue:path value:completed];
        [self.tableView reloadData];
    }


#### Remove completed Tasks Action Listener

To remove completed tasks from the list, we just have to fire the action `clearTasks`.

    :::objectivec
    - (IBAction)removeClicked:(id)sender {
        [ANKRefs fireAction:@"root.model" name:@"clearTasks"];
    }

And that's it. Now we have a basic ios todo app that is backed by an Ankor server.
If you haven't done so already, check out the [server tutorial][1].
There you will learn how to write an Ankor server that can be used with this app.

[1]: http://ankor.io/tutorials/server
