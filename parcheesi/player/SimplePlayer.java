package parcheesi.player;

import parcheesi.move.Move;
import parcheesi.die.Die;
import parcheesi.Board;
import parcheesi.Color;
import parcheesi.Pawn;

public class SimplePlayer implements Player {
	Color color;

	// TODO?
	// NOTE: Stub
	public SimplePlayer () { }

	public void startGame(String color) {
		this.color = Color.lookupByColorName(color);
	}

	public Move[] doMove(Board board, int[] dice) {
		// TODO
		return new Move[] {};
	}

	public void DoublesPenalty() {
		// TODO
		// getFurthestPawn().restart();
	}

	public Pawn getFurthestPawn() {
		// TODO
		return new Pawn(3, "blue");
	}

	public Color getColor() {
		return color;
	}
}
