#  The **messaging.demo**  Module
---


# Protobuf over ZeroMQ Publish/Subscribe Demo

*	This is a Java 14 (**minimum required java version 8** ), com.google.protobuf and org.zeromq.jeromq project built on Maven.


*	This project demonstrates a ØMQ Publisher which constantly generates random protobuf messages and packs them over the wire via a ØMQ socket.


*	The protobuf messages were pre-compiled by __protoc__ compiler from __.proto__ files


*	A ØMQ Subscriber receives the messages and unpacks them to pretty print them as JSON via a logger to the console.

### about this module ###

* [messaging.demo](.) - The module which contains the demo, consisting of the Publisher and Subscriber working together. To build this module and run the demo:


> *	messaging.demo$ mvn clean install

> *	or

> *	messaging.demo$ mvn package


### Copyright (C) 2014 - 2020 T.Silverman, All rights reserved. ###

