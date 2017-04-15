package parcheesi.rule;

import parcheesi.move.Move;
import parcheesi.move.MoveMain;

import parcheesi.die.Die;
import parcheesi.die.DoublesBonusDie;

import parcheesi.player.Player;
import parcheesi.Board;

public class MoveMainRules implements Rule {
	// FIXME: This responsibility does not belong here.
	boolean hasDoubles(Die[] dice) {
		return dice[0].getValue() == dice[1].getValue();
	}

	int doubleBonus(int doublesValue) {
		return 7 - doublesValue;
	}

	Die[] diceWithBonus(Die[] dice) throws Die.InvalidDieException {
		Die die1 = dice[0];
		Die die2 = dice[1];

		int doubleBonusValue = doubleBonus(dice[0].getValue());

		Die bonus1 = new DoublesBonusDie(doubleBonusValue);
		Die bonus2 = new DoublesBonusDie(doubleBonusValue);

		return new Die [] { die1, die2, bonus1, bonus2 };
	}
	// FIXME: End of misplaced block of responsibility.

	static class MustUseDoublesBonus implements Rule {
		public boolean enforce(Die[] dice, Player p, Board before, Move[] moves, Board after) {
			for (Move m : moves) {
				// TODO
			}

			return false;
		}

		public MustUseDoublesBonus() { }
		public static MustUseDoublesBonus instance = new MustUseDoublesBonus();
	}

	static class MustGiveDoublesPenalty implements Rule {
		public boolean enforce(Die[] dice, Player p, Board before, Move[] moves, Board after) {
			// FIXME Game is not implemented yet.
			// if in the most recent turn, it was the 2nd DoublesBonusTurn and doubles were rolled:
			//   then and only then, the penalty has to be applied.
			Turn currentTurn = Game.currentTurn();

			// If the currentTurn was given as a doubles bonus
			if (currentTurn instanceof DoublesBonusTurn
					// And it was the 2nd such doubles bonus turn, and you rolled doubles on it
					&& currentTurn.doublesRolled == 3) {
				// Then we must apply the penalty.

				// Verify that:
				// (1) 1 Move is taken
				if (currentTurn.movesTaken.length == 1) {
					Move onlyMove = currentTurn.movesTaken.get(0);

					// (2) The move taken is a DoublesPenalty move
					if (onlyMove instanceof DoublesPenaltyMove) {
						// (3) The DoublesPenaltyMove was enforced correctly
						Pawn furthestPawn = before.furthestPawnOfPlayer(p);
						return before.removePawn(furthestPawn).equals(after);
					}
				}
			}

			return false;
		}

		public MustGiveDoublesPenalty() { }
		public static MustGiveDoublesPenalty instance = new MustGiveDoublesPenalty();
	}

	public MoveMainRules() { }
	public static MoveMainRules instance = new MoveMainRules();

	// NOTE: All must be true relationship.
	public boolean enforce(Die[] dice, Player player, Board before, Move[] moves, Board after) {
		boolean anyMoveIsMoveMain = false;
		for (Move m : moves) {
			anyMoveIsMoveMain |= (m instanceof MoveMain);
		}

		if (!anyMoveIsMoveMain) return true; // No need to apply these rules.

		if (instance.hasDoubles(dice)) {
			Die[] newDice = instance.diceWithBonus(dice);
			return MustUseDoublesBonus.instance.enforce(newDice, player, before, moves, after)
				&& MustGiveDoublesPenalty.instance.enforce(newDice, player, before, moves, after);
		}

		// None of the rules ended up applying anyway; return true by default.
		return true;
	}

	public static void main(String[] args) {
		new MoveMainRulesTester();
	}

	static class MoveMainRulesTester extends parcheesi.test.Tester {
		public MoveMainRulesTester() {
			summarize();
		}
	}
}

