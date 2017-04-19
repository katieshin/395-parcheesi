package parcheesi.player;

import parcheesi.pawn.Pawn;
import parcheesi.move.Move;
import parcheesi.die.Die;
import parcheesi.Board;
import parcheesi.Color;

public class SimplePlayer implements Player {
	Color.Player color;

	// TODO?
	// NOTE: Stub
	public SimplePlayer () { }

	public void startGame(String color) {
		this.color = Color.Player.lookupByColorName(color);
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

	public Color.Player getColor() {
		return color;
	}
}
