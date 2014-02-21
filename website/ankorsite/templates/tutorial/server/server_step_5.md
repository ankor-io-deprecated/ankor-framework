### Reacting to Changes

In the previous step we've seen how to use the `@ActionListener` annotation to react to `Action`s from the client.
In this step we'll be using the `@ChangeListener` annotation to react to changes form both the client and server.

The problem was that the `footerVisibility` would never update after it was set to `false` initially.
What we want to have is a method that does this:

    :::java
    boolean footerVisibility = taskRepository.getTasks().size() != 0;
    modelRef.appendPath("footerVisibility").setValue(footerVisibility);

These statements should be called whenever the `itemsLeft` property changes.
The `@ChangeListener` annotation needs a pattern to

    :::java
    @ChangeListener(pattern = "root.model.itemsLeft")
    public void updateFooterVisibility() {
        boolean footerVisibility = taskRepository.getTasks().size() != 0;
        modelRef.appendPath("footerVisibility").setValue(footerVisibility);
    }




