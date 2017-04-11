package parcheesi.move;

import parcheesi.player.Player;
import parcheesi.die.Die;
import parcheesi.Board;
import parcheesi.Pawn;

// represents a move where a player enters a piece
public class EnterPiece extends SmartMove implements Move {
	Pawn pawn;

	public EnterPiece(Pawn pawn) {
		this.pawn=pawn;
	}

	// FIXME this responsibility should be moved to RulesChecker!
	// FIXME SmartMove should be deleted; use RulesChecker.validMove(...) instead.
	public static boolean canMakeMove(Player player, Board board, Die[] dice) {
		Pawn[] pawnsInStart = board.getPlayerPawnsInStart(player);

		if (pawnsInStart.length == 0) {
			return false; // There are no pawns that can enter.
		}

		Pawn p = pawnsInStart[0];
		Move m = new EnterPiece(p);

		// TODO: parcheesi.rule.RulesChecker.check(...) instead.
		return parcheesi.rule.EnterPieceRules.instance.enforce(
			dice,
			player,
			board,
			new Move[] { m },
			board.performMove(m)
		);
	}

	public static void main(String[] args) throws Die.InvalidDieException {
		new EnterPieceTester();
	}

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
