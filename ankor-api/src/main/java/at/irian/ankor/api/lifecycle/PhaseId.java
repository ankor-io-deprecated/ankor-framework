package at.irian.ankor.api.lifecycle;

/**
 */
public enum PhaseId {
    /**
     * Get client messages from message bus and translate them to model changes and actions
     */
    DecodeClientMessage,

    /**
     * Restore the user session model by means of the StateManager
     */
    RestoreModel,

    /**
     * Process all ModelChangeEvents and apply them to the model
     */
    UpdateModelValues,

    /**
     * Process all ModelActionEvents and call the corresponding action methods
     */
    InvokeApplication,

    /**
     * Detect model changes and calculate the delta for sending to the client
     */
    DetectModelChanges,

    /**
     * Save the model in the user session (via StateManager)
     */
    SaveModel,

    /**
     * Collect all relevant response information for the client and send them in a message to the client
     */
    SendResponseMessages
}
