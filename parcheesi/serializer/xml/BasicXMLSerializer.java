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
		Node startNode    = Start();
		Node mainNode     = Main();
		Node homeRowsNode = HomeRows();
		Node homeNode     = Home();

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
				.map(d -> Die().child(d.getValue()))
				.toArray(Node[]::new)
		);
	}

	public static void main(String[] args) throws Die.InvalidDieException {
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

		board.movePawnForward(pawn, parcheesi.Parameters.Board.maxPawnTravelDistance - 1);

		System.out.println(serializer.serialize(pawns, board));

		Die[] dice = {
			new parcheesi.die.NormalDie(1),
			new parcheesi.die.NormalDie(5)
		};

		System.out.println();
		System.out.println(serializer.serialize(dice));

		try {
			Void().child("can't do this");
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
