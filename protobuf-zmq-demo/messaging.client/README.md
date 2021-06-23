#  The **messaging.client** Module
---


# Protobuf over ZeroMQ Publish/Subscribe Demo

*	This is a Java 14 (**minimum required java version 8** ), com.google.protobuf and org.zeromq.jeromq project built on Maven.


*	This project demonstrates a ØMQ Publisher which constantly generates random protobuf messages and packs them over the wire via a ØMQ socket.


*	The protobuf messages were pre-compiled by __protoc__ compiler from __.proto__ files


*	A ØMQ Subscriber receives the messages and unpacks them to pretty print them as JSON via a logger to the console.

### about this module ###

* [messaging.client](.) - The module which contains the ØMQ Subscriber


*	to individually build the subsequent modules that depend on module __messaging.client__ it must be installed in the local maven repository.


*	To install it run the following command from the module root directory __messaging.client__:


> *	messaging.client$ mvn clean install

> *	or, if you won't clean any of the module directories again:

> *	messaging.client$ mvn package



### Copyright (C) 2014 - 2020 T.Silverman, All rights reserved. ###

