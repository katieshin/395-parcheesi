package parcheesi.player;

import parcheesi.pawn.Pawn;
import parcheesi.move.Move;
import parcheesi.die.Die;
import parcheesi.Board;
import parcheesi.Color;

public class SimplePlayer implements Player {
	// TODO?
	// NOTE: Stub
	public SimplePlayer () { }

	public void startGame(String color) {
		this.color = Color.Player.lookupByColorName(color);
	}

	public Move[] doMove(Board board, int[] dice) {
		// TODO
		Turn myTurn = new Turn(this, board, dice);

		List<Board> boards = myTurn.nextBoardsAvailable();

		Move best;
		int maxH = 0;
		int maxHR = 0;
		int maxRing = 0;
		int maxSafe = 0;
		int ct = 0;
		for(Board board : boards)
		{
			int inH = 0;
			int inHR = 0;
			int inRing = 0;
			int inSafe = 0;
			for(Pawn p : this.pawns())
			{
				if(board.inHome(p))
					inH++;
				if(board.inHomeRow(p))
					inHR++;
				if(board.inMain(p))
					inRing++;
				if(board.inSafe(p))
					
					inSafe++;
			}

			if(inH > maxH)
			{
				best = myTurn.turnsAvailable()[ct]
				maxH = inH;
				maxHR = inHR;
				maxMain = inMain;
				maxSafe = inSafe;
			}
			if(inH == maxH)
			{
				if(inHR > maxHR)
				{
					best = myTurn.turnsAvailable()[ct]
					maxH = inH;
					maxHR = inHR;
					maxMain = inMain;
					maxSafe = inSafe;
				}
				if(inHR == maxHR)
				{
					if(inMain > maxMain)
					{
						best = myTurn.turnsAvailable()[ct]
						maxH = inH;
						maxHR = inHR;
						maxMain = inMain;
						maxSafe = inSafe;
					}
					if(inMain == maxMain)
					{
						if(inSafe >= maxSafe)
						{
							best = myTurn.turnsAvailable()[ct]
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
}
