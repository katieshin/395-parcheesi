package parcheesi;

import parcheesi.player.Player;

// NOTE: Do not change this interface!
interface Game {
	// add a player to the game
	void register(Player p);

	// start a game
	void start();
}
