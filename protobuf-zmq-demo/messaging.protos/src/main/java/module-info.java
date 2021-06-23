module messaging.protos {

	requires org.slf4j;
	requires messaging.util;
	requires transitive com.google.protobuf;
	requires transitive com.google.protobuf.util;

	exports messaging.protos;
	exports messaging.protos.util;

}
