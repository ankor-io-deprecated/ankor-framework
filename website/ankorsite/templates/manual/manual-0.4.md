[TOC]

# Introduction

## What is Ankor?

Ankor is a framework that helps sharing logical object structures (models) and events (actions and changes)
between a client and a server. Ankor's goal is to do this in a transparent way, so that the developer does
not even have to bother with the server-client communication at all.

Our mission statement:

> Bind your UI to the client-side view model, implement behaviour on the server-side
> - and let Ankor do the rest.

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

## MV(S)VM

### MVVM - Model View ViewModel

TBD

## Overview

### Features

TBD

### Use-cases

* Complex user interface with sophisticated validation and behaviour
* High-performance reactive user interface with asynchronous server communication
* Complex view model with various GUIs on various platforms and devices

### Modules

Ankor consists of several modules from which you probably only need some of them. Here is a quick overview:

<table class="table">
    <thead>
        <tr>
            <td><strong>Module name</strong></td>
            <td><strong>Description</strong></td>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>ankor-actor</td>
            <td>Actor support using Akka as dispatcher for message and event handling on the server side.</td>
        </tr>
        <tr>
            <td>ankor-annotation</td>
            <td>Annotation support for providing model metadata by means of Java annotations.</td>
        </tr>
        <tr>
            <td>ankor-core</td>
            <td>Core Module, needed in every Ankor server and (Java based) client application.</td>
        </tr>
        <tr>
            <td>ankor-el</td>
            <td>Java Unified Expression Language (JSR-245) support for Ref path syntax.</td>
        </tr>
        <tr>
            <td>ankor-fx</td>
            <td>Java FX client support.</td>
        </tr>
        <tr>
            <td>ankor-js</td>
            <td>Javascript client support overlay war for Ankor based web-server.</td>
        </tr>
        <tr>
            <td>ankor-json</td>
            <td>Json serialization/deserialization support using Jackson as underlying implementation.</td>
        </tr>
        <tr>
            <td>ankor-proxy</td>
            <td>Proxy support for automatic change event firing on setter calls.</td>
        </tr>
        <tr>
            <td>ankor-service</td>
            <td>AnkorSystem builder and helper classes for starting up Ankor servers and clients.</td>
        </tr>
        <tr>
            <td><del>ankor-servlet</del></td>
            <td>Deprecated</td>
        </tr>
        <tr>
            <td>ankor-socket-connector</td>
            <td>Messaging via simple socket connections (only for testing, not meant for production).</td>
        </tr>
        <tr>
            <td>ankor-websocket-connector</td>
            <td>Messaging via WebSocket (preferred default messaging implementation for Ankor).</td>
        </tr>
    </tbody>
</table>

## Getting started

### Hello world

TBD

### Tutorials

The best way to get an idea of how applications can be written using Ankor is stepping through the [tutorials](/tutorials).


# General

## What is a Ref?

TBD

## Change Events

TBD

## Action Events

TBD


# Frequently Asked Questions

TBD