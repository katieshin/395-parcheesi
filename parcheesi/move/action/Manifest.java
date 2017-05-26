package parcheesi.move.action;

import java.util.List;
import java.util.Arrays;

public class Manifest {
	public static final List<Action> ACTIONS = Arrays.asList(
		Bop.action,
		BreakBlockade.action,
		Enter.action,
		FormBlockade.action,
		MoveForward.action
	);
}
