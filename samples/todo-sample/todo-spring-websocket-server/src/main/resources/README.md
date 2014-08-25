# Note

Since Spring Boot creates an executable JAR instead of a WAR it does not use the `webapp` folder for static content.
Instead the static content is located in `src/main/resources/static`. 
Both the contents of `ankor-js/src/main/webapp` and `todo-js-client/src/main/webapp` have been hand copied there.
Therefore modifications of those will not be reflected in this module without copying the files again.

TODO: Automate
