package parcheesi.serializer.xml;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Node<T> {
	private List<T> children = new ArrayList<T>();
	private String name;

	public Node(String name) {
		this.name = name;
	}

	public String toString() {
		String tagEnd = this.name + ">";

		String start = "<" + tagEnd;
		String close = "</" + tagEnd;

		String body = children
			.stream()
			.map(c -> c.toString())
			.collect(Collectors.joining(""));

		return start + body + close;
	}

	public Node child(T... children) {
		this.children.addAll(Arrays.asList(children));
		return this;
	}

	public static class Empty<T> extends Node<T> {
		public Empty(String name) {
			super(name);
		}

		@Override
		public Node child(T... children) throws UnsupportedOperationException {
			throw new UnsupportedOperationException("Cannot add children to empty node.");
		}
	}
}

