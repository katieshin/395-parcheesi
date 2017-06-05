package parcheesi.player;

import java.util.ArrayList;
import java.util.List;

import parcheesi.die.NormalDie;
import parcheesi.pawn.Pawn;
import parcheesi.turn.Turn;
import parcheesi.move.Move;
import parcheesi.die.Die;
import parcheesi.Board;
import parcheesi.Color;

import static parcheesi.Parameters.pawnsPerPlayer;

public class SimplePlayer implements Player {
	Color.Player color;
	List<Pawn> pawns;

	public void startGame(String color) {
		this.color = Color.Player.lookupByColorName(color);

		this.pawns = new ArrayList<Pawn>(pawnsPerPlayer);
		for (int pi = 0; pi < pawnsPerPlayer; pi++) {
			this.pawns.add(new Pawn(pi, color));
		}
	}

	public Move[] doMove(Board board, int[] dieValues) {
		// TODO
		List<Die> dice = new ArrayList<Die>(dieValues.length);
		for (int dieValue : dieValues) {
			dice.add(new NormalDie(dieValue));
		}

		Turn myTurn = new Turn(this, board, dice);

		return new Move[] {};
	}

	public void DoublesPenalty() {
		// TODO
		// getFurthestPawn().restart();
	}

	public List<Pawn> pawns() {
		return pawns;
	}
}
