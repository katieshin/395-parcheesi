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
				.map(Fn::dieToDieNode)
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
							pawnToPawnNode(pawn1, board),
							pawnToPawnNode(pawn2, board),
							pawnToPawnNode(pawn3, board),
							pawnToPawnNode(pawn4, board)
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
							pawnToPawnNode(pawn3, board),
							pawnToPawnNode(pawn4, board)
						),
						Main().child(
							pawnToPieceLoc(pawn1, board),
							pawnToPieceLoc(pawn2, board)
						),
						HomeRows().child(),
						Home().child()
					).toString()
				),
				"Pawns in main should serialize to <main>"
			);

			board.movePawnForward(pawn1, parcheesi.Parameters.Board.maxPawnTravelDistance - 2);

			String player2color = parcheesi.Color.forPlayer(1).getColorName();
			Pawn player2pawn1 = new Pawn(0, player2color);
			Pawn player2pawn2 = new Pawn(1, player2color);
			Pawn player2pawn3 = new Pawn(2, player2color);
			Pawn player2pawn4 = new Pawn(3, player2color);

			Pawn[] pawns2 = {
				player2pawn1,
				player2pawn2,
				player2pawn3,
				player2pawn4,
				pawn1,
				pawn2,
				pawn3,
				pawn4
			};

			board.addPawn(player2pawn1);
			board.addPawn(player2pawn2);

			board.movePawnForward(player2pawn2, parcheesi.Parameters.Board.maxPawnTravelDistance - 2 - 4);

			check(
				serializer.serialize(pawns2, board).toString().equals(
					Board().child(
						Start().child(
							pawnToPawnNode(player2pawn3, board),
							pawnToPawnNode(player2pawn4, board),
							pawnToPawnNode(pawn3, board),
							pawnToPawnNode(pawn4, board)
						),
						Main().child(
							pawnToPieceLoc(player2pawn1, board),
							pawnToPieceLoc(pawn2, board)
						),
						HomeRows().child(
							pawnToPieceLoc(player2pawn2, board),
							pawnToPieceLoc(pawn1, board)
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
							pawnToPawnNode(pawn3, board),
							pawnToPawnNode(pawn4, board)
						),
						Main().child(
							pawnToPieceLoc(pawn2, board)
						),
						HomeRows().child(),
						Home().child(
							pawnToPawnNode(pawn1, board)
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
						diceList.stream().map(Fn::dieToDieNode).toArray(Node[]::new)
					).toString()
				),
				"Dice array should serialize to <dice>"
			);

			summarize();
		}
	}
}
