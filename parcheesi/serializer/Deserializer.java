package parcheesi.serializer;

public interface Deserializer<T> {
	public T parse(String serializedData);
}
