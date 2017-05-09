package parcheesi.serializer;

import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Function;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


import parcheesi.pawn.PawnWhisperer;
import parcheesi.pawn.Pawn;
import parcheesi.Board;
import parcheesi.die.Die;

import static parcheesi.Parameters.Board.*;

public class BasicXMLSerializer implements Serializer {
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

	private static BiFunction<Pawn, Board, Node>
		pawnToPawnNode =
			(pawn, board) ->
				Pawn().child(
					Color().child(PawnWhisperer.color(pawn).getColorName()),
					Id().child(PawnWhisperer.id(pawn))
				);

	private static BiFunction<Pawn, Board, Node>
		pawnToPieceLoc =
			(pawn, board) ->
				PieceLoc().child(
					pawnToPawnNode.apply(pawn, board),
					Loc().child(
						board.pawnDistance(pawn) + (spacesPerRow / 2)
					)
				);

	private static BiFunction<Board, BiFunction<Pawn, Board, Node>, Consumer<Pawn>>
		withBoard =
			(board, action) ->
				pawn -> action.apply(pawn, board);

	private static BiFunction<Node, BiFunction<Pawn, Board, Node>, BiFunction<Pawn, Board, Node>>
		childMapper =
			(node, action) ->
				(pawn, board) -> node.child(action.apply(pawn, board));

	private static Map<Predicate<Pawn>, Consumer<Pawn>>
		nodeOperations(Board board, Node startNode, Node mainNode, Node homeRowsNode, Node homeNode) {
			return new HashMap<Predicate<Pawn>, Consumer<Pawn>>() {{
				put(
					board::inHomeRow,
					withBoard.apply(board, childMapper.apply(homeRowsNode, pawnToPieceLoc))
				);
				put(
					board::inStart,
					withBoard.apply(board, childMapper.apply(startNode, pawnToPawnNode))
				);
				put(
					board::inHome,
					withBoard.apply(board, childMapper.apply(homeNode, pawnToPawnNode))
				);
				put(
					board::inMain,
					withBoard.apply(board, childMapper.apply(mainNode, pawnToPieceLoc))
				);
			}};
	}

	public Node serialize(Pawn[] pawns, Board board) {
		Node startNode    = Start();
		Node mainNode     = Main();
		Node homeRowsNode = HomeRows();
		Node homeNode     = Home();

		nodeOperations(board, startNode, mainNode, homeRowsNode, homeNode)
			.forEach((predicate, action) ->
				Arrays.asList(pawns)
					.stream()
					.filter(predicate)
					.forEach(action)
			);

		return Board().child(startNode, mainNode, homeRowsNode, homeNode);
	}

	public Node serialize(Die[] dice) {
		return Dice().child(
			Arrays.asList(dice).stream().map(d -> Die().child(d.getValue())).toArray(Node[]::new)
		);
	}

	public static void main(String[] args) {
		// FIXME: these are tests aren't these tests aren't these
		Board board = new Board();

		Serializer serializer = new BasicXMLSerializer();

		String color = parcheesi.Color.forPlayer(0).getColorName();

		Pawn pawn = new Pawn(0, color);

		Pawn[] pawns = {
			pawn,
			new Pawn(1, color),
			new Pawn(2, color),
			new Pawn(3, color)
		};

		board.addPawn(pawn);
		System.out.println(serializer.serialize(pawns, board));
	}
}
