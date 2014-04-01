### Complete Tasks

Every Task has a completed boolean property. We may set this property on client side and
notify the server about the Change.

#### Mark Task as completed

As soon as a row item in the tasks lists gets clicked, we want to flip the tasks completed state
and send a Change Event to the server.

Here is how it works. Again, just uncomment the line `AppDelegate.m`.

    :::objectivec
    - (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
    {
        ...
        [ANKRefs changeValue:path value:completed];
        [self.tableView reloadData];
    }

#### Remove completed Tasks Action Listener

To remove completed tasks from the list, we just have to fire the `clearTasks` action.

    :::objectivec
    - (IBAction)removeClicked:(id)sender {
        [ANKRefs fireAction:@"root.model" name:@"clearTasks"];
    }

And that's it. Now we have a basic ios todo app that is backed by an Ankor server.
If you haven't done so already, check out the [server tutorial][1].
There you will learn how to write an Ankor server that can be used with this app.

[1]: http://ankor.io/tutorials/server
