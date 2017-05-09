package parcheesi.serializer.xml;

import java.util.Arrays;
import java.util.List;

import parcheesi.serializer.Serializer;
import parcheesi.pawn.Pawn;
import parcheesi.die.Die;
import parcheesi.Board;

import static parcheesi.serializer.xml.Element.*;
import static parcheesi.serializer.xml.Fn.*;

public class BasicXMLSerializer implements Serializer {
	public Node serialize(Pawn[] pawns, Board board) {
		Node<Node> startNode    = Start();
		Node<Node> mainNode     = Main();
		Node<Node> homeRowsNode = HomeRows();
		Node<Node> homeNode     = Home();

		List<Pawn> pawnsList = Arrays.asList(pawns);

		nodeOperations(board, startNode, mainNode, homeRowsNode, homeNode)
			.forEach((predicate, action) ->
				pawnsList
					.stream()
					.filter(predicate)
					.forEach(action)
			);

		return Board().child(startNode, mainNode, homeRowsNode, homeNode);
	}

	public Node serialize(Die[] dice) {
		return Dice().child(
			Arrays.asList(dice)
				.stream()
				.map(dieToDieNode)
				.toArray(Node[]::new)
		);
	}

	public static void main(String[] args) throws Die.InvalidDieException {
		new BasicXMLSerializerTester();
	}

	private static class BasicXMLSerializerTester extends parcheesi.test.Tester {
		BasicXMLSerializerTester() throws Die.InvalidDieException {
			Serializer serializer = new BasicXMLSerializer();

			Board board = new Board();
			String color = parcheesi.Color.forPlayer(0).getColorName();
			Pawn pawn1 = new Pawn(0, color);
			Pawn pawn2 = new Pawn(1, color);
			Pawn pawn3 = new Pawn(2, color);
			Pawn pawn4 = new Pawn(3, color);

			Pawn[] pawns = { pawn1, pawn2, pawn3, pawn4 };

			check(
				serializer.serialize(pawns, board).toString().equals(
					Board().child(
						Start().child(
							pawnToPawnNode.apply(pawn1, board),
							pawnToPawnNode.apply(pawn2, board),
							pawnToPawnNode.apply(pawn3, board),
							pawnToPawnNode.apply(pawn4, board)
						),
						Main().child(),
						HomeRows().child(),
						Home().child()
					).toString()
				),
				"Pawns in start should serialize to <start>"
			);

			board.addPawn(pawn1);
			board.addPawn(pawn2);

			check(
				serializer.serialize(pawns, board).toString().equals(
					Board().child(
						Start().child(
							pawnToPawnNode.apply(pawn3, board),
							pawnToPawnNode.apply(pawn4, board)
						),
						Main().child(
							pawnToPieceLoc.apply(pawn1, board),
							pawnToPieceLoc.apply(pawn2, board)
						),
						HomeRows().child(),
						Home().child()
					).toString()
				),
				"Pawns in main should serialize to <main>"
			);

			board.movePawnForward(pawn1, parcheesi.Parameters.Board.maxPawnTravelDistance - 2);

			check(
				serializer.serialize(pawns, board).toString().equals(
					Board().child(
						Start().child(
							pawnToPawnNode.apply(pawn3, board),
							pawnToPawnNode.apply(pawn4, board)
						),
						Main().child(
							pawnToPieceLoc.apply(pawn2, board)
						),
						HomeRows().child(
							pawnToPieceLoc.apply(pawn1, board)
						),
						Home().child()
					).toString()
				),
				"Pawns in home row should serialize to <home-rows>"
			);

			board.movePawnForward(pawn1, 1);

			check(
				serializer.serialize(pawns, board).toString().equals(
					Board().child(
						Start().child(
							pawnToPawnNode.apply(pawn3, board),
							pawnToPawnNode.apply(pawn4, board)
						),
						Main().child(
							pawnToPieceLoc.apply(pawn2, board)
						),
						HomeRows().child(),
						Home().child(
							pawnToPawnNode.apply(pawn1, board)
						)
					).toString()
				),
				"Pawns in home should serialize to <home>"
			);

			Die[] dice = {
				new parcheesi.die.NormalDie(1),
				new parcheesi.die.NormalDie(5)
			};

			List<Die> diceList = Arrays.asList(dice);

			check(
				serializer.serialize(dice).toString().equals(
					Dice().child(
						diceList.stream().map(dieToDieNode).toArray(Node[]::new)
					).toString()
				),
				"Dice array should serialize to <dice>"
			);

			boolean ex = false;
			try {
				Void().child("can't do this");
			} catch (UnsupportedOperationException e) {
				ex = true;
			}

			check(ex, "Trying to add a child to an Empty Node throws UnsupportedOperationException");

			summarize();
		}
	}
}
