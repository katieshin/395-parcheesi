package parcheesi.rule;

import parcheesi.move.Move;
import parcheesi.Board;
import parcheesi.Die;
import parcheesi.Pawn;

import parcheesi.test.Tester;

public class EnterPiece implements Rule {
	static class ADieIsFive implements Rule {
		public boolean enforce (Move[] moves, Die[] dice, Board before, Board after) {
			for (Die d : dice) {
				if (d.getValue() == 5) return true;
			}
			return false;
		}

		public ADieIsFive () { }
		public static ADieIsFive instance = new ADieIsFive();
	}

	static class DiceSumToFive implements Rule {
		public boolean enforce (Move[] moves, Die[] dice, Board before, Board after) {
			int sum = 0;
			for (Die d : dice) {
				sum += d.getValue();
			}
			return sum == 5;
		}

		public DiceSumToFive () { }
		public static DiceSumToFive instance = new DiceSumToFive();
	}

	// NOTE: Either/or relationship.
	public boolean enforce(Move[] moves, Die[] dice, Board before, Board after) {
		boolean anyMoveIsEnterPiece = false;
		for (Move m : moves) {
			anyMoveIsEnterPiece |= (m instanceof parcheesi.move.EnterPiece);
		}

		if (!anyMoveIsEnterPiece) return true; // No need to apply these rules.

		return ADieIsFive.instance.enforce(moves, dice, before, after)
			|| DiceSumToFive.instance.enforce(moves, dice, before, after);
	}

	public EnterPiece () { }
	public static EnterPiece instance = new EnterPiece();

	public static void main (String[] args) throws parcheesi.Die.InvalidDieException {
		new EnterPieceTester();
	}

	static class EnterPieceTester extends Tester {
		public EnterPieceTester () throws parcheesi.Die.InvalidDieException {
			/* NOTE: Don't worry about cases other rules will catch. E.g.; entry square is
			 * already full. That will be caught by MaximumSquareOccupancy.
			 */
			// Setup for all tests.
			Pawn p = new Pawn(1, "blue");

			parcheesi.move.EnterPiece enterMove = new parcheesi.move.EnterPiece(p);
			Move[] moves = new Move[] { enterMove };
			Die[] dice = new Die[] { new Die(5), new Die(4) };

			check(
				EnterPiece.ADieIsFive.instance.enforce(moves, dice, new Board(), new Board()),
				"ADieIsFive success check."
			);

			dice = new Die[] { new Die(4), new Die(1) };
			check(
				!EnterPiece.ADieIsFive.instance.enforce(moves, dice, new Board(), new Board()),
				"ADieIsFive fail check."
			);

			check(
				EnterPiece.DiceSumToFive.instance.enforce(moves, dice, new Board(), new Board()),
				"DiceSumToFive success check."
			);

			dice = new Die[] { new Die(4), new Die(4) };
			check(
				!EnterPiece.DiceSumToFive.instance.enforce(moves, dice, new Board(), new Board()),
				"DiceSumToFive fail check."
			);

			// Integration tests.
			dice = new Die[] { new Die(4), new Die(1) };
			check(
				EnterPiece.instance.enforce(moves, dice, new Board(), new Board()),
				"Integration test: Dice sum to 5 is valid for EnterPiece."
			);

			dice = new Die[] { new Die(5), new Die(1) };
			check(
				EnterPiece.instance.enforce(moves, dice, new Board(), new Board()),
				"Integration test: Any die is 5 is valid for EnterPiece."
			);

			dice = new Die[] { new Die(3), new Die(1) };
			check(
				!EnterPiece.instance.enforce(moves, dice, new Board(), new Board()),
				"Integration test: Dice where neither die is 5 and sum is not 5 are invalid for"
				+ " EnterPiece."
			);

			parcheesi.move.MoveMain move4 = new parcheesi.move.MoveMain(p, 0, 4);
			moves = new Move[] { move4 };
			check(
				EnterPiece.instance.enforce(moves, dice, new Board(), new Board()),
				"Integration test: if no move is an EnterPiece move, then bypass tests. (That is to"
				+ " say, if no rule in this rule set applies, assume that this rule set is"
				+ " satisfied because none of its rules can possibly be broken -- they don't matter"
				+ " in this case.)"
			);

			summarize();
		}
	}
}
