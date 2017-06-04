package parcheesi.move.action;

import parcheesi.move.EnterPiece;
import parcheesi.move.Move;
import parcheesi.pawn.Pawn;
import parcheesi.die.Die;
import parcheesi.Board;

public class Enter implements Action {
	public boolean isApplicable(Class<? extends Move> MoveClass, Die die, Pawn pawn, Board board) {
		return MoveClass == EnterPiece.class
			&& die.getValue() == parcheesi.Parameters.dieValueToEnter
			&& (new Board(board)).addPawn(pawn);
	}

	public boolean apply(Die die, Pawn pawn, Board board) {
		return board.addPawn(pawn);
	}

	// NOTE: Singleton.
	public static Enter action = new Enter();

	public static void main(String[] args) throws Die.InvalidDieException {
		new EnterTester();
	}

	public static class EnterTester extends parcheesi.test.Tester {
		public EnterTester() throws Die.InvalidDieException {
			Die die     = new parcheesi.die.NormalDie(parcheesi.Parameters.dieValueToEnter);
			Pawn pawn1  = new Pawn(0, parcheesi.Color.forPlayer(0).getColorName());
			Board board = new Board();

			check(
				Enter.action.isApplicable(EnterPiece.class, die, pawn1, board),
				"Enter move action is applicable for valid EnterPiece conditions"
			);

			check(
				Enter.action.apply(die, pawn1, board),
				"Taking Enter move action succeeds for valid EnterPiece conditions"
			);

			die = new parcheesi.die.NormalDie(((parcheesi.Parameters.dieValueToEnter - 2) % 6) + 1);

			check(
				!Enter.action.isApplicable(EnterPiece.class, die, pawn1, board),
				"Enter move action is not applicable if the pawn is already entered"
			);

			Pawn pawn2 = new Pawn(1, parcheesi.Color.forPlayer(0).getColorName());

			check(
				!Enter.action.isApplicable(EnterPiece.class, die, pawn2, board),
				"Enter move action is not applicable for unentered pawn with incorrect die value"
			);

			Pawn pawn3 = new Pawn(2, parcheesi.Color.forPlayer(0).getColorName());

			board.addPawn(pawn2);

			check(
				!Enter.action.isApplicable(EnterPiece.class, die, pawn3, board),
				"Enter action is not applicable if there's a blockade on entry"
			);

			summarize();
		}
	}
}
