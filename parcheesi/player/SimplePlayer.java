package parcheesi.player;

import java.util.List;
import java.util.ArrayList;

import parcheesi.turn.TranslatedMove;
import parcheesi.die.NormalDie;
import parcheesi.pawn.Pawn;
import parcheesi.turn.Turn;
import parcheesi.move.Move;
import parcheesi.die.Die;
import parcheesi.Board;
import parcheesi.Color;

import static parcheesi.Parameters.pawnsPerPlayer;

public class SimplePlayer implements Player {
	List<Pawn> pawns;
	Color.Player color;

	public void startGame(String color) {
		this.color = Color.Player.lookupByColorName(color);

		pawns = new ArrayList<Pawn>(pawnsPerPlayer);
		for (int pi = 0; pi < pawnsPerPlayer; pi++) {
			pawns.add(new Pawn(pi, this.color));
		}
	}

	public Move[] doMove(Board board, int[] dieValues) {
		List<Die> dice = new ArrayList<Die>(dieValues.length);
		for (int dieValue : dieValues) {
			try {
				dice.add(new NormalDie(dieValue));
			} catch (Die.InvalidDieException ex) {
				// be sad
			}
		}

		Turn myTurn = new Turn(this, board, dice);

		List<Board> boards = myTurn.nextBoardsAvailable();

		TranslatedMove best;
		int maxH = 0;
		int maxHR = 0;
		int maxMain = 0;
		int maxSafe = 0;
		int ct = 0;
		for(Board b : boards)
		{
			int inH = 0;
			int inHR = 0;
			int inMain = 0;
			int inSafe = 0;
			for(Pawn p : this.pawns())
			{
				if(b.inHome(p))
					inH++;
				if(b.inHomeRow(p))
					inHR++;
				if(b.inMain(p))
					inMain++;
				if(b.inSafe(p))
					
					inSafe++;
			}

			if(inH > maxH)
			{
				best = myTurn.movesAvailable().get(ct);
				maxH = inH;
				maxHR = inHR;
				maxMain = inMain;
				maxSafe = inSafe;
			}
			if(inH == maxH)
			{
				if(inHR > maxHR)
				{
					best = myTurn.movesAvailable().get(ct);
					maxH = inH;
					maxHR = inHR;
					maxMain = inMain;
					maxSafe = inSafe;
				}
				if(inHR == maxHR)
				{
					if(inMain > maxMain)
					{
						best = myTurn.movesAvailable().get(ct);
						maxH = inH;
						maxHR = inHR;
						maxMain = inMain;
						maxSafe = inSafe;
					}
					if(inMain == maxMain)
					{
						if(inSafe >= maxSafe)
						{
							best = myTurn.movesAvailable().get(ct);
							maxH = inH;
							maxHR = inHR;
							maxMain = inMain;
							maxSafe = inSafe;
						}
					}
				}
			}
			ct++;
			if(ct > 100)
				break;
		}

		return best;
	}

	public void DoublesPenalty() {
		// TODO
		// getFurthestPawn().restart();
	}

	public List<Pawn> pawns() {
		return pawns;
	}
}
