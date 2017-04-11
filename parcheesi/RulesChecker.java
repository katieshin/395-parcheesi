package parcheesi;

import parcheesi.Board;
import parcheesi.move.Move;
import parcheesi.test.Tester;
import parcheesi.Pawn;

class RulesChecker {
    public static boolean validMove(Move move, Board board) {
        /* For each type of move, determine whether move is valid given board.
         *   - EnterPiece
         *   - MoveMain
         *   - MoveHome
         */
        return true;
    }

    public static void main(String args[]) {
        new RulesCheckerTester();
    }
}

class RulesCheckerTester extends Tester {
    public RulesCheckerTester() {
        Pawn p = new Pawn(5, "blue");

        check(
            RulesChecker.validMove(new parcheesi.move.EnterPiece(p), new Board()),
            "EnterPiece should be valid on an empty Board."
        );

        summarize();
    }
}
