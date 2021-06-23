#  The **messaging.util**  Module
---

# Protobuf over ØMQ (ZeroMQ) Publish/Subscribe Demo

*	This is a Java 14 (**minimum required java version 8** ), com.google.protobuf and org.zeromq.jeromq project built on Maven.


*	This project demonstrates a ØMQ Publisher which constantly generates random protobuf messages and packs them over the wire via a ØMQ socket.


*	The protobuf messages were pre-compiled by __protoc__ compiler from __.proto__ files


*	A ØMQ Subscriber receives the messages and unpacks them to pretty print them as JSON via a logger to the console.

### about this module ###

*	[messaging.util](.) - A module which contains various utility classes and constants for the demo


*	This module also serves as a library, shared between the other project modules, and it includes a number of common utilites to read


*	to compile the __messaging.util__ module, cd into the messaging.util directory and run:


> *	messaging.util$ mvn clean compile

*	to run the tests and generate the reports, as well as compile the __messaging.util__ module run:

> *	messaging.util$ mvn clean surefire-report:report site

*	alternatively run:

> *	messaging.util$ mvn clean verify

*	to individually build the subsequent modules that depend on module __messaging.util__ it must be installed in the local maven repository. To install it run:

> *	messaging.util$ mvn clean install

> *	or, if you won't clean any of the module directories again:

> *	messaging.util$ mvn clean package



### Copyright (C) 2014 - 2020 T.Silverman, All rights reserved. ###

