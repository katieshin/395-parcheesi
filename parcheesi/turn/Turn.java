package parcheesi.turn;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.Collectors;

import parcheesi.Board;
import parcheesi.die.Die;
import parcheesi.pawn.Pawn;
import parcheesi.player.Player;
import parcheesi.die.CombinedDie;

public class Turn {
	// Dice held on this Turn.
	List<Die> diceAvailable;
	// Dice combinations.
	List<Die> diceCombinations;
	// Indices of dice used so far.
	List<Die> diceUsed;
	// Moves allowed given the current dice.
	List<TranslatedMove> movesAvailable;
	// Possible end board stated allowed given the current dice.
	List<Board> nextBoardsAvailable;
	// Moves that have been taken so far. (Corresponding to some die.)
	List<TranslatedMove> movesTaken;
	// Player taking the turn.
	Player player;
	// Board at beginning of turn.
	Board startBoard;
	// Board in progress.
	Board resultBoard;


	public Turn(Player player, Board startBoard, List<Die> startingDice) {
		int startingDiceCount = startingDice.size();

		this.player = player;
		this.startBoard = startBoard;
		this.resultBoard = startBoard;

		this.diceAvailable    = new ArrayList<Die>(startingDice);
		this.diceCombinations = generateDiceCombinations(diceAvailable);
		this.movesAvailable   = generateMovesAvailable(diceCombinations, startBoard);
		this.nextBoardsAvailable = generateNextMovesAvailable(this.movesAvailable, startBoard);

		this.diceUsed   = new ArrayList<Die>(diceAvailable.size());
		this.movesTaken = new ArrayList<TranslatedMove>(movesAvailable.size());
	}

	public void takeMove(TranslatedMove move) {
		if (movesAvailable.contains(move)) {
			// Update the board
			resultBoard = move.take(resultBoard);
			// Add to the list of moves taken
			movesTaken.add(move);
			// Move dice from available to used
			diceUsed.addAll(diceAvailable
					.stream()
					.filter((d) -> move.die().has(d))
					.collect(Collectors.toList()));
			diceAvailable.removeAll(diceUsed);
			// Update diceCombinations
			diceCombinations = generateDiceCombinations(diceAvailable);
			// Update movesAvailable
			movesAvailable = generateMovesAvailable(diceCombinations, resultBoard);
		}
	}

	public List<TranslatedMove> movesAvailable() {
		return movesAvailable;
	}

	public Board resultBoard() {
		return resultBoard;
	}

	public List<Board> nextBoardsAvailable() {
		return nextBoardsAvailable;
	}

	// public void addDie(Die d) {
	// 	diceAvailable.add(d);
	// 	// NOTE: inefficiency: regenerates all combinations instead of updating with just the new ones.
	// 	diceCombinations = generateDiceCombinations(diceAvailable);
	// }

	List<TranslatedMove> generateMovesAvailable(List<Die> dice, Board board) {
		List<TranslatedMove> moves = new ArrayList<TranslatedMove>();

		List<Pawn> moveablePawns = player.pawns()
			.stream()
			.filter((p) -> !board.inHome(p))
			.collect(Collectors.toList());

		for (Pawn pawn : moveablePawns) {
			for (Die die : dice) {
				moves.add(new TranslatedMove(die, pawn, board));
			}
		}

		return moves.stream().filter((m) -> !m.isNoop()).collect(Collectors.toList());
	}
	List<Board> generateNextMovesAvailable(List<TranslatedMove> moves, Board startBoard) {
		List<Board> boards = new ArrayList<Board>();

		for (TranslatedMove move : moves) {
			boards.add(move.take(startBoard));
		}

		return boards;
	}

	List<Die> generateDiceCombinations(List<Die> diceAvailable) {
		List<Die> combinations = new ArrayList<Die>(diceAvailable);
		List<Die> currentCombination = new ArrayList<Die>(combinations);

		do {
			currentCombination = dieCombinations(currentCombination, diceAvailable);
			combinations.addAll(currentCombination);
		} while (currentCombination.size() != 0);

		return combinations;
	}

	List<Die> dieCombinations(List<Die> dice1, List<Die> dice2) {
		List<Die> combinations = new ArrayList<Die>();

		for (Die die1 : dice1) {
			List<Die> intersect = dice2.stream().filter((d) -> !die1.has(d)).collect(Collectors.toList());
			for (Die die2 : intersect) {
				combinations.add(new CombinedDie(die1, die2));
			}
		}

		return combinations;
	}

	public static void main(String[] args) throws Die.InvalidDieException {
		Board board = new Board();
		Turn t = new Turn(new parcheesi.player.StubPlayer(0), board, Arrays.asList(
			new parcheesi.die.NormalDie(5),
			new parcheesi.die.NormalDie(5),
			new parcheesi.die.NormalDie(2),
			new parcheesi.die.NormalDie(2)
		));

		System.out.println(t.diceCombinations.size() == (4 + 12 + 24 + 24));

		// // Pairs
		List<Die> pairs = t.dieCombinations(t.diceAvailable, t.diceAvailable);
		// System.out.println(pairs.stream().count() == 12);
		// pairs.stream().forEach((d) -> System.out.println(d + " having value " + d.getValue()));
		// // Triples
		List<Die> triples = t.dieCombinations(pairs, t.diceAvailable);
		// System.out.println(triples.stream().count() == 24);
		// triples.stream().forEach((d) -> System.out.println(d + " having value " + d.getValue()));
		// // Quadruples
		List<Die> quadruples = t.dieCombinations(pairs, pairs);
		// System.out.println(quadruples.stream().count() == 24);
		// quadruples.stream().forEach((d) -> System.out.println(d + " having value " + d.getValue()));
		// Quintuples -- of which there are none
		List<Die> quintuples = t.dieCombinations(quadruples, t.diceAvailable);
		// System.out.println(quintuples.stream().count() == 0);
		
		t.movesAvailable.forEach((m) -> m.debugPrint());
		t.takeMove(t.movesAvailable.get(0));
		System.out.println("-------------------------");
		t.movesAvailable.forEach((m) -> m.debugPrint());
	}
}
