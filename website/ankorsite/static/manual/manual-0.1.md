# Introduction

## What is Ankor?

Ankor is a framework that helps sharing logical object structures (models) and events (actions and changes)
between a client and a server. Ankor's goal is to do this in a transparent way, so that the developer does
not even have to bother with the server-client communication at all.

Our mission statement:
> Bind your view to the model on the client-side, add behaviour to the model on the server-side
> - and let Ankor do the rest.

## What Ankor is **not**

Ankor does not re-invent the wheel. Therefore:

* Ankor is **not** yet another fancy UI widget library.

    Use your favorite well-established client framework
    like *Dojo Toolkit*, *React*, *jQuery UI* for browser-based clients, or *JavaFX* for your Java-based client.
    However, Ankor makes using these UI libraries much simpler by helping to
    keep the necessary client code to a minimum.

* Ankor is **not** a new network transport protocol.

    Ankor itself makes use of popular protocols like *HTTP* or *WebSocket*. In fact Ankor can be used with virtually
    any network protocol that supports relyable message transport. Planned protocols for future versions of Ankor are
    *JMS* and *RabbitMQ*.

* Ankor is **not** a code generation tool.

    We believe that you as the developer are the master of your code. Ankor helps to develop your presentation
    model in a natural and descriptive way. There is no need to generate code or use templates.

    (Well: depending on your client technology you might actually be forced to use code generation and templating,
    but this is out of scope for Ankor)
