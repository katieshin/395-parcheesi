package parcheesi.move.action;

import parcheesi.move.Move;
import parcheesi.pawn.Pawn;
import parcheesi.die.Die;
import parcheesi.Board;

public interface Action {
	boolean isApplicable(Class<? extends Move> MoveClass, Die die, Pawn pawn, Board board);
	boolean apply(Die die, Pawn pawn, Board board);
}
