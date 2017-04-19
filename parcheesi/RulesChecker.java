package parcheesi;

import parcheesi.move.Move;
import parcheesi.pawn.Pawn;
import parcheesi.Board;

class RulesChecker {
	public static boolean validMove(Move move, Board board) {
		/* For each type of move, determine whether move is valid given board.
		 *	 - EnterPiece
		 *	 - MoveMain
		 *	 - MoveHome
		 */
		return true;
	}

	public static void main(String args[]) {
		new RulesCheckerTester();
	}

	static class RulesCheckerTester extends parcheesi.test.Tester {
		public RulesCheckerTester() {
			parcheesi.pawn.Pawn p = new parcheesi.pawn.Pawn(5, "blue");

			check(
				RulesChecker.validMove(new parcheesi.move.EnterPiece(p), new Board()),
				"EnterPiece should be valid on an empty Board."
			);

			summarize();
		}
	}
}

