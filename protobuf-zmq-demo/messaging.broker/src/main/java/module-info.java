module messaging.broker {

	requires org.slf4j;
	requires messaging.util;
	requires messaging.protos;
	requires transitive jeromq;

	exports messaging.broker;

}
