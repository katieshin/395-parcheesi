package parcheesi.move.action;

import java.util.List;

import parcheesi.move.Move;
import parcheesi.pawn.Pawn;
import parcheesi.die.Die;
import parcheesi.Board;

public class Bop implements Action {
	public boolean isApplicable(Class<? extends Move> MoveClass, Die die, Pawn pawn, Board board) {
		Board testBoard = new Board(board);
		Action preconditionAction;

		if (MoveForward.action.isApplicable(MoveClass, die, pawn, board)) {
			preconditionAction = MoveForward.action;
		} else if (Enter.action.isApplicable(MoveClass, die, pawn, board)) {
			preconditionAction = Enter.action;
		} else {
			return false;
		}

		if (!preconditionAction.apply(die, pawn, testBoard)) {
			return false;
		}

		int coord = testBoard.getPawnCoordinate(pawn);
		List<Pawn> pawns = testBoard.getPawnsAtCoordinate(coord);

		// If there's any pawn of a different color here, return true.
		return pawns.stream().anyMatch(p -> !pawn.color.equals(p.color));

		// TODO?:
		// return BopMoveRules.applicable(die, pawn, board);
	}

	public boolean apply(Die die, Pawn pawn, Board board) {
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
			board.movePawnForward(otherPawn,
					(parcheesi.Parameters.Board.mainRingSizePerDimension
						* (parcheesi.Parameters.Board.dimensions - 1))
					- die.getValue());

			check(
				Bop.action.isApplicable(parcheesi.move.MoveMain.class, die, otherPawn, board),
				"Bop is applicable when other-colored pawn is at destination"
			);

			board.movePawnForward(otherPawn, die.getValue());

			Pawn pawn2 = new Pawn(1, parcheesi.Color.forPlayer(0).getColorName());
			check(
				!Bop.action.isApplicable(parcheesi.move.EnterPiece.class, die, pawn, board),
				"Bop is not applicable for EnterPiece if Enter action fails"
			);

			check(
				Bop.action.isApplicable(parcheesi.move.EnterPiece.class, die, pawn2, board),
				"Bop is applicable for EnterPiece where other colored pawn occupies Entry"
			);

			summarize();
		}
	}
}
