package parcheesi.rule;

import parcheesi.move.Move;
import parcheesi.die.Die;
import parcheesi.Board;

public interface Rule {
	public boolean enforce (Move[] moves, Die[] dice, Board before, Board after);
}
