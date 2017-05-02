package parcheesi;

import static parcheesi.Parameters;

import parcheesi.player.Player;

// NOTE: Do not change this interface!
interface Game {
	// add a player to the game
	void register(Player p);

	// start a game
	void start();
}

public class Parcheesi implements Game {
	/*
	 * Manage:
	 *	Players
	 *	Pawns
	 *	Board
	 *	Turns
	 */
	// Players and Pawns.
	Player[] players = new Player[Parameters.maxPlayers];
	Pawn[] pawns     = new Pawn[Parameters.pawnsPerPlayer * Parameters.maxPlayers];

	// The current Board.
	Board currentBoard = new Board();

	// Complete history of the Game (by Turns).
	List<Turn> turnHistory = new ArrayList<Turn>();

	public Parcheesi() {

	}

	void register(Player p) {

	}

	void start() {

	}
}
