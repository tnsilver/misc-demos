module messaging.server {

	requires org.slf4j;
	requires messaging.util;
	requires messaging.protos;
	requires transitive jeromq;

	exports messaging.server;

}
