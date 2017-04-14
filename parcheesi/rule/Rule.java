package parcheesi.rule;

import parcheesi.player.Player;
import parcheesi.move.Move;
import parcheesi.die.Die;
import parcheesi.Board;

public interface Rule {
	// Order of arguments indicates before/after state change.
	// player, dice, and before are as-they-were before calling player.doMove(...).
	// moves is the stored result of player.doMove(...), and after is the resulting board.
	public boolean enforce (Die[] dice, Player player, Board before, Move[] moves, Board after);
}

// public class Home extends LogicalAndRuleset {
// 	/* NOTE: Rule is made redundant by other rules.
// 	 * public class MustEnterByExactCount implements Rule {
// 	 *	public boolean enforce (Move[] moves, Die[] dice, Board before, Board after) {
// 	 *		return false;
// 	 *	}
// 	 * }
// 	 */

// 	public class MustUseHomeBonus implements Rule {
// 		public boolean enforce (Move[] moves, Die[] dice, Board before, Board after) {
// 			return false;
// 		}
// 	}

// 	// NOTE: All must be true relationship.
// 	public boolean enforce (Move[] moves, Die[] dice, Board before, Board after) {
// 		return MustUseHomeBonus(moves, dice, before, after);
// 	}
// }

// public class UseAllDice implements Rule {
// 	public boolean enforce (Move[] moves, Die[] dice, Board before, Board after) {
// 		return false;
// 	}
// }

// public class NoSplittingDie implements Rule {
// 	public boolean enforce (Move[] moves, Die[] dice, Board before, Board after) {
// 		return false;
// 	}
// }

// public class MoveCounterClockwise implements Rule {
// 	public boolean enforce (Move[] moves, Die[] dice, Board before, Board after) {
// 		return false;
// 	}
// }

// public class CannotMovePastBlockade implements Rule {
// 	public boolean enforce (Move[] moves, Die[] dice, Board before, Board after) {
// 		return false;
// 	}
// }

// public class CannotEnterOpponentHomeRow implements Rule {
// 	public boolean enforce (Move[] moves, Die[] dice, Board before, Board after) {
// 		return false;
// 	}
// }

// public class MaximumSquareOccupancy implements Rule {
// 	public boolean enforce (Move[] moves, Die[] dice, Board before, Board after) {
// 		return false;
// 	}
// }
