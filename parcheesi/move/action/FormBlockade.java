package parcheesi.move.action;

import java.util.List;

import parcheesi.move.EnterPiece;
import parcheesi.move.MoveMain;
import parcheesi.move.Move;
import parcheesi.pawn.Pawn;
import parcheesi.die.Die;
import parcheesi.Board;

public class FormBlockade implements Action {
	public boolean isApplicable(Class<? extends Move> MoveClass, Die die, Pawn pawn, Board board) {
		Board testBoard = new Board(board);

		if (MoveClass == EnterPiece.class) {
			testBoard.addPawn(pawn);
		} else if (MoveClass == MoveMain.class) {
			testBoard.movePawnForward(pawn, die.getValue());
		} else {
			return false;
		}

		List<Pawn> pawns = testBoard.getPawnsAtCoordinate(testBoard.getPawnCoordinate(pawn));
		return pawns
			.stream()
			.filter(p -> !pawn.equals(p))
			.anyMatch(p -> pawn.color.equals(p.color));
	}

	public boolean apply(Die die, Pawn pawn, Board board) {
		// TODO: Add a blockade to a data structure.
		return false;
	}

	// NOTE: singleton.
	public static FormBlockade action = new FormBlockade();

	public static void main(String[] args) throws Die.InvalidDieException {
		new FormBlockadeTester();
	}

	private static class FormBlockadeTester extends parcheesi.test.Tester {
		public FormBlockadeTester() throws Die.InvalidDieException {
			Board board = new Board();
			Pawn pawn = new Pawn(0, parcheesi.Color.forPlayer(0).getColorName());
			Die die = new parcheesi.die.NormalDie(5);

			board.addPawn(pawn);

			check(
				!action.isApplicable(MoveMain.class, die, pawn, board),
				"FormBlockade is not applicable on a board with only 1 pawn"
			);

			Pawn pawn2 = new Pawn(1, parcheesi.Color.forPlayer(0).getColorName());

			check(
				action.isApplicable(EnterPiece.class, die, pawn2, board),
				"FormBlockade is applicable to EnterPiece when there is a pawn of same color on entry"
			);
			
			board.addPawn(pawn2);
			board.movePawnForward(pawn, die.getValue());

			check(
				action.isApplicable(MoveMain.class, die, pawn2, board),
				"FormBlockade is applicable to MoveMain when moving to position having a pawn of same color"
			);

			check(
				!action.isApplicable(EnterPiece.class, die, pawn2, board),
				"FormBlockade is not applicable to EnterPiece move on a pawn that already entered"
			);

			pawn.color = parcheesi.Color.forPlayer(1).getColorName();

			check(
				!action.isApplicable(MoveMain.class, die, pawn2, board),
				"FormBlockade is not applicable to MoveMain when moving to a pawn of a different color"
			);

			summarize();
		}
	}
}
