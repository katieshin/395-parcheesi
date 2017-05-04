package parcheesi.move.action;

import parcheesi.move.EnterPiece;
import parcheesi.move.Move;
import parcheesi.pawn.Pawn;
import parcheesi.die.Die;
import parcheesi.Board;

public class Enter implements Action {
	public boolean isApplicable(Class<? extends Move> MoveClass, Board board, Die die, Pawn pawn) {
		return MoveClass == EnterPiece.class;
	}

	public boolean apply(Board board, Die die, Pawn pawn) {
		return board.addPawn(pawn);
	}

	// NOTE: Singleton.
	public static Enter action = new Enter();

	public static void main(String[] args) throws Die.InvalidDieException {
		new EnterTester();
	}

	public static class EnterTester extends parcheesi.test.Tester {
		public EnterTester() throws Die.InvalidDieException {
			Board board = new Board();

			// NOTE: This die roll is purposefully invalid for an EnterPiece move.
			Die die = new parcheesi.die.NormalDie(5);

			// NOTE: This set of added Pawns is purposefully chosen to be invalid.
			Pawn pawn1 = new Pawn(0, parcheesi.Color.forPlayer(0).getColorName());

			check(
				Enter.action.isApplicable(EnterPiece.class, board, die, pawn1),
				"Enter move action is applicable for valid EnterPiece conditions"
			);

			check(
				Enter.action.apply(board, die, pawn1),
				"Taking Enter move action succeeds for valid EnterPiece conditions"
			);

			board = new Board();
			die = new parcheesi.die.NormalDie(1);

			check(
				Enter.action.isApplicable(EnterPiece.class, board, die, pawn1),
				"Enter move action is applicable even if it breaks the rules of EnterPiece"
			);

			check(
				Enter.action.apply(board, die, pawn1),
				"Taking Enter move action succeeds even if it breaks the rules of EnterPiece"
			);

			Pawn pawn2 = new Pawn(1, parcheesi.Color.forPlayer(0).getColorName());
			Pawn pawn3 = new Pawn(2, parcheesi.Color.forPlayer(0).getColorName());
			Pawn pawn4 = new Pawn(3, parcheesi.Color.forPlayer(0).getColorName());

			board.addPawn(pawn2);
			board.addPawn(pawn3);
			board.addPawn(pawn4);

			check(
				Enter.action.isApplicable(EnterPiece.class, board, die, pawn1),
				"Enter move action is applicable even if the resulting Board is broke AF"
			);

			check(
				!Enter.action.apply(board, die, pawn1),
				"Cannot take Enter move action if the resulting Board would be broke AF"
			);

			summarize();
		}
	}
}
