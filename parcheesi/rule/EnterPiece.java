package parcheesi.rule;

import parcheesi.move.Move;
import parcheesi.die.Die;
import parcheesi.Board;
import parcheesi.Pawn;
import parcheesi.Player;

import parcheesi.test.Tester;

public class EnterPiece implements Rule {
	static class ADieIsFive implements Rule {
		public boolean enforce(Die[] dice, Player player, Board before, Move[] moves, Board after) {
			for (Die d : dice) {
				if (d.getValue() == 5) return true;
			}
			return false;
		}

		// NOTE: Singleton.
		public ADieIsFive () { }
		public static ADieIsFive instance = new ADieIsFive();
	}

	static class DiceSumToFive implements Rule {
		public boolean enforce(Die[] dice, Player player, Board before, Move[] moves, Board after) {
			int sum = 0;
			for (Die d : dice) {
				sum += d.getValue();
			}
			return sum == 5;
		}

		// NOTE: Singleton.
		public DiceSumToFive () { }
		public static DiceSumToFive instance = new DiceSumToFive();
	}

	// NOTE: Either/or relationship.
	public boolean enforce(Die[] dice, Player player, Board before, Move[] moves, Board after) {
		boolean anyMoveIsEnterPiece = false;
		for (Move m : moves) {
			anyMoveIsEnterPiece |= (m instanceof parcheesi.move.EnterPiece);
		}

		if (!anyMoveIsEnterPiece) return true; // No need to apply these rules.

		return ADieIsFive.instance.enforce(dice, player, before, moves, after)
			|| DiceSumToFive.instance.enforce(dice, player, before, moves, after);
	}

	// NOTE: Singleton.
	public EnterPiece () { }
	public static EnterPiece instance = new EnterPiece();

	public static void main (String[] args) throws Die.InvalidDieException {
		new EnterPieceTester();
	}

	static class EnterPieceTester extends Tester {
		public EnterPieceTester () throws Die.InvalidDieException {
			/* NOTE: Don't worry about cases other rules will catch. E.g.; entry square is
			 * already full. That will be caught by MaximumSquareOccupancy.
			 */
			// Setup for all tests.
			Player player = new parcheesi.StubPlayer();
			parcheesi.Pawn p = new parcheesi.Pawn(1, "blue");

			parcheesi.move.EnterPiece enterMove = new parcheesi.move.EnterPiece(p);
			Move[] moves = new Move[] { enterMove };
			Die[] dice = new parcheesi.die.NormalDie[] {
				new parcheesi.die.NormalDie(5),
				new parcheesi.die.NormalDie(4)
			};

			check(
				ADieIsFive.instance.enforce(dice, player, new Board(), moves, new Board()),
				"ADieIsFive success check."
			);

			dice = new parcheesi.die.NormalDie[] {
				new parcheesi.die.NormalDie(4),
				new parcheesi.die.NormalDie(1)
			};
			check(
				!ADieIsFive.instance.enforce(dice, player, new Board(), moves, new Board()),
				"ADieIsFive fail check."
			);

			check(
				DiceSumToFive.instance.enforce(dice, player, new Board(), moves, new Board()),
				"DiceSumToFive success check."
			);

			dice = new parcheesi.die.NormalDie[] {
				new parcheesi.die.NormalDie(4),
				new parcheesi.die.NormalDie(4)
			};
			check(
				!DiceSumToFive.instance.enforce(dice, player, new Board(), moves, new Board()),
				"DiceSumToFive fail check."
			);

			// Integration tests.
			dice = new parcheesi.die.NormalDie[] {
				new parcheesi.die.NormalDie(4),
				new parcheesi.die.NormalDie(1)
			};
			check(
				EnterPiece.instance.enforce(dice, player, new Board(), moves, new Board()),
				"Integration test: Dice sum to 5 is valid for EnterPiece."
			);

			dice = new parcheesi.die.NormalDie[] {
				new parcheesi.die.NormalDie(5),
				new parcheesi.die.NormalDie(1)
			};
			check(
				EnterPiece.instance.enforce(dice, player, new Board(), moves, new Board()),
				"Integration test: Any die is 5 is valid for EnterPiece."
			);

			dice = new parcheesi.die.NormalDie[] {
				new parcheesi.die.NormalDie(3),
				new parcheesi.die.NormalDie(1)
			};
			check(
				!EnterPiece.instance.enforce(dice, player, new Board(), moves, new Board()),
				"Integration test: Dice where neither die is 5 and sum is not 5 are invalid for"
				+ " EnterPiece."
			);

			parcheesi.move.MoveMain move4 = new parcheesi.move.MoveMain(p, 0, 4);
			moves = new Move[] { move4 };
			check(
				EnterPiece.instance.enforce(dice, player, new Board(), moves, new Board()),
				"Integration test: if no move is an EnterPiece move, then bypass tests."
				/* (That is to say, if no rule in this rule set applies, assume that this rule set
				 *  is satisfied because none of its rules can possibly be broken -- they don't
				 *  matter in this case.)
				 */
			);

			summarize();
		}
	}
}
