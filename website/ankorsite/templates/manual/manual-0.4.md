[TOC]

# Introduction

## What is Ankor?

Ankor is a framework that helps sharing logical object structures (models) and events (actions and changes)
between a client and a server. Ankor's goal is to do this in a transparent way, so that the developer does
not even have to bother with the server-client communication at all.

Our mission statement:

> Bind your UI to the client-side view model, implement behaviour on the server-side - and let Ankor do the rest.

## What Ankor is **not**

Ankor does not re-invent the wheel. Therefore:

* Ankor is **not** yet another fancy UI widget library.

    Use your favorite well-established client framework
    like *Dojo Toolkit*, *jQuery UI* for browser-based clients, or *JavaFX* for your Java-based client.
    However, Ankor makes using these UI libraries much simpler by helping to
    keep the necessary client code to a minimum.

* Ankor is **not** a new network transport protocol.

    Ankor itself makes use of popular protocols like *HTTP* or *WebSocket*. In fact Ankor can be used with virtually
    any network protocol that supports reliable message transport. Planned protocols for future versions of Ankor are
    *JMS* and *RabbitMQ*.

* Ankor is **not** a code generation tool.

    We believe that you as the developer are the master of your code. Ankor helps to develop your presentation
    model in a natural and descriptive way. There is no need to generate or precompile code or use templates.

    (Well: depending on your client technology you might actually be forced to use code generation and templating,
    but this is out of scope for Ankor)

## Ankor Use Cases

Ankor.io is not meant as the framework or tool that is always the right choice and beats everything. For simple form-based applications using Ankor is like taking the sledgehammer to crack a nut.

The following use cases and scenarious are those where Ankor.io comes into play:

* **Complex** user interface with sophisticated validation and behaviour
* High-performance **reactive** user interface with **asynchronous** server communication
* Complex view model with various GUIs on **different platforms and devices**

## Architectural Pattern

### MVVM - Model View ViewModel

Ankor is largely influenced by the well-known MVVM pattern. See [Wikipedia][mvvm-wikipedia] for a good overview of this pattern.

<img src="/static/images/manual/mvvm.png" style="width:600px" title="Classical MVVM"/>

### MVSVM - Model View with Sync'ed ViewModel

The following question arise when using the MVVM pattern in a client-server environment:

* How does the client communicate with the server?
* What communication channels and service calls do I need to build my ViewModel on the client?
* How can my ViewModel (dynamically) react on Model changes on the server?

Ankor enhances the MVVM pattern by closing the client-server gap and thus building an extended pattern that we call *MVSVM* - the *Model View with Sync'ed ViewModel* pattern. The idea is to have the ViewModel on both sides of the "wire" so that both the client and the server can access and manipulate the ViewModel data.

<img src="/static/images/manual/mvsvm.png" style="width:600px" title="Model View with Sync'ed ViewModel"/>

The Ankor framework promises to take over all that cumbersome tasks for making client and server communicate. This is achieved by implementing the ViewModel on the server-side and letting Ankor automatically synchronize the ViewModel to the client. The ViewModel behaviour is defined on the server-side, the client-side ViewModel only holds the data. The synchronization is done bi-directionally by means of change events and action events.

<img src="/static/images/manual/ankor-mvsvm-events.png" style="width:600px" title="Ankor MVSVM Events"/>

Keep in mind that technically speaking the ViewModel may totally differ on the client and the server side. Only the structure and the primitive values are the same on both sides. On the server side the ViewModel is normally implemented by means of JavaBeans in a tree structure.

On the client side the ViewModel is type-less and can be implemented as:

* a Java data structure consisting of nested Maps, Lists and Primitives (JavaFX)
* a Javascript data structure consisting of nested Dictionaries, Arrays and Primitives (HTML5)
* an Objective-C data structure consisting of nested Dictionaries, Arrays and Primitives (iOS, OS X)
* a C# data structure consisting of nested Dynamics, Lists and Primitives (.NET)

<img src="/static/images/manual/ankor-viewmodel.png" style="width:600px" title="Ankor ViewModel"/>


## Ankor Modules

The Ankor framework consists of several modules from which you probably only need some of them.

#### Core Module (ankor-core)

Core Module, defining all basic Ankor types like Ref, Events, etc.

This is a mandatory module for Ankor-based servers and Java clients.

#### Unified EL Support (ankor-el)

Java Unified Expression Language (JSR-245) support for Ref path syntax.

This is the default module for Ankor Ref path parsing and is currently mandatory for Ankor-based servers and Java clients.

#### Json Support (ankor-json)

Json serialization/deserialization support using Jackson as underlying implementation.

