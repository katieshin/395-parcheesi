package parcheesi;

import parcheesi.move.Move;

// NOTE: Do not change this interface!
public interface Player {
	// inform the player that a game has started
	// and what color the player is.
	void startGame(String color);

	// ask the player what move they want to make
	Move[] doMove(Board brd, int[] dice);

	// inform the player that they have suffered
	// a doubles penalty
	void DoublesPenalty();
}
