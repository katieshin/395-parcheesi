package parcheesi.xml;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XML {
	private static class Node<T> {
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
	}

	private static class EmptyNode<T> extends Node<T> {
		public EmptyNode(String name) {
			super(name);
		}

		@Override
		public Node child(T... children) {
			System.err.println("Hey! You can't add children to this EmptyNode.");
			return this;
		}
	}

	private static Node<String> StartGame() { return new Node<String>("start-game"); }
	private static Node<String> Name()      { return new Node<String>("name"); }

	private static Node<Node> DoMove() { return new Node<Node>("do-move"); }
	private static Node<Node> Board()  { return new Node<Node>("board"); }
	private static Node<Node> Dice()   { return new Node<Node>("dice"); }

	private static EmptyNode DoublesPenalty() { return new EmptyNode("doubles-penalty"); }
	private static EmptyNode Void()           { return new EmptyNode("void"); }

	private static Node<Node> Start()    { return new Node<Node>("start"); }
	private static Node<Node> Home()     { return new Node<Node>("home"); }
	private static Node<Node> Main()     { return new Node<Node>("main"); }
	private static Node<Node> HomeRows() { return new Node<Node>("home-rows"); }
	private static Node<Node> PieceLoc() { return new Node<Node>("piece-loc"); }
	private static Node<Node> Pawn()     { return new Node<Node>("pawn"); }

	private static Node<Integer> Die()  { return new Node<Integer>("die"); }
	private static Node<Integer> Loc()  { return new Node<Integer>("loc"); }
	private static Node<String> Color() { return new Node<String>("color"); }
	private static Node<Integer> Id()   { return new Node<Integer>("id"); }

	public static void main(String[] args) {
		// FIXME: these are tests aren't these tests aren't these
		System.out.println(StartGame().child("red"));
		System.out.println(DoMove().child(
			Board().child(
				Start().child(
					Pawn().child(
						Color().child("red"),
						Id().child(0)
					),
					Pawn().child(
						Color().child("blue"),
						Id().child(0)
					)
				),
				Main().child(
					PieceLoc().child(
						Pawn().child(
							Color().child("red"),
							Id().child(1)
						),
						Loc().child(42)
					)
				),
				HomeRows(),
				Home()
			),
			Dice().child(
				Die().child(5),
				Die().child(1)
			)
		));
		System.out.println(DoublesPenalty().child());
		System.out.println(Void());
	}
}
