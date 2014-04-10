[TOC]

# Overview

## Switchboard

The Switchboard is the central point for connecting shared models and handling the message sending
between connected models. It does not matter where connected models do reside. Models can not only be
connected (i.e. shared) between client and server, but also between two servers. It is even possible
to connect two models that reside on the same server instance.

### How a client connects to a server model

![Outgoing Socket Connection](/static/images/spidoc/ClientSocketConnect.png)

### How a server receives a client connection request

![Incoming Socket Connection](/static/images/spidoc/ServerSocketConnect.png)
