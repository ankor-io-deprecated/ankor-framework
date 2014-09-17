# Ankor Release 0.4

## New Features

* Spring Support:
  * Auto-wiring: View model beans now can used @Autowired to inject Spring beans
  * Spring post processing: View model beans are initialized by Spring BeanPostProcessors, so it is now possible to
                            use e.g. @PostConstruct, etc.
  * Start Ankor applications with Spring Boot
  * See Samples: TodoWebSocketSpringBootServer.java, AnimalsWebSocketSpringBootServer.java

* @InjectedRef feature: View model beans can now get their Ref injected by defining a field of type Ref annotated with @InjectedRef

* Utilities for React Javascript Clients


## Improvements



## Migration

