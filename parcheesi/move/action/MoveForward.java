package parcheesi.move.action;

import parcheesi.move.MoveMain;
import parcheesi.move.MoveHome;
import parcheesi.move.Move;
import parcheesi.pawn.Pawn;
import parcheesi.die.Die;
import parcheesi.Board;

public class MoveForward implements Action {
	// TODO: What about additional effects? e.g. add/remove a Blockade from Game; create bonus Die...
	public boolean isApplicable(Class<? extends Move> MoveClass, Board board, Die die, Pawn pawn) {
		return MoveClass == MoveMain.class || MoveClass == MoveHome.class;
	}

	public boolean apply(Board board, Die die, Pawn pawn) {
		return board.movePawnForward(pawn, die.getValue());
	}

	// NOTE: Singleton
	public static MoveForward action = new MoveForward();

	public static void main(String[] args) throws Die.InvalidDieException {
		new MoveForwardTester();
	}

	public static class MoveForwardTester extends parcheesi.test.Tester {
		public MoveForwardTester() throws Die.InvalidDieException {
			Board board = new Board();

			Die die = new parcheesi.die.NormalDie(3);
			Pawn pawn = new Pawn(0, parcheesi.Color.forPlayer(0).getColorName());

			check(
				MoveForward.action.isApplicable(MoveMain.class, board, die, pawn),
				"MoveForward is applicable for invalid MoveMain conditions"
			);

			check(
				MoveForward.action.isApplicable(MoveHome.class, board, die, pawn),
				"MoveForward is application for invalid MoveHome conditions"
			);

			board.addPawn(pawn);

			check(
				MoveForward.action.isApplicable(MoveMain.class, board, die, pawn),
				"MoveForward is applicable for valid MoveMain conditions"
			);

			check(
				MoveForward.action.isApplicable(MoveHome.class, board, die, pawn),
				"MoveForward is applicable for invalid MoveHome conditions"
			);

			check(
				MoveForward.action.apply(board, die, pawn),
				"Taking MoveForward succeeds for valid MoveMain conditions and invalid MoveHome conditions"
			);

			// NOTE: Reinitialize Board, which has been mutated.
			board = new Board();
			board.addPawn(pawn);

			int maxPawnTravelDistance = parcheesi.Parameters.Board.maxPawnTravelDistance;
			board.movePawnForward(pawn, maxPawnTravelDistance - die.getValue() - 1);

			check(
				MoveForward.action.apply(board, die, pawn),
				"Taking MoveForward succeeds for valid MoveHome conditions and invalid MoveMain conditions"
			);

			check(
				!MoveForward.action.apply(board, die, pawn),
				"Cannot take MoveForward if the resulting Board would be broke AF (cannot walk off Board)"
			);

			summarize();
		}
	}
}
