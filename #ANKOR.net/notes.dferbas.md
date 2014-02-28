# for todo app

* make an abstraction of Dispatcher in ankor core and remove windows.base dependency afterwards

* check: is an observable list necessary
 
* check: "Biglist" bzw. Insert/Delete elements

* style it! http://mahapps.com/MahApps.Metro/guides/quick-start.html
  http://mui.codeplex.com/documentation
  
* try to silverlight it
  
* need a IRef interface to hide stuff in DynaRef

# short term stuff

* DynaRef: RootRef["path"]["to"]["kas"] wie RootRef.Append("path").Append("to")...

* make a consistent model about deref a ref: when to observe, when to deref shallow/deep, iterate...

* migrate code to new git repo and commit/push!

* the maps are not sorted, starting from server (talk to manfred), json.net seems to not sort, and so on -> something is done here?? check

* clean dependencies/packages: jsonmodel not in messaging, pathsyntax not in ref (at least if circular is than still), ref interface einziehen, jmodel interface...

* check and rename all exising delegates/events/handler according to naming rules: http://www.codeproject.com/Articles/20550/C-Event-Implementation-Fundamentals-Best-Practices

* check commands as actions (the wpf way)

* add logging lib

* add delete change to implement closing tab
* Big list impl (need a local proxylist that implements IList (not only IList<T>) to work with the controls
* need change types insert/delete/modify..

* support remote action messages

* better impl of the sortedDictionary (that works if not readonly too)

* think about oberservable collections
* implement @ankorWatched == observableCollection?!

* clean the whole jmodel locking/parallel update stuff

* "root" prefix should not be fixed

# done

* support "replace" change type
* consider having the client side model not in Json.net but a plain dictionary hierarchy (need language to evaluate then, impromptu?, spring expl, scripting, DLR?, self)
-> used DynaObject == IDictionary for now
* unify the "root" prefix stuff (java uses it)
* make a more generic event system: Listener, dispatcher, all events in one list, 
	* internal events unify, external events unify to internal, iternalSetValue+UpdateModel unify 

* Fire actions from refs 
* support List/Array (model to ref) for Animal combo box -> OK, is supported, but the binding is only possible via the string value because the items and the selected item are different refs
* restore all the tabs in startup
* fix remote change problem -> is more or less ok for non map/list right now
* web sockets for .net

# long term stuff

* "pattern matching" for change listener, for annotation later

* implement refs that get invalidated (value/subtree is deleted) IsValidate prop und unreg wenn nicht mehr vorhanden??

# To remember

#### Debug databinding

    SelectedItem="{Binding Path=model.Value, PresentationTraceSources.TraceLevel=High}"

#### Add a debug converter
```    
    <DataGridTextColumn Binding="{Binding Value, Converter={Ankor:Debug}}" />
```
    
#### Set trace debug levels in code

			PresentationTraceSources.Refresh();
			Trace.Listeners.Add(new TextWriterTraceListener("C:/out.txt"));

			PresentationTraceSources.DataBindingSource.Listeners.Add(new ConsoleTraceListener() );
			PresentationTraceSources.DataBindingSource.Switch.Level = SourceLevels.All;

# ankor java

* How are client port numbers distributed?

* try to test a view model

* check e.g. AnimalDetailModel: not consistent with the member vs ref values (editable, nameStatus...) 

* About the server side programming model
    * aspectj can do load time weaving (like spring even w.o. an agent at startup), this would be the only completely transparent solution (aspects on field access) evtl. marked by annotation
	* proxying will not work bo of the local calls when delegating. non delegation but super calls work with cglib...


# VS plugins
* git source control provider
* (git extensions ?)
* nu get package manager
* prod power tools
* solution explorer tools