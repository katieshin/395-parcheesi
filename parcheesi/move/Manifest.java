package parcheesi.move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* NOTE: This would need to be updated with any new Moves if any new Moves were to be created.
 * TranslatedMove depends on its accuracy.
 */
// TODO: Use Java's crazy-ass dynamic classloader stuff to generate this data automagically.
public class Manifest {
	public static final List<Class<? extends Move>> MOVE_CLASSES = Arrays.asList(
		MoveMain.class,
		MoveHome.class,
		EnterPiece.class
	);
}
