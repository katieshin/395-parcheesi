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

	public static void main(String[] args) {
		new NodeTester();
	}

	private static class NodeTester extends parcheesi.test.Tester {
		private Node<Integer> ExampleIntegerNode() { return new Node<Integer>("example-integer"); }
		private Node<String> ExampleStringNode() { return new Node<String>("example-string"); }
		private Node<Boolean> ExampleBooleanNode() { return new Node<Boolean>("example-boolean"); }
		private Node<Node> ExampleRecursiveNode() { return new Node<Node>("example-recursive"); }
		private Node.Empty ExampleEmptyNode() { return new Node.Empty("example-empty"); }

		public NodeTester() {
			check(
				ExampleIntegerNode().child(5).toString()
					.equals("<example-integer>5</example-integer>"),
				"Example integer node serializes correctly"
			);

			check(
				ExampleBooleanNode().child(true).toString()
					.equals("<example-boolean>true</example-boolean>"),
				"Example boolean node serializes correctly"
			);

			check(
				ExampleStringNode().child("hello").toString()
					.equals("<example-string>hello</example-string>"),
				"Example string node serializes correctly"
			);

			check(
				ExampleRecursiveNode().child(
					ExampleIntegerNode().child(11),
					ExampleBooleanNode().child(false),
					ExampleStringNode().child("potato")
				).toString()
					.equals(
						"<example-recursive>"
							+ "<example-integer>11</example-integer>"
							+ "<example-boolean>false</example-boolean>"
							+ "<example-string>potato</example-string>"
						+ "</example-recursive>"
					),
				"Example recursive node serializes correctly"
			);

			check(
				ExampleRecursiveNode().child(
					ExampleRecursiveNode().child(
						ExampleRecursiveNode().child(
							ExampleStringNode().child("deep, huh?")
						)
					)
				).toString()
					.equals(
						"<example-recursive>"
							+ "<example-recursive>"
								+ "<example-recursive>"
									+ "<example-string>deep, huh?</example-string>"
								+ "</example-recursive>"
							+ "</example-recursive>"
						+ "</example-recursive>"
					),
				"Nested example recursive nodes serialize correctly"
			);

			check(
				ExampleEmptyNode().toString()
					.equals("<example-empty></example-empty>"),
				"Example empty node serializes correctly"
			);

			boolean exceptionCaught = false;
			try {
				ExampleEmptyNode().child("something");
			} catch (UnsupportedOperationException ex) {
				exceptionCaught = true;
			}

			check(
				exceptionCaught,
				"Trying to add a child to an empty node raises UnsupportedOperationException"
			);

			summarize();
		}
	}
}

