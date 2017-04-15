package parcheesi.move;

import parcheesi.player.Player;
import parcheesi.die.Die;
import parcheesi.Board;
import parcheesi.Pawn;

// represents a move where a player enters a piece
public class EnterPiece implements Move {
	Pawn pawn;

	public EnterPiece(Pawn pawn) {
		this.pawn=pawn;
	}

	public static void main(String[] args) throws Die.InvalidDieException {
		new EnterPieceTester();
	}

	// FIXME: these tests don't belong here
	static class EnterPieceTester extends parcheesi.test.Tester {
		public EnterPieceTester() throws Die.InvalidDieException {
			Player p = new parcheesi.player.SimplePlayer();

			check(
				EnterPiece.canMakeMove(p, new Board(), new Die[] {
					new parcheesi.die.NormalDie(5),
					new parcheesi.die.NormalDie(1)
				}),
				"Should be able to make move EnterPiece if a die is 5."
			);

			check(
				EnterPiece.canMakeMove(p, new Board(), new Die[] {
					new parcheesi.die.NormalDie(3),
					new parcheesi.die.NormalDie(2)
				}),
				"Should be able to make move EnterPiece if sum of dice is 5."
			);

			summarize();
		}
	}
}
