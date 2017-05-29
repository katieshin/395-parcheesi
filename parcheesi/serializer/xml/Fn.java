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
			Color().child(pawn.color.getColorName()),
			Id().child(pawn.id)
		);
	}

	static Node<Node> pawnToPieceLoc(Pawn pawn, Board board) {
		// FIXME: re-indexing is hard-coded here; should be defined by message format?
		int playerIndex = pawn.playerIndex;
		int dimensionOffset = ((playerIndex + 2) % 4) * mainRingSizePerDimension;
		int localOffset = (spacesPerRow / 2) + 1;
		int totalOffset = dimensionOffset + localOffset;

		int distance = board.pawnDistance(pawn);

		int newIndex;

		if (distance > pawnMainRingDistance) {
			// NOTE: in home row
			newIndex = distance - pawnMainRingDistance - 1;
		} else {
			// NOTE: in main
			newIndex = (distance + totalOffset) % (pawnMainRingDistance + spacesPerRow/2);
		}

		return PieceLoc().child(
			pawnToPawnNode(pawn, board),
			Loc().child(newIndex)
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
