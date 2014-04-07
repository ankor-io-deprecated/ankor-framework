# Ankor Release 0.2

## New Features

* Monitoring
* Console application
* New event dispatchers: AkkaConsistentHashingEventDispatcher, AkkaSessionBoundEventDispatcher
* @AutoSignal/Cglib proxy feature: calling setter directly (instead of Ref.setValue) triggers change event (see Animal sample)
* Javascript: todosample integration with "React"

## Improvements

* Messaging replaced by pluggable Connector architecture (see Switchboard)
* Viewmodel metadata and pluggable annotation support
* Simplified JavaFX Client setup and startup
* Simplified Server setup and startup (Ankor Application interface replaces ModelRootFactory)
* WebSocket: automatic reconnect
* iOS Client: improvements and automatic reconnect

## Migration

Please have a look at the new tutorials for Ankor 0.2
