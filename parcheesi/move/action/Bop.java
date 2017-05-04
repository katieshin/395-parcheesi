package parcheesi.move.action;

import java.util.List;

import parcheesi.move.Move;
import parcheesi.pawn.Pawn;
import parcheesi.die.Die;
import parcheesi.Board;

public class Bop implements Action {
	public boolean isApplicable(Class<? extends Move> MoveClass, Board board, Die die, Pawn pawn) {
		Board testBoard = new Board(board);
		Action preconditionAction;

		if (MoveForward.action.isApplicable(MoveClass, board, die, pawn)) {
			preconditionAction = MoveForward.action;
		} else if (Enter.action.isApplicable(MoveClass, board, die, pawn)) {
			preconditionAction = Enter.action;
		} else {
			throw new UnsupportedOperationException(
				"Bop action is impossible; it does not even make sense to ask if it is applicable."
			);
		}

		if (!preconditionAction.apply(testBoard, die, pawn)) {
			return false;
		}

		int coord = testBoard.getPawnCoordinate(pawn);
		List<Pawn> pawns = testBoard.getPawnsAtCoordinate(coord);

		// If there's any pawn of a different color here, return true.
		return pawns.stream().anyMatch(p -> !pawn.color.equals(p.color));

		// TODO?:
		// return BopMoveRules.applicable(board, die, pawn);
	}

	public boolean apply(Board board, Die die, Pawn pawn) {
		int coord = board.getPawnCoordinate(pawn);
		List<Pawn> pawns = board.getPawnsAtCoordinate(coord);

		// FIXME use BopPenalty.action here?
		Pawn bopped = pawns.stream()
			.filter(p -> !pawn.color.equals(p.color))
			.findFirst()
			.get();

		// TODO: add a bonus to the turn.
		return board.removePawn(bopped);
	}

	// NOTE: Singleton.
	public static Bop action = new Bop();

	public static void main(String[] args) throws Die.InvalidDieException {
		new BopTester();
	}

	public static class BopTester extends parcheesi.test.Tester {
		public BopTester() throws Die.InvalidDieException {
			Board board = new Board();
			Die die = new parcheesi.die.NormalDie(4);
			Pawn pawn = new Pawn(0, parcheesi.Color.forPlayer(0).getColorName());

			check(
				!Bop.action.isApplicable(parcheesi.move.EnterPiece.class, board, die, pawn),
				"Bop is not applicable for EnterPiece if no other colored pawns on board"
			);

			check(
				!Bop.action.isApplicable(parcheesi.move.MoveMain.class, board, die, pawn),
				"Bop is not applicable for MoveMain when pawn is not on board"
			);

			check(
				!Bop.action.isApplicable(parcheesi.move.MoveHome.class, board, die, pawn),
				"Bop is not applicable for MoveHome when pawn is not on board"
			);

			board.addPawn(pawn);

			check(
				!Bop.action.isApplicable(parcheesi.move.EnterPiece.class, board, die, pawn),
				"Bop is not applicable for EnterPiece if pawn is already on board"
			);

			check(
				!Bop.action.isApplicable(parcheesi.move.MoveMain.class, board, die, pawn),
				"Bop is not applicable for MoveMain when no other pawns are at destination"
			);

			Pawn otherPawn = new Pawn(0, parcheesi.Color.forPlayer(1).getColorName());
			board.addPawn(otherPawn);
			board.movePawnForward(otherPawn, parcheesi.Parameters.Board.pawnMainRingDistance - die.getValue());

			check(
				Bop.action.isApplicable(parcheesi.move.MoveMain.class, board, die, otherPawn),
				"Bop is applicable when other-colored pawn is at destination"
			);

			board.movePawnForward(otherPawn, die.getValue());

			Pawn pawn2 = new Pawn(1, parcheesi.Color.forPlayer(0).getColorName());
			check(
				!Bop.action.isApplicable(parcheesi.move.EnterPiece.class, board, die, pawn),
				"Bop is not applicable for EnterPiece if Enter action fails"
			);

			check(
				Bop.action.isApplicable(parcheesi.move.EnterPiece.class, board, die, pawn2),
				"Bop is applicable for EnterPiece where other colored pawn occupies Entry"
			);

			summarize();
		}
	}
}
