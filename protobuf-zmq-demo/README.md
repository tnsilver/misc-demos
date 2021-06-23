# Protobuf over ØMQ (ZeroMQ) Publish/Subscribe Demo

*	This is a Java 14 (**minimum required java version 8** ), com.google.protobuf and org.zeromq.jeromq project built on Maven.


*	This project demonstrates a ØMQ Publisher which constantly generates random protobuf messages and packs them over the wire via a ØMQ socket.


*	The protobuf messages were pre-compiled by __protoc__ compiler from __.proto__ files


*	A ØMQ Subscriber receives the messages and unpacks them to pretty print them as JSON via a logger to the console.

### Loading the project to Eclipse ###

*	 to load the project into eclipse - first build it from command line:


> *	protobuf-zmq-demo$ mvn package


*	Use eclipse File->Import->Existing maven projects to import protobuf-zmq-demo into the package/project explorer


*	In the  __proto__  module add **target/generated-test-sources/protobuf/java**  and **target/generated-sources/protobuf/java**  as source folders


*	Use Project->clean to rebuild the entire project

### Quick Run ###

*	 to run the demo without having to install anything to the local maven repository, navigate to the root of this project and run one of the following commands:


> *	protobuf-zmq-demo$ mvn clean verify


> *	protobuf-zmq-demo$ mvn clean package

Demo
> *	protobuf-zmq-demo$ mvn clean install

### Modules in this project ###

*	[messaging.util](messaging.util) - A module which contains various utility classes and constants for the demo


*	This module also serves as a library, shared between the other project modules, and it includes a number of common utilites to read


*	to compile the __util__ module, cd into the util directory and run:


> *	messaging.util$ mvn clean compile

*	to run the tests and generate the reports, as well as compile the __messaging.util__ module run:

> *	messaging.util$ mvn clean surefire-report:report site

*	alternatively run:

> *	messaging.util$ mvn clean verify

*	to individually build the subsequent modules that depend on module __messaging.util__ it must be installed in the local maven repository. To install it run:

> *	messaging.util$ mvn clean install

> *	or, if you won't clean any of the module directories again:

> *	messaging.util$ mvn clean package


*	[messaging.protos](messaging.protos) - The module which contains the __.proto__ files and is responsible for compiling them into protobuf classes.


*	This module also serves as a library, shared between the other projct's modules, and it includes a number of common utilites to read


*	to compile the __messaging.protos__ cd into the messaging.protos directory and run:

> *	messaging.protos$ mvn clean compile


*	to run the tests and generate the reports, as well as compile the __messaging.protos__ run:


> *	messaging.protos$ mvn clean surefire-report:report site

*	alternatively run:

> *	messaging.protos$ mvn clean verify


*	to individually build the subsequent modules that depend on module __messaging.protos__ it must be installed in the local maven repository. To install it run:

> *	messaging.protos$ mvn clean install

> *	or, if you won't clean any of the module directories again:

> *	messaging.protos$ mvn clean package


*	[messaging.server](messaging.server) - The module which contains the ØMQ Publisher


*	to individually build the subsequent modules that depend on module __messaging.server__ it must be installed in the local maven repository.


*	To install it run the following command from the module root directory __messaging.server__:


> *	messaging.server$ mvn clean install

> *	or, if you won't clean any of the module directories again:

> *	messaging.server$ mvn package


*	[messaging.broker](messaging.broker) - The module which contains the ØMQ Broker, Proxy and various ØMQ features


*	to individually build the subsequent modules that depend on module __messaging.broker__ it must be installed in the local maven repository.


*	To install it run the following command from the module root directory __messaging.broker__:


> *	messaging.broker$ mvn clean install

> *	or, if you won't clean any of the module directories again:

> *	messaging.broker$ mvn package


* [messaging.client](messaging.client) - The module which contains the ØMQ Subscriber


*	to individually build the subsequent modules that depend on module __messaging.client__ it must be installed in the local maven repository.


*	To install it run the following command from the module root directory __messaging.client__:


> *	messaging.client$ mvn clean install

> *	or, if you won't clean any of the module directories again:

> *	messaging.client$ mvn package

* [messaging.demo](messaging.demo) - The module which contains the demo, consisting of the Publisher and Subscriber working together. To build this module and run the demo:
Demo

> *	messaging.demo$ mvn clean install

> *	or

> *	messaging.demo$ mvn package


## Credits

![T.N.Silverman](../.images/me.jpg?raw=true "T.N.Silverman")

All java code, property files, Java Scripts, JSP's and HTML templates created by [T.N. Silverman](https://github.com/tnsilver "About T.N.Silverman")

##### Copyright (C) 2014 - 2021 T.N.Silverman, All rights reserved.



## License


[Licensed under the Apache License, Version 2.0](LICENSE "Apache License")

