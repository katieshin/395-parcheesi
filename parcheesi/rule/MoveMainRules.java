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

	static class MustUseDoubleBonus implements Rule {
		public boolean enforce(Die[] dice, Player p, Board before, Move[] moves, Board after) {
			for (Move m : moves) {
				// TODO
			}

			return false;
		}

		public MustUseDoubleBonus() { }
		public static MustUseDoubleBonus instance = new MustUseDoubleBonus();
	}

	static class MustGiveDoublePenalty implements Rule {
		public boolean enforce(Die[] dice, Player p, Board before, Move[] moves, Board after) {
			if (p.doublesRolled == 2) {
				// Verify that:
				// 1) No moves were taken
				return moves.length == 0
					// 2) Furthest pawn was forced to restart
					&& after.getPositionOfPawn(p.getFurthestPawn()) == -1;
			}
			return false;
		}

		public MustGiveDoublePenalty() { }
		public static MustGiveDoublePenalty instance = new MustGiveDoublePenalty();
	}

	public NormalMoveRules() { }
	public static NormalMoveRules instance = new NormalMoveRules();

	// NOTE: All must be true relationship.
	public boolean enforce(Die[] dice, Player p, Board before, Move[] moves, Board after) {
		boolean anyMoveIsMoveMain = false;
		for (Move m : moves) {
			anyMoveIsMoveMain |= (m instanceof MoveMain);
		}

		if (!anyMoveIsMoveMain) return true; // No need to apply these rules.

		if (instance.hasDoubles(dice)) {
			Die[] newDice = instance.diceWithBonus(dice);
			return MustUseDoubleBonus.instance.enforce(newDice, player, before, moves, after)
				&& MustGiveDoublePenalty.instance.enforce(newDice, player, before, moves, after);
		}

		// None of the rules ended up applying anyway; return true by default.
		return true;
	}

	public static void main(String[] args) {
		new NormalMoveRulesTester();
	}

	static class NormalMoveRulesTester extends parcheesi.test.Tester {
		public NormalMoveRulesTester() {
			summarize();
		}
	}
}

