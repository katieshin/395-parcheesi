import javax.enterprise.inject.se.SeContainerInitializer;
import javax.enterprise.inject.se.SeContainer;

import bugzapper.ContractualInterceptor;

import parcheesi.Board;
import parcheesi.pawn.Pawn;
import parcheesi.Color;

public class Main {
	public static void main(String[] args) {
		SeContainer container = SeContainerInitializer.newInstance()
			.addPackages(ContractualInterceptor.class, Board.class)
			// NOTE: since ContractualInterceptor has Priority annotation, no need to explicitly enable it
			// .enableInterceptors(ContractualInterceptor.class)
			.initialize();

		// Returns an Instance<Board>
		container.select(Board.class);
		Board board = container.select(Board.class).get();
		Board board2 = container.select(Board.class).get();

		Pawn p = new Pawn(0, Color.forPlayer(0));

		// All of these will generate log messages because of instrumentation
		board.addPawn(p);
		board.getPawnCoordinate(p);
		board.equals(board2);
		board.isBlockade(board.getPawnCoordinate(p));
	}
}
