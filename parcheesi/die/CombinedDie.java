package parcheesi.die;

public class CombinedDie extends Die {
	Die firstDie;
	Die secondDie;

	public CombinedDie(Die firstDie, Die secondDie) {
		this.firstDie  = firstDie;
		this.secondDie = secondDie;
	}

	public int getValue () {
		return firstDie.getValue() + secondDie.getValue();
	}
}
