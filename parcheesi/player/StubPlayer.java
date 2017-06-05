package parcheesi.player;

import java.util.List;
import java.util.ArrayList;

import parcheesi.move.Move;
import parcheesi.pawn.Pawn;
import parcheesi.Color;
import parcheesi.Board;

import static parcheesi.Parameters.pawnsPerPlayer;

public class StubPlayer implements Player {
	List<Pawn> pawns;
	int playerIndex;

	public StubPlayer(int index) {
		playerIndex = index;

		pawns = new ArrayList<Pawn>(pawnsPerPlayer);
		for (int pi = 0; pi < pawnsPerPlayer; pi++) {
			pawns.add(new Pawn(pi, Color.forPlayer(playerIndex)));
		}
	}

	public List<Pawn> pawns() {
		return pawns;
	}

	public void startGame(String color) {
		// TODO
		return;
	}

	public Move[] doMove(Board board, int[] dice) {
		// TODO
		return new Move[] { };
	}

	public void DoublesPenalty() {
		// TODO
		// ? Seriously necessary? Seems at first. like an odd implementation choice.
		return;
	}
}
