package parcheesi.rule;

import parcheesi.move.Move;
import parcheesi.Board;

public interface Rule {
	public boolean enforce (Move[] moves, int[] dice, Board before, Board after);
}
