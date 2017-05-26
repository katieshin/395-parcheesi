package parcheesi.serializer.xml;

import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Function;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import parcheesi.pawn.Pawn;
import parcheesi.die.Die;
import parcheesi.Board;

import static parcheesi.serializer.xml.Element.*;
import static parcheesi.Parameters.Board.*;

class Fn {
	static Node<Integer> dieToDieNode(Die d) {
		return Die().child(d.getValue());
	}

	static Node<Node> pawnToPawnNode(Pawn pawn, Board board) {
		return Pawn().child(
			Color().child(parcheesi.Color.Player.lookupByColorName(pawn.color)),
			Id().child(pawn.id)
		);
	}

	static Node<Node> pawnToPieceLoc(Pawn pawn, Board board) {
		return PieceLoc().child(
			pawnToPawnNode(pawn, board),
			Loc().child(
				// FIXME: re-indexing is hard-coded here; should be defined by message format?
				board.pawnDistance(pawn) + (spacesPerRow / 2)
			)
		);
	}

	static Function<Pawn, Node<Node>> withBoard(Board board, BiFunction<Pawn, Board, Node<Node>> action) {
		return pawn -> action.apply(pawn, board);
	}

	static Consumer<Pawn> pawnToChildMapper(Node<Node> parent, Function<Pawn, Node<Node>> action) {
		return pawn -> parent.child(action.apply(pawn));
	}

	static Map<Predicate<Pawn>, Consumer<Pawn>> nodeOperations(
			Board board,
			Node<Node> startNode,
			Node<Node> mainNode,
			Node<Node> homeRowsNode,
			Node<Node> homeNode
		) {
			return new HashMap<Predicate<Pawn>, Consumer<Pawn>>() {{
				put(
					board::inHomeRow,
					pawnToChildMapper(homeRowsNode, withBoard(board, Fn::pawnToPieceLoc))
				);
				put(
					board::inStart,
					pawnToChildMapper(startNode, withBoard(board, Fn::pawnToPawnNode))
				);
				put(
					board::inHome,
					pawnToChildMapper(homeNode, withBoard(board, Fn::pawnToPawnNode))
				);
				put(
					board::inMain,
					pawnToChildMapper(mainNode, withBoard(board, Fn::pawnToPieceLoc))
				);
			}};
	}

	// TODO: tests.
}
