package parcheesi.move.action;

import java.util.List;
import java.util.Optional;

import parcheesi.move.Move;
import parcheesi.pawn.Pawn;
import parcheesi.die.Die;
import parcheesi.Board;

public class Bop implements Action {
	private static class BopEnter extends Enter {
		public static BopEnter action = new BopEnter();
	}

	private static class BopMoveForward extends MoveForward {
		public static BopMoveForward action = new BopMoveForward();
	}

	public boolean isApplicable(Class<? extends Move> MoveClass, Die die, Pawn pawn, Board board) {
		Board testBoard = new Board(board);

		if (BopEnter.action.isApplicable(MoveClass, die, pawn, testBoard)) {
			return BopEnter.action.apply(die, pawn, testBoard)
				&& findBopTarget(pawn, testBoard).isPresent();
		} else if (BopMoveForward.action.isApplicable(MoveClass, die, pawn, testBoard)) {
			if (BopMoveForward.action.apply(die, pawn, testBoard)) {
				Optional<Pawn> bopTarget = findBopTarget(pawn, testBoard);
				return bopTarget.isPresent() && !board.isSafe(bopTarget.get());
			}
		}

		return false;
	}

	public boolean apply(Die die, Pawn pawn, Board board) {
		// Pre: Bop.isApplicable(...) -> true
		// TODO: add a bonus to the turn.
		return board.removePawn(findBopTarget(pawn, board).get());
	}

	private Optional<Pawn> findBopTarget(Pawn bopper, Board board) {
		int coord = board.getPawnCoordinate(bopper);
		List<Pawn> pawns = board.getPawnsAtCoordinate(coord);

		return pawns
			.stream()
			.filter(p -> !bopper.color.equals(p.color))
			.findFirst();
	}

	// NOTE: Singleton.
	public static Bop action = new Bop();

	public static void main(String[] args) throws Die.InvalidDieException {
		new BopTester();
	}

	public static class BopTester extends parcheesi.test.Tester {
		public BopTester() throws Die.InvalidDieException {
			Board board = new Board();
			Die die = new parcheesi.die.NormalDie(5);
			Pawn pawn = new Pawn(0, parcheesi.Color.forPlayer(0).getColorName());

			check(
				!Bop.action.isApplicable(parcheesi.move.EnterPiece.class, die, pawn, board),
				"Bop is not applicable for EnterPiece if no other colored pawns on board"
			);

			check(
				!Bop.action.isApplicable(parcheesi.move.MoveMain.class, die, pawn, board),
				"Bop is not applicable for MoveMain when pawn is not on board"
			);

			check(
				!Bop.action.isApplicable(parcheesi.move.MoveHome.class, die, pawn, board),
				"Bop is not applicable for MoveHome when pawn is not on board"
			);

			board.addPawn(pawn);

			check(
				!Bop.action.isApplicable(parcheesi.move.EnterPiece.class, die, pawn, board),
				"Bop is not applicable for EnterPiece if pawn is already on board"
			);

			check(
				!Bop.action.isApplicable(parcheesi.move.MoveMain.class, die, pawn, board),
				"Bop is not applicable for MoveMain when no other pawns are at destination"
			);

			Pawn otherPawn = new Pawn(0, parcheesi.Color.forPlayer(1).getColorName());
			board.addPawn(otherPawn);
			// Move otherPawn onto player 0 dimension.
			board.movePawnForward(otherPawn,
					(parcheesi.Parameters.Board.mainRingSizePerDimension
						* (parcheesi.Parameters.Board.dimensions - 1))
					- die.getValue());

			check(
				!Bop.action.isApplicable(parcheesi.move.MoveMain.class, die, otherPawn, board),
				"Bop is not applicable when other-colored pawn is at destination but destination is safe"
			);

			// NOTE: move pawn off of Entry, because it's safe.
			board.movePawnForward(pawn, 1);

			// Move otherPawn onto player 0 entry
			board.movePawnForward(otherPawn, die.getValue());

			Pawn pawn2 = new Pawn(1, parcheesi.Color.forPlayer(0).getColorName());
			check(
				Bop.action.isApplicable(parcheesi.move.EnterPiece.class, die, pawn2, board),
				"Bop is applicable for EnterPiece where other colored pawn occupies Entry"
			);

			check(
				!Bop.action.isApplicable(parcheesi.move.EnterPiece.class, die, pawn, board),
				"Bop is not applicable for EnterPiece if Enter action fails"
			);

			summarize();
		}
	}
}
