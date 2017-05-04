package parcheesi.move.action;

import parcheesi.move.Move;
import parcheesi.pawn.Pawn;
import parcheesi.die.Die;
import parcheesi.Board;

public class FormBlockade extends MoveAction {
	@Override
	boolean isApplicable(Class<? extends Move> MoveClass, Board board, Die die, Pawn pawn) {
		if (!super.isApplicable(MoveClass, board, die, pawn)) {
			return false;
		}

		// TODO: True if there's a pawn of the same color on the board location you will land on.
		return false;
	}

	public static void main(String[] args) {
	}
}
