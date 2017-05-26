package parcheesi.turn;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

import parcheesi.move.action.Action;
import parcheesi.move.EnterPiece;
import parcheesi.move.MoveMain;
import parcheesi.move.MoveHome;
import parcheesi.move.Move;
import parcheesi.pawn.Pawn;
import parcheesi.die.Die;
import parcheesi.Board;

class TranslatedMove {
	private Class<? extends Move> MoveClass;
	private List<Action> actions;
	private Die die;
	private Pawn pawn;

	/**
	 * Create the complete move encapsulating all of the Actions taken by some Pawn on some Board
	 * using some Die.
	 *
	 * @param die The Die to use
	 * @param pawn The Pawn using the Die
	 * @param board The Board on which the Pawn moves
	 */
	public TranslatedMove(Die die, Pawn pawn, Board board) throws IllegalStateException {
		this.die  = die;
		this.pawn = pawn;

		List<Class<? extends Move>> possibleMoveClasses = getMoveClasses(die, pawn, board);
		if (possibleMoveClasses.size() != 1) {
			throw new IllegalStateException("A TranslatedMove somehow has more than 1 MoveClass");
		}

		this.MoveClass = possibleMoveClasses.get(0);
		this.actions   = getApplicableActions(MoveClass, die, pawn, board);
	}

	/**
	 * Generate the board resulting from applying all of the Actions to be taken for this
	 * TranslatedMove.
	 *
	 * @param board Board to copy and update
	 * @return Board resulting from applying every Action to take for this TranslatedMove
	 */
	public Board take(Board board) {
		Board resultBoard = new Board(board);

		for (Action action : actions) {
			action.apply(die, pawn, resultBoard);
		}

		return resultBoard;
	}

	private List<Class<? extends Move>> getMoveClasses(Die die, Pawn pawn, Board board) {
		return parcheesi.move.Manifest.MOVE_CLASSES.stream()
			.filter(MoveClass -> isMoveFeasible(MoveClass, die, pawn, board))
			.collect(Collectors.toList());
	}

	private boolean isMoveFeasible(Class<? extends Move> MoveClass, Die die, Pawn pawn, Board board) {
		Board testBoard = new Board(board);

		if (MoveClass == MoveMain.class || MoveClass == MoveHome.class) {
			return testBoard.movePawnForward(pawn, die.getValue());
		}

		if (MoveClass == EnterPiece.class) {
			return testBoard.addPawn(pawn);
		}

		return false;
	}

	// Assuming a Move is feasible, what modifiers apply to the Move?
	private List<Action> getApplicableActions(Class<? extends Move> MoveClass, Die die, Pawn pawn, Board board) {
		return parcheesi.move.action.Manifest.ACTIONS
			.stream()
			.filter(a -> a.isApplicable(MoveClass, die, pawn, board))
			.collect(Collectors.toList());
	}

	public static void main(String[] args) {
		new TranslatedMoveTester();
	}

	private static class TranslatedMoveTester extends parcheesi.test.Tester {
		public TranslatedMoveTester() {
			summarize();
		}
	}
}
