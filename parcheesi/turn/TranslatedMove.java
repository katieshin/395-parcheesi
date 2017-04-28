package parcheesi.turn;

import java.util.ArrayList;
import java.util.List;

import parcheesi.move.action.MoveAction;
import parcheesi.move.Move;
import parcheesi.pawn.Pawn;
import parcheesi.die.Die;
import parcheesi.Board;

class TranslatedMove {

	Class<? extends Move> MoveClass;
	List<MoveAction> actions;

	/**
	 * Create the complete move encapsulating all of the Actions taken by some Pawn on some Board
	 * using some Die.
	 *
	 * @param die The Die to use
	 * @param pawn The Pawn using the Die
	 * @param board The Board on which the Pawn moves
	 */
	public TranslatedMove(Die die, Pawn pawn, Board board) {
		MoveClass = getMoveClass(dice, pawn, board);
		modifiers = getApplicableActions(MoveClass, die, pawn, board);
	}

	/**
	 * Generate the board resulting from applying all of the Actions to be taken for this
	 * TranslatedMove.
	 *
	 * @param board Board to copy and update
	 * @return Board resulting from applying every Action to take for this TranslatedMove
	 */
	public Board take(Board board) {
		Board result = new Board(board);

		for (MoveAction action : actions) {
			action.take(result);
		}

		return result;
	}

	private Class<? extends Move> getMoveClass(Die die, Pawn pawn, Board board) {
		for (Class<? extends Move> MoveClass : Move.MOVE_CLASSES) {
			if (isMoveFeasible(MoveClass, die, pawn, board)) {
				return MoveClass;
			}
		}
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
	private List<MoveAction> getApplicableActions(Class<? extends Move> MoveClass, Die die, Pawn pawn, Board board) {
		List<MoveAction> applicableActions = new ArrayList<MoveAction>();

		for (MoveAction action : MoveAction.ACTIONS) {
			if (action.isApplicable(MoveClass, die, pawn, board)) {
				applicableActions.add(action);
			}
		}

		return applicableActions;
	}
}
