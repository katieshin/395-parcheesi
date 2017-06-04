package parcheesi.move.action;

import java.util.List;

import parcheesi.move.MoveMain;
import parcheesi.move.MoveHome;
import parcheesi.move.Move;
import parcheesi.pawn.Pawn;
import parcheesi.die.Die;
import parcheesi.Board;

import static parcheesi.Parameters.Board.maximumPawnOccupancy;

public class MoveForward implements Action {
	public boolean isApplicable(Class<? extends Move> MoveClass, Die die, Pawn pawn, Board board) {
		if (MoveClass == MoveMain.class || MoveClass == MoveHome.class) {
			Board testBoard = new Board(board);
			if (testBoard.movePawnForward(pawn, die.getValue())) {
				List<Pawn> pawns = board.getPawnsAtCoordinate(board.getPawnCoordinate(pawn));
				if (pawns.size() <= maximumPawnOccupancy) {
					// NOTE: Either you're not in a safe spot, so you're fine. (Might be a Bop.)
					return !board.inSafe(pawn)
						// NOTE: Or you're in a safe spot, and you can't share it with a pawn of another color.
						|| !pawns.stream().anyMatch(p -> !pawn.color.equals(p.color));
				}
			}
		}

		return false;
	}

	public boolean apply(Die die, Pawn pawn, Board board) {
		return board.movePawnForward(pawn, die.getValue());
	}

	// NOTE: Singleton
	public static MoveForward action = new MoveForward();

	public static void main(String[] args) throws Die.InvalidDieException {
		new MoveForwardTester();
	}

	public static class MoveForwardTester extends parcheesi.test.Tester {
		public MoveForwardTester() throws Die.InvalidDieException {
			Die die = new parcheesi.die.NormalDie(3);
			Pawn pawn = new Pawn(0, parcheesi.Color.forPlayer(0).getColorName());
			Board board = new Board();

			check(
				!MoveForward.action.isApplicable(MoveMain.class, die, pawn, board),
				"MoveForward is not applicable for invalid MoveMain conditions"
			);

			check(
				!MoveForward.action.isApplicable(MoveHome.class, die, pawn, board),
				"MoveForward is not applicable for invalid MoveHome conditions"
			);

			board.addPawn(pawn);

			check(
				MoveForward.action.isApplicable(MoveMain.class, die, pawn, board),
				"MoveForward is applicable for MoveMain when move is valid"
			);

			check(
				MoveForward.action.isApplicable(MoveHome.class, die, pawn, board),
				"MoveForward is applicable for MoveHome when move is valid"
			);

			check(
				MoveForward.action.apply(die, pawn, board),
				"Taking MoveForward succeeds for valid conditions regardless of MoveClass"
			);

			Pawn otherPawn1 = new Pawn(0, parcheesi.Color.forPlayer(1));
			board.addPawn(otherPawn1);
			board.movePawnForward(
					pawn,
					parcheesi.Parameters.Board.mainRingSizePerDimension - die.getValue()
			);

			check(
				!MoveForward.action.isApplicable(MoveMain.class, die, pawn, board),
				"Cannot MoveForward if you'd be sharing a safety with a different color pawn"
			);

			Pawn otherPawn2 = new Pawn(1, parcheesi.Color.forPlayer(1));
			board.addPawn(otherPawn2);
			board.movePawnForward(otherPawn1, 1);
			board.movePawnForward(otherPawn2, 1);
			board.movePawnForward(pawn, 1);

			check(
				MoveForward.action.isApplicable(MoveMain.class, die, pawn, board),
				"Cannot MoveForward if you'd be moving onto a blockade"
			);

			board.movePawnForward(pawn, die.getValue() - 1);

			check(
				MoveForward.action.isApplicable(MoveMain.class, die, pawn, board),
				"Cannot MoveForward if you'd be moving through a blockade"
			);

			// NOTE: Reinitialize Board, which has been mutated.
			board = new Board();
			board.addPawn(pawn);

			int maxPawnTravelDistance = parcheesi.Parameters.Board.maxPawnTravelDistance;
			board.movePawnForward(pawn, maxPawnTravelDistance - die.getValue() - 1);

			check(
				MoveForward.action.isApplicable(MoveMain.class, die, pawn, board),
				"Can move forward into home with exact count"
			);

			board.movePawnForward(pawn, die.getValue() - 1);

			check(
				!MoveForward.action.isApplicable(MoveMain.class, die, pawn, board),
				"Cannot MoveForward if you would end up off the board"
			);

			summarize();
		}
	}
}
