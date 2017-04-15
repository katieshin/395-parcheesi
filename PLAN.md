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

- ~~SmartMove is dumb and we're getting rid of it~~
- Pawn.getColor doesn't belong there
- Player should not own doubles count
- Player should not know which pawn is furthest on Board
- Make sure code is organized top-down (Helpers after helped methods)
- How will we calculate pawn distance from Entry accurately? (Aren't doing this yet)
- Board should not own performMove()
  - TranslatedMoves have a method called apply()
- Move constants in Board to Parameters.java (or some other config file)
- Use try/catch to clean up `throws` spew in NormalDieTester and DoublesBonusDie

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
