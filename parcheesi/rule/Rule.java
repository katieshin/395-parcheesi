package parcheesi.rule;

import parcheesi.move.Move;
import parcheesi.Board;
import parcheesi.Die;

public interface Rule {
	public boolean enforce (Move[] moves, Die[] dice, Board before, Board after);
}