This is the default module for message serialization/deserialization and is currently mandatory for Ankor-based servers and Java clients.

#### Service (ankor-service)

AnkorSystem builder and helper classes for starting up Ankor servers and clients.

This is an optional module that is highly recommended for simplified set up of Ankor-based servers and Java clients.

#### Annotation Support (ankor-annotation)

Annotation support for providing model metadata by means of Java annotations.

This is the default module for ViewModel metadata definition and is currently mandatory for Ankor-based servers.

#### Javascript Client (ankor-js)

Javascript client support overlay war for Ankor based web-servers.

This is a mandatory module for Ankor-based HTML5 clients.

#### Ankor JavaFX Support (ankor-fx)

Java FX client support.

This is a mandatory module for Ankor-based JavaFX clients.

#### Actor Support (ankor-actor)

Actor support using Akka as dispatcher for message and event handling on the server side.

This is the default module for event dispatching and concurrency control and is highly recommended to be used instead of the "simple synchronized event dispatching" on Ankor-based servers. On Java-based Ankor clients the Actor-based event dispatching is not necessary.

#### WebSocket Connector (ankor-websocket-connector)

Messaging via WebSocket.

This is the preferred messaging implementation for Ankor and is a mandatory module for Ankor-based servers and Java clients.

#### Socket Connector (ankor-socket-connector)

Messaging via simple socket connections.

In (unit) testing environments this connector may be used instead of the "Websocket Connector" as a simpler to set up network communication. Both the server and the client have dedicated socket ports where they can receive messages from the other party. These fixed port numbers have to be specified in the settings and known by each communicating party.

#### Auto Proxies (ankor-proxy)

Proxy support for automatic change event firing on setter calls.

This is an experimental module that enhances server-side ViewModel beans by means of CGLib proxies that automatically fire change events on every bean setter call.


## Setting up an Ankor application

### Maven BOM

The Maven artifact "ankor-bom" is the Ankor "bill of materials" (see [Maven Dependency Mechanism][maven-dependency-mechanism]) and can be used to define all Ankor versions in the dependencyManagement section of your application.

	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>at.irian.ankor</groupId>
				<artifactId>ankor-bom</artifactId>
				<version>0.4</version>
			</dependency>
		</dependencies>
	</dependencyManagement>


### Maven Bundles

In addition, Ankor offers several predefined Maven bundles that can be used as convenient single-point dependencies for applications.

#### Javascript Client Bundle (ankor-bundle-js-client)

Bundle dependencies (overlay) for a JavaScript Client.

