package parcheesi.serializer.xml;

import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Function;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import parcheesi.pawn.PawnWhisperer;
import parcheesi.pawn.Pawn;
import parcheesi.die.Die;
import parcheesi.Board;

import static parcheesi.serializer.xml.Element.*;
import static parcheesi.Parameters.Board.*;

class Fn {
	static Function<Die, Node<Integer>>
		dieToDieNode =
			d -> Die().child(d.getValue());

	static BiFunction<Pawn, Board, Node<Node>>
		pawnToPawnNode =
			(pawn, board) ->
				Pawn().child(
					Color().child(PawnWhisperer.color(pawn).getColorName()),
					Id().child(PawnWhisperer.id(pawn))
				);

	static BiFunction<Pawn, Board, Node<Node>>
		pawnToPieceLoc =
			(pawn, board) ->
				PieceLoc().child(
					pawnToPawnNode.apply(pawn, board),
					Loc().child(
						board.pawnDistance(pawn) + (spacesPerRow / 2)
					)
				);

	static BiFunction<Board, BiFunction<Pawn, Board, Node<Node>>, Function<Pawn, Node<Node>>>
		withBoard =
			(board, action) ->
				pawn -> action.apply(pawn, board);

	static BiFunction<Node<Node>, Function<Pawn, Node<Node>>, Consumer<Pawn>>
		childMapper =
			(node, action) ->
				pawn -> node.child(action.apply(pawn));

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
					childMapper.apply(homeRowsNode, withBoard.apply(board, pawnToPieceLoc))
				);
				put(
					board::inStart,
					childMapper.apply(startNode, withBoard.apply(board, pawnToPawnNode))
				);
				put(
					board::inHome,
					childMapper.apply(homeNode, withBoard.apply(board, pawnToPawnNode))
				);
				put(
					board::inMain,
					childMapper.apply(mainNode, withBoard.apply(board, pawnToPieceLoc))
				);
			}};
	}
}
