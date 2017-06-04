package parcheesi.move.action;

import java.util.List;
import java.util.Arrays;

public class Manifest {
	public static final List<Action> ACTIONS = Arrays.asList(
		// NOTE: order is significant
		// "Move" actions always need to happen before Bops
		// "Move" actions are mutually exclusive, so order doesn't matter
		Enter.action,
		MoveForward.action,
		Bop.action
	);
}
