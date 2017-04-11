package parcheesi.player;

import parcheesi.move.Move;

public class StubPlayer implements Player {
	public StubPlayer() {
		// TODO: Stub.
	}

	public void startGame(String color) {
		// TODO
		return;
	}

	public Move[] doMove(Board board, int[] dice) {
		// TODO
		return new Move[] { };
	}

	public void DoublesPenalty() {
		// TODO
		// ? Seriously necessary? Seems at first. like an odd implementation choice.
		return;
	}
}
