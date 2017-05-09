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

	/* NOTE: I solemnly swear to the Java compiler that any RuntimeExceptions are heretofore my own
	 * damn fault. Because lo, though I walk through the dark'ning cavern of this hell of generics,
	 * no type may I hold, for the Gods saw fit to erase them...
	 */
	@SuppressWarnings("unchecked")
	public Node<T> child(T... children) {
		for (T child : children) {
			this.child(child);
		}
		return this;
	}

	public Node<T> child(T child) {
		this.children.add(child);
		return this;
	}

	public static class Empty<T> extends Node<T> {
		public Empty(String name) {
			super(name);
		}

		@Override
		public Node<T> child(Object child) throws UnsupportedOperationException {
			throw new UnsupportedOperationException("Cannot add child to empty node.");
		}
	}
}

