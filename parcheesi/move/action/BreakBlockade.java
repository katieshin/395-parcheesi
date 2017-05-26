package parcheesi.move.action;

import java.util.List;

import parcheesi.move.Move;
import parcheesi.pawn.Pawn;
import parcheesi.die.Die;
import parcheesi.Board;

public class BreakBlockade extends MoveForward {
	public boolean isApplicable(Class<? extends Move> MoveClass, Die die, Pawn pawn, Board board) {
		if (!super.isApplicable(MoveClass, die, pawn, board)) {
			return false;
		}

		List<Pawn> pawns = board.getPawnsAtCoordinate(board.getPawnCoordinate(pawn));
		boolean currentlyInBlockade = pawns.stream().anyMatch(p -> pawn.color.equals(p.color));

		return currentlyInBlockade;
	}

	public boolean apply(Die die, Pawn pawn, Board board) {
		// TODO: Remove a blockade from a data structure.
		return false;
	}

	public static void main(String[] args) {
	}
}
