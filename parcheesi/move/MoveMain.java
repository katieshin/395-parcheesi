package parcheesi.move;

import parcheesi.die.Die;
import parcheesi.Board;
import parcheesi.Pawn;

// represents a move that starts on the main ring
// (but does not have to end up there)
public class MoveMain extends SmartMove implements Move {
	Pawn pawn;
	int start;
	int distance;

	public MoveMain(Pawn pawn, int start, int distance) {
		this.pawn=pawn;
		this.start=start;
		this.distance=distance;
	}

	public static boolean canMakeMove(Board board, Die[] dice) {
		// TODO
		return false;
	}
}
