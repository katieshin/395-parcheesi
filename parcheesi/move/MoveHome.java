package parcheesi;

import parcheesi.interfaces.Move;

// represents a move that starts on one of the home rows
class MoveHome implements Move {
	Pawn pawn;
	int start;
	int distance;

	MoveHome(Pawn pawn, int start, int distance) {
		this.pawn=pawn;
		this.start=start;
		this.distance=distance;
	}
}