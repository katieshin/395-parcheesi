package parcheesi.move;

import parcheesi.die.Die;
import parcheesi.Board;
import parcheesi.Pawn;

// represents a move that starts on one of the home rows
public class MoveHome implements Move {
	Pawn pawn;
	int start;
	int distance;

	public MoveHome(Pawn pawn, int start, int distance) {
		this.pawn=pawn;
		this.start=start;
		this.distance=distance;
	}
}
