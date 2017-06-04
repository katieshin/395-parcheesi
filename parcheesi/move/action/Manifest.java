package parcheesi.move.action;

import java.util.List;
import java.util.Arrays;

public class Manifest {
	public static final List<Action> ACTIONS = Arrays.asList(
		// NOTE: order is significant
		// BreakBlockade always has to come first
		BreakBlockade.action,
		// "Move" actions always need to happen before Bops/FormBlockades
		// "Move" actions are mutually exclusive, so order doesn't matter
		Enter.action,
		MoveForward.action,
		// "Post-move" actions are mutually exclusive, so order doesn't matter
		Bop.action,
		FormBlockade.action
	);
}
