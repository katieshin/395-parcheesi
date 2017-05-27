package parcheesi.move.action;

import java.util.List;

import parcheesi.move.Move;
import parcheesi.pawn.Pawn;
import parcheesi.die.Die;
import parcheesi.Board;

public class BreakBlockade extends MoveForward implements Action {
	public boolean isApplicable(Class<? extends Move> MoveClass, Die die, Pawn pawn, Board board) {
		if (!super.isApplicable(MoveClass, die, pawn, board)) {
			return false;
		}

		List<Pawn> pawns = board.getPawnsAtCoordinate(board.getPawnCoordinate(pawn));
		return pawns
			.stream()
			.filter(p -> !pawn.equals(p))
			.anyMatch(p -> pawn.color.equals(p.color));
	}

	public boolean apply(Die die, Pawn pawn, Board board) {
		// TODO: Remove a blockade from a data structure.
		return false;
	}

	// NOTE: singleton.
	public static BreakBlockade action = new BreakBlockade();

	public static void main(String[] args) throws parcheesi.die.Die.InvalidDieException {
		new BreakBlockadeTester();
	}

	private static class BreakBlockadeTester extends parcheesi.test.Tester {
		public BreakBlockadeTester() throws parcheesi.die.Die.InvalidDieException {
			Board board = new Board();
			Pawn pawn   = new Pawn(0, parcheesi.Color.forPlayer(0).getColorName());
			Pawn pawn2  = new Pawn(1, parcheesi.Color.forPlayer(0).getColorName());
			Die die     = new parcheesi.die.NormalDie(3);

			board.addPawn(pawn);

			check(
				!action.isApplicable(parcheesi.move.MoveMain.class, die, pawn, board),
				"BreakBlockade is not applicable on a board without any blockades"
			);

			board.addPawn(pawn2);

			check(
				action.isApplicable(parcheesi.move.MoveMain.class, die, pawn, board),
				"BreakBlockade is applicable on a board where pawns with same color share space"
			);

			// TODO: test breaking the blockade (apply, not just isApplicable)
			board.movePawnForward(pawn2, die.getValue());

			Pawn otherPawn = new Pawn(0, parcheesi.Color.forPlayer(1).getColorName());
			board.addPawn(otherPawn);
			board.movePawnForward(otherPawn, parcheesi.Parameters.Board.pawnMainRingDistance);

			check(
				!action.isApplicable(parcheesi.move.MoveMain.class, die, pawn, board),
				"BreakBlockade is not applicable on a board where pawns with differing colors share space"
			);

			summarize();
		}
	}
}
