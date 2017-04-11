package parcheesi.move;

import parcheesi.player.Player;
import parcheesi.die.Die;
import parcheesi.Board;

abstract class SmartMove {
	public static boolean canMakeMove(Player player, Board board, Die[] dice) {
		throw new UnsupportedOperationException(
			"SmartMove#canMakeMove(...) has not been implemented by subclass."
		);
	}
}
