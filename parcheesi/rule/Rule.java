package parcheesi.rule;

import parcheesi.move.Move;
import parcheesi.die.Die;
import parcheesi.Board;
import parcheesi.Player;

public interface Rule {
	// Order of arguments indicates before/after state change.
	// player, dice, and before are as-they-were before calling player.doMove(...).
	// moves is the stored result of player.doMove(...), and after is the resulting board.
	public boolean enforce (Die[] dice, Player player, Board before, Move[] moves, Board after);
}
