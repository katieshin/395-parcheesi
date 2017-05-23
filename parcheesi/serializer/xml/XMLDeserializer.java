package parcheesi.serializer.xml;

import parcheesi.serializer.Deserializer;
import parcheesi.serializer.xml.Node;

public interface XMLDeserializer<T> extends Deserializer<Node> {
	public Node<T> parse(String serializedXML);
}
