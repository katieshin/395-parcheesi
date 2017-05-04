package parcheesi.move.action;

import parcheesi.move.Move;
import parcheesi.pawn.Pawn;
import parcheesi.die.Die;
import parcheesi.Board;

public interface Action {
	boolean isApplicable(Class<? extends Move> MoveClass, Board board, Die die, Pawn pawn);
	boolean apply(Board board, Die die, Pawn pawn);
}