This can be used to include the Ankor Javascript resources in your web application by means of the Maven dependency mechanism. Think of Bower (see [below](#bower)) as an alternative for including Ankor Javascript resources in your HTML5 client application.

#### ViewModel Bundle (ankor-bundle-server-viewmodel)

Bundle for server-side ViewModel modules.

All Ankor base classes, utilities, helpers and annotations needed for Ankor-enriched view models. Use this bundle if you have a separate module for your view model in your application and you want to keep the Ankor dependencies of this module to a minimum.

#### Socket JavaFX Client Bundle (ankor-bundle-socket-fx-client)

Bundle dependencies for a socket based JavaFX Client.

Use this bundle for a JavaFX based Ankor client that communicates with a server over a raw socket connection.
> Please note, that socket-based Ankor communication is a proof-of-concept and not meant for production!

#### Socket Server Bundle (ankor-bundle-socket-server)

Bundle dependencies for a socket based standalone server.

Use this bundle for an Ankor server that communicates with it's clients over raw socket connections.

> Please note, that socket-based Ankor communication is a proof-of-concept and not meant for production!

#### WebSocket JavaFX Client Bundle (ankor-bundle-websocket-fx-client)

Bundle dependencies for a WebSocket based JavaFX Client.

Use this bundle for a JavaFX based Ankor client that communicates with a server over a bi-directional WebSocket connection.

#### Custom WebSocket Client Bundle (ankor-bundle-websocket-java-client)

Bundle dependencies for a general WebSocket based Java Client (such as a batch application or a Swing client).

Use this bundle if your client is based on Java and either has no user interface or you want to use a UI technology other than JavaFX.

#### WebSocket Server Bundle (ankor-bundle-websocket-server)

Bundle dependencies for a WebSocket based server endpoint.

Use this bundle for an Ankor server that communicates with it's clients over WebSocket connections.

#### WebSocket SpringBoot Server Bundle (ankor-bundle-websocket-springboot-server)

Bundle dependencies for a WebSocket-based Spring Boot server.

Use this bundle for an Ankor server that communicates with it's clients over WebSocket connections and        is started on top of [Spring Boot][spring-boot].

<a id="bower"/>
### Ankor Bower Package "ankor-js" 

To include the Ankor Javascript resources in your HTML5 client you can use [Bower][bower]. 
Make sure that [`bower`](bower) is installed on your system.

Add Ankor with:

	$ bower install ankor-js
	
#### Example usage

Somewhere in your main `.html` file, before your other code, add:

    <script src="path/to/bower_components/ankor-js/ankor.js"></script>
    
This will define a global `ankor` namespace object (you can also include it with requirejs).

Create a basic ankor system that connects to a WebSocket endpoint:

    var ankorSystem = new ankor.AnkorSystem({
        modelId: "root",
        utils: new ankor.utils.BaseUtils(),
        transport: new ankor.transport.WebSocketTransport("/websocket/ankor", {
          connectProperty: "root"
        }),
        debug: true
    });
    
Get the root ref via:
    
    rootRef = ankorSystem.getRef("root");
    
and listen for changes:

    rootRef.addPropChangeListener(function (ref) { ... });

## Getting started

### Hello world

A simple Ankor view model that counts the number of changes of a property.

    public class HelloAnkorApplication extends SimpleSingleRootApplication {
        
        public HelloAnkorApplication() {
            super("Hello Ankor", "root");
        }
    
        @Override
        public Object createModel(Ref rootRef, Map<String, Object> connectParameters) {
            return new RootModel(rootRef);
        }
        
        public class RootModel {
    
            private final Ref rootRef;
            
            // view model properties
            private String helloWorld = "Hello World";
            private int count = 0;
            
            public RootModel(Ref rootRef) {
                AnkorPatterns.initViewModel(this, rootRef); // make Ankor view model
                this.rootRef = rootRef; // save the ref
            }
            
            @ChangeListener(pattern = "root.helloWorld")
            public void onHelloWorldChanged() {
                // count the changes of the helloWorld property and send the new value to the client
                rootRef.appendPath("count").setValue(count + 1);
            }
            
            // TODO: getters and setters for view model properties
        }
    }
    
This snippet is taken form the [`hello-ankor`][helloankor] sample.

### Tutorials

The best way to get an idea of how applications can be written using Ankor is stepping through the [tutorials](/tutorials).

# Ankor Terms and Concepts

## What is a Ref?

A core concept of Ankor is the [`Ref`][ref]. 
It represents a reference to a view model.
All view model properties are ordered in a hierarchical tree structure.
The Ref object allows us to navigate this tree structure and manipulate the underlying properties.

You can also think in terms of paths. A Ref is a path in the view model tree.

Example:
	
	root: {
		prop1: "hello",
		prop2: "world",
		people: [{id: 1, name: "foo"}, {id: 2, name: "bar"}],
		attributes: {
			color: "red",
			size: "big"
		}
	}
	
### Creating a Ref

	Ref prop1Ref = refFactory.ref("root.prop1");
	Ref user1NameRef = refFactory.ref("root.people[1].name");
	Ref colorRef = refFactory.ref("root.attributes.color");
	
### Obtaining the value of a Ref

	System.out.println(prop1Ref.getValue());      //prints "hello"
	System.out.println(user1NameRef.getValue());  //prints "foo"
	System.out.println(colorRef.getValue());      //prints "red"
	
### Changing the value of a view model property by means of a Ref

	prop1Ref.setValue("hi");
	user1NameRef.setValue("baz");
	colorRef.setValue("blue");
	
### View Model Tree traversal by means of a Ref

	Ref rootRef = prop1Ref.parent();
	
	Ref attrRef = colorRef.parent();
	Ref sizeRef = attrRef.appendLiteralKey("size");
	
	Ref peopleRef = rooRef.appendPath("people");
	Ref user2Ref = peopleRef.appendIndex(2);
	

## Change Events

An Ankor change event has the following attributes:

* the changed view model property, represented by a Ref
* the change, consisting of
	* type of change (value change, array item deletion, list insertion, etc.)
	* new value


## Action Events

An Ankor action event has the following attributes:

* the view model property, on which the action has happened
* the action, consisting of
	* the name of the action
	* optional action parameters




[ref]: http://ankor.io/javadoc/at/irian/ankor/ref/Ref.html
[helloankor]: https://github.com/ankor-io/hello-ankor
[bower]: http://bower.io
[spring-boot]: http://projects.spring.io/spring-boot/
[maven-dependency-mechanism]: http://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html
[mvvm-wikipedia]: http://en.wikipedia.org/wiki/Model_View_ViewModel
