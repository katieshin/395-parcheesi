package parcheesi.move;

import parcheesi.Pawn;

// represents a move where a player enters a piece
public class EnterPiece implements Move {
	Pawn pawn;

	public EnterPiece(Pawn pawn) {
		this.pawn=pawn;
	}
}
