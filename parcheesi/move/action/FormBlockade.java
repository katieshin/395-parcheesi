package parcheesi.move.action;

import java.util.List;

import parcheesi.move.Move;
import parcheesi.pawn.Pawn;
import parcheesi.die.Die;
import parcheesi.Board;

public class FormBlockade extends MoveForward {
	public boolean isApplicable(Class<? extends Move> MoveClass, Die die, Pawn pawn, Board board) {
		if (!super.isApplicable(MoveClass, die, pawn, board)) {
			return false;
		}

		Board testBoard = new Board(board);
		testBoard.movePawnForward(pawn, die.getValue());
		List<Pawn> pawns = testBoard.getPawnsAtCoordinate(testBoard.getPawnCoordinate(pawn));
		boolean pawnAtTargetHasSameColor = pawns.stream().anyMatch(p -> pawn.color.equals(p.color));

		return pawnAtTargetHasSameColor;
	}

	public boolean apply(Die die, Pawn pawn, Board board) {
		// TODO: Add a blockade to a data structure.
		return false;
	}

	public static void main(String[] args) {
	}
}
