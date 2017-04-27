# Planned work for Parcheesi (TODO)

Current structure: (RulesChecker at top, because importance)

RulesChecker
  EnterPieceRules
  MoveMainRules
    ? Board
    ? Player
    ? How do we handle special moves?
    ? How do we apply a move to a board?

Not all of the questions that need to be answered in order to write MoveMainRules have been
answered.

## Cruft (Stuff to clean up)

- Make sure code is organized top-down (Helpers after helped methods)
- ~~Board should not own performMove()~~
  - TranslatedMoves have a method called apply()

## Handling Doubles Penalty

- Special Move DoublesPenaltyMove which becomes the only move in that DoublesBonusTurn
- This move is set as your Turn's only move if and only if, after rolling the dice for the 2nd
  DoublesBonusTurn (3rd consecutive Turn total), you roll doubles. Both dice are used, so you cannot
  translate any moves. 1 move is added: DoublesPenaltyMove. That becomes your Turn.
- This responsibility will probably be handled in Game. It will roll your dice and give you your
  Turn, and that Turn might be a Turn in which you can make no choices (either no moves are
  available to you, or you incurred a DoublesPenalty and that is your only move), and the player
  should still be presented with some message, and then the Turn should be applied and the rules
  checked.

# Board Interface (Need this)

1. ~~movePawnForward~~
2. ~~removePawn~~

	// !!Doesn't belong here!!
	// Location loc = before.getPawnLocation(pawn);
	// before.setPawnCoordinate(pawn, loc.next(pawn));
	MoveMain.apply(Board board) {
		board.movePawn(this.pawn, this.die);
	}

	Board.movePawnForward(Pawn pawn, int count) {
		for (int i = 0; i < count; i++) {
			Location loc = getPawnLocation(pawn);
			setPawnCoordinate(pawn, loc.next(pawn));
		}
	}

## New stuff to make way for

1. Turn
  - keeps track of all dice
  - translates dice to available "TranslatedMove"s
  - keeps track of moves taken
  - a die can be added to the Turn
  - a move (from available moves) can be taken on the Turn, and a corresponding die "used"
  - end method (private, called when number of moves taken is equal to number of dice)

  - TranslatedMove
    - origin die --- DoublesBonusDie, NormalDie, BopBonusDie, HomeBonusDie
    - parent move type
      - One of: EnterPiece, MoveHome, MoveMain
    - ModifierMove[] modifiers
      - One or more of: Bop, CreateBlockade, BreakBlockade, EnterHome
      - A ModifierMove adds effects: add a blockade/remove a blockade/add a die/moves other pawns
    - apply() method for applying the move to the board and turn, and then applies each ModifierMove

2. DoublesBonusTurn -- special type of turn that is played immediately instead of after other
   players (extends or it might wrap Turn)
  - keeps track of doubles count
  - Turn turn (the turn which is actually going to be taken)
  - Or it is a turn?
  - If turn results in another DoublesBonusTurn, create new DoublesBonusTurn with updated doubles
    count, and add new Turn to beginning of list of turns

3. BopMoveRules, CreateBlockadeMoveRules, EnterHomeMoveRules, BreakBlockadeMoveRules,
   EnterPieceMoveRules, MoveHomeMoveRules, MoveMainMoveRules
  - These are a special subclass of Rule called a MoveRule which must have:
     - precondition
     - postcondition
     which take the same arguments as enforce.

4. Rules now have dependencies.
  - BFS of dependency tree tells us which rules need to be run before other rules
  - Memoize the result during a single move to reduce overhead (bonus)

5. PawnKeeper
  - add package parcheesi.pawn to Pawn and to PawnKeeper
  - PawnKeeper will have access to Pawn fields/methods
  - Create static methods for getting info about a Pawn from outside the package

6. Game
  - Knows the order in which players take their turns
  - Keeps track of all turns taken in the game so far

  - Turn[] turnHistory
    - Either Turn or DoublesBonusTurn
    - Use sequence of turns to figure out how many doubles a player has rolled
    - A Turn after a series of "DoublesBonusTurn"s signifies that a new player is playing
