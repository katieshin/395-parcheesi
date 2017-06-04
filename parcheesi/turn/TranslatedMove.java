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
	private Board board;

	/**
	 * Create the complete move encapsulating all of the Actions taken by some Pawn on some Board
	 * using some Die.
	 *
	 * @param die The Die to use
	 * @param pawn The Pawn using the Die
	 * @param board The Board on which the Pawn moves
	 */
	public TranslatedMove(Die die, Pawn pawn, Board board) throws IllegalStateException {
		this.die   = die;
		this.pawn  = pawn;
		this.board = board;

		this.MoveClass = getMoveClass(die, pawn, board);
		this.actions   = getApplicableActions(this.MoveClass, die, pawn, board);
	}

	/**
	 * Generate the board resulting from applying all of the Actions to be taken for this
	 * TranslatedMove.
	 *
	 * @param board Board to copy and update
	 * @return Board resulting from applying every Action to take for this TranslatedMove
	 */
	public Board take(Board board) throws UnsupportedOperationException {
		if (this.board != board) {
			throw new UnsupportedOperationException(
				"Cannot take move on board different from board for which move was generated"
			);
		}

		Board resultBoard = new Board(board);

		for (Action action : actions) {
			action.apply(die, pawn, resultBoard);
		}

		return resultBoard;
	}

	private Class<? extends Move> getMoveClass(Die die, Pawn pawn, Board board)
		throws IllegalStateException {
		Board testBoard = new Board(board);

		if (testBoard.inNest(pawn)) {
			return EnterPiece.class;
		} else if (testBoard.inHomeRow(pawn)) {
			return MoveHome.class;
		} else if (testBoard.inMain(pawn)) {
			return MoveMain.class;
		}

		throw new IllegalStateException("No MoveClass seems to apply to this (die, pawn, board) set");
	}

	// Assuming a Move is feasible, what modifiers apply to the Move?
	private List<Action> getApplicableActions(
			Class<? extends Move> MoveClass,
			Die die,
			Pawn pawn,
			Board board
	) {
		return parcheesi.move.action.Manifest.ACTIONS
			.stream()
			.filter(a -> a.isApplicable(MoveClass, die, pawn, board))
			.collect(Collectors.toList());
	}

	public static void main(String[] args) throws Die.InvalidDieException {
		new TranslatedMoveTester();
	}

	private static class TranslatedMoveTester extends parcheesi.test.Tester {
		TranslatedMove tm;

		void debug() {
			System.out.println(tm.MoveClass);
			System.out.println("Actions:...");
			tm.actions.stream().forEach(
				a -> System.out.println(a.toString().substring("parcheesi.move.action.".length()))
			);
			System.out.println("From die: " + tm.die.getValue());
			System.out.println("From pawn: player " + tm.pawn.playerIndex + " id " + tm.pawn.id);
			System.out.println("🧀 🧀 🧀");
		}

		public TranslatedMoveTester() throws Die.InvalidDieException {
			Board board = new Board();
			Pawn pawn   = new Pawn(0, parcheesi.Color.forPlayer(1));
			Die die     = new parcheesi.die.NormalDie(5);

			tm = new TranslatedMove(die, pawn, board);
			debug();
			board = tm.take(board);

			tm = new TranslatedMove(tm.die, tm.pawn, board);
			debug();

			Pawn pawn2 = new Pawn(1, parcheesi.Color.forPlayer(1));
			tm = new TranslatedMove(die, pawn2, board);
			debug();
			board = tm.take(board);

			Pawn otherPawn = new Pawn(0, parcheesi.Color.forPlayer(0));
			tm = new TranslatedMove(die, otherPawn, board);
			debug();
			board = tm.take(board);

			board.movePawnForward(
				otherPawn,
				parcheesi.Parameters.Board.mainRingSizePerDimension - die.getValue()
			);

			tm = new TranslatedMove(die, otherPawn, board);
			debug();
		}
	}
}
