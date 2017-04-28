package parcheesi.turn;

import java.util.ArrayList;

public class Turn {
	// Dice held on this Turn.
	List<Die> diceAvailable;
	// Indices of dice used so far.
	List<Die> diceUsed;
	// Moves allowed given the current dice.
	List<TranslatedMove> movesAvailable;
	// Moves that have been taken so far. (Corresponding to some die.)
	List<TranslatedMove> movesTaken;

	public Turn(Die[] startingDice) {
		int startingDiceCount = startingDice.length;

		diceAvailable  = new ArrayList<Die>(startingDice);

		diceUsed       = new ArrayList<int>(startingDiceCount);
		movesAvailable = new ArrayList<TranslatedMove>(startingDiceCount);
		movesTaken     = new ArrayList<TranslatedMove>(startingDiceCount);
	}

	public void addDie(Die d) {
		dice.add(d);
		allowedMoves.add(new TranslatedMove(d));
	}

	// TODO: implement TranslatedMove.equals(...).
	public boolean moveIsAvailable (TranslatedMove move) {
		boolean result = false;
		for (availableMove : movesAvailable) {
			result |= availableMove.equals(move);
		}
		return result;
	}
}
