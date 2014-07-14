#### Event-driven framework

React to events asynchronously even on server side. 
Implement your Action and Change Listeners in Java. 
Just annotate your View Model to register Listeners.

    :::java
    @ActionListener
    public void addTask(
        @Param("title") final String title) {...}
    
    @ChangeListener(pattern = ".filter")
    public void filterDidChange() {...}
    
<blockquote>
  <p>An application based on asynchronous communication implements a loosely coupled design, 
  much better so than one based purely on synchronous method calls.</p>
  <footer><a href="http://www.reactivemanifesto.org/">The Reactive Manifesto</a></footer>
</blockquote>
