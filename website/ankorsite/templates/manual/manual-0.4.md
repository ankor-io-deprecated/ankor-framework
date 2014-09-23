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

## Architectural Pattern

### MVVM - Model View ViewModel

Ankor is largely influenced by the well-known MVVM pattern. See [Wikipedia][mvvm-wikipedia] for a good overview of this pattern.

### MVSVM - Model View with Sync'ed ViewModel

The following question arise when using the MVVM pattern in a client-server environment:

* How does the client communicate with the server?
* What communication channels and service calls do I need to build my ViewModel on the client?
* How can my ViewModel (dynamically) react on Model changes on the server?

Ankor enhances the MVVM pattern by closing the client-server gap an thus building an extended pattern that we call *MVSVM* - the *Model View with Sync'ed ViewModel* pattern.

The Ankor framework promises to take over all that cumbersome tasks for making client and server to communicate.


## Overview

### Features

TBD

### Use-cases

* Complex user interface with sophisticated validation and behaviour
* High-performance reactive user interface with asynchronous server communication
* Complex view model with various GUIs on various platforms and devices

## Ankor Modules

The Ankor framework consists of several modules from which you probably only need some of them.

#### ankor-actor

Actor support using Akka as dispatcher for message and event handling on the server side.

#### ankor-annotation

Annotation support for providing model metadata by means of Java annotations.

#### ankor-core

Core Module, needed in every Ankor server and (Java based) client application.

#### ankor-el

Java Unified Expression Language (JSR-245) support for Ref path syntax.

#### ankor-fx

Java FX client support.

#### ankor-js

Javascript client support overlay war for Ankor based web-server.

#### ankor-json

Json serialization/deserialization support using Jackson as underlying implementation.

#### ankor-proxy

Proxy support for automatic change event firing on setter calls.

#### ankor-service

AnkorSystem builder and helper classes for starting up Ankor servers and clients.

#### ankor-socket-connector

Messaging via simple socket connections (only for testing, not meant for production).

#### ankor-websocket-connector

Messaging via WebSocket (preferred default messaging implementation for Ankor).

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

#### Socket JavaFX Client (ankor-bundle-socket-fx-client)

Bundle dependencies for a socket based JavaFX Client.

Use this bundle for a JavaFX based Ankor client that communicates with a server over a raw socket connection.
> Please note, that socket-based Ankor communication is a proof-of-concept and not meant for production!

#### Socket Server (ankor-bundle-socket-server)

Bundle dependencies for a socket based standalone server.

Use this bundle for an Ankor server that communicates with it's clients over raw socket connections.

> Please note, that socket-based Ankor communication is a proof-of-concept and not meant for production!

#### WebSocket JavaFX Client (ankor-bundle-websocket-fx-client)

Bundle dependencies for a WebSocket based JavaFX Client.

Use this bundle for a JavaFX based Ankor client that communicates with a server over a bi-directional WebSocket connection.

#### Custom WebSocket Client (ankor-bundle-websocket-java-client)

Bundle dependencies for a general WebSocket based Java Client (such as a batch application or a Swing client).

Use this bundle if your client is based on Java and either has no user interface or you want to use a UI technology other than JavaFX.

#### WebSocket Server (ankor-bundle-websocket-server)

Bundle dependencies for a WebSocket based server endpoint.

Use this bundle for an Ankor server that communicates with it's clients over WebSocket connections.

#### WebSocket SpringBoot Server (ankor-bundle-websocket-springboot-server)

Bundle dependencies for a WebSocket-based Spring Boot server.

Use this bundle for an Ankor server that communicates with it's clients over WebSocket connections and        is started on top of [Spring Boot][spring-boot].

### Bower

To include the Ankor Javascript resources in your HTML5 client you can use [Bower](bower). Make sure that [Bower](bower) is installed on your system.

Install Ankor with:

	$ bower install ankor-js


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

# General

## What is a Ref?

A core concept of Ankor is the [`Ref`][ref]. 
It represents a reference to a view model.
All view model properties are ordered in a hierarchical tree structure.
The Ref object allows us to navigate this tree structure and manipulate the underlying properties.

## Change Events

TBD

## Action Events

TBD

# Frequently Asked Questions

TBD

[ref]: http://ankor.io/static/javadoc/apidocs-0.3/at/irian/ankor/ref/Ref.html
[helloankor]: https://github.com/ankor-io/hello-ankor
[bower]: http://bower.io
[spring-boot]: http://projects.spring.io/spring-boot/
[maven-dependency-mechanism]: http://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html
[mvvm-wikipedia]: http://en.wikipedia.org/wiki/Model_View_ViewModel
