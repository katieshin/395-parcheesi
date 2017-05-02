package parcheesi.rule;

import parcheesi.player.Player;
import parcheesi.move.Move;
import parcheesi.die.Die;
import parcheesi.Board;

import parcheesi.test.Tester;

public class EnterPieceRules implements Rule {
	static class ADieIsFive implements Rule {
		public boolean enforce(Die[] dice, Player player, Board before, Move[] moves, Board after) {
			for (Die d : dice) {
				if (d.getValue() == 5) return true;
			}
			return false;
		}

		// NOTE: Singleton.
		public ADieIsFive () { }
		public static ADieIsFive rule = new ADieIsFive();
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
		public static DiceSumToFive rule = new DiceSumToFive();
	}

	// NOTE: Either/or relationship.
	public boolean enforce(Die[] dice, Player player, Board before, Move[] moves, Board after) {
		return ADieIsFive.rule.enforce(dice, player, before, moves, after)
			|| DiceSumToFive.rule.enforce(dice, player, before, moves, after);
	}

	// NOTE: Singleton.
	public EnterPieceRules () { }
	public static rule = new EnterPieceRules();

	public static void main (String[] args) throws Die.InvalidDieException {
		new EnterPieceTester();
	}

	static class EnterPieceTester extends Tester {
		public EnterPieceTester () throws Die.InvalidDieException {
			/* NOTE: Don't worry about cases other rules will catch. E.g.; entry square is
			 * already full. That will be caught by MaximumSquareOccupancy.
			 */
			// Setup for all tests.
			Player player = new parcheesi.player.StubPlayer();
			parcheesi.pawn.Pawn p = new parcheesi.pawn.Pawn(1, "blue");

			parcheesi.move.EnterPiece enterMove = new parcheesi.move.EnterPiece(p);
			Move[] moves = new Move[] { enterMove };
			Die[] dice = new parcheesi.die.NormalDie[] {
				new parcheesi.die.NormalDie(5),
				new parcheesi.die.NormalDie(4)
			};

			check(
				ADieIsFive.rule.enforce(dice, player, new Board(), moves, new Board()),
				"ADieIsFive success check."
			);

			dice = new parcheesi.die.NormalDie[] {
				new parcheesi.die.NormalDie(4),
				new parcheesi.die.NormalDie(1)
			};
			check(
				!ADieIsFive.rule.enforce(dice, player, new Board(), moves, new Board()),
				"ADieIsFive fail check."
			);

			check(
				DiceSumToFive.rule.enforce(dice, player, new Board(), moves, new Board()),
				"DiceSumToFive success check."
			);

			dice = new parcheesi.die.NormalDie[] {
				new parcheesi.die.NormalDie(4),
				new parcheesi.die.NormalDie(4)
			};
			check(
				!DiceSumToFive.rule.enforce(dice, player, new Board(), moves, new Board()),
				"DiceSumToFive fail check."
			);

			// Integration tests.
			dice = new parcheesi.die.NormalDie[] {
				new parcheesi.die.NormalDie(4),
				new parcheesi.die.NormalDie(1)
			};
			check(
				EnterPieceRules.rule.enforce(dice, player, new Board(), moves, new Board()),
				"Integration test: Dice sum to 5 is valid for EnterPiece."
			);

			dice = new parcheesi.die.NormalDie[] {
				new parcheesi.die.NormalDie(5),
				new parcheesi.die.NormalDie(1)
			};
			check(
				EnterPieceRules.rule.enforce(dice, player, new Board(), moves, new Board()),
				"Integration test: Any die is 5 is valid for EnterPiece."
			);

			dice = new parcheesi.die.NormalDie[] {
				new parcheesi.die.NormalDie(3),
				new parcheesi.die.NormalDie(1)
			};
			check(
				!EnterPieceRules.rule.enforce(dice, player, new Board(), moves, new Board()),
				"Integration test: Dice where neither die is 5 and sum is not 5 are invalid for"
				+ " EnterPiece."
			);

			parcheesi.move.MoveMain move4 = new parcheesi.move.MoveMain(p, 0, 4);
			moves = new Move[] { move4 };
			check(
				EnterPieceRules.rule.enforce(dice, player, new Board(), moves, new Board()),
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
