package parcheesi.serializer.xml;

import static parcheesi.Utils.*;
import static parcheesi.serializer.xml.Element.*;

// Let's be horrifyingly inefficient.
public class BasicXMLDeserializer implements XMLDeserializer {
	public Node<Node> parse(String string) {
		return Board();
	}

	public Node<Node> parseBoard(String string) {
		Node<Node> boardNode = Board();

		Node<Node> startNode = Start();
		int start = string.indexOf("<start>");
		int end = string.indexOf("</start>");
		startNode.child(parsePawns(string.substring(start + ("<start>").length(), end)));
		boardNode.child(startNode);

		Node<Node> mainNode = Main();
		start = string.indexOf("<main>");
		end = string.indexOf("</main>");
		mainNode.child(parsePieceLocs(string.substring(start + ("<main>").length(), end)));
		boardNode.child(mainNode);

		Node<Node> homeRowsNode = HomeRows();
		start = string.indexOf("<home-rows>");
		end = string.indexOf("</home-rows>");
		homeRowsNode.child(parsePieceLocs(string.substring(start + ("<home-rows>").length(), end)));
		boardNode.child(homeRowsNode);

		Node<Node> homeNode = Home();
		start = string.indexOf("<home>");
		end = string.indexOf("</home>");
		homeNode.child(parsePawns(string.substring(start + ("<home>").length(), end)));
		boardNode.child(homeNode);

		return boardNode;
	}

	public Node[] parsePawns(String string) {
		String[] pawnStrings = string.split("</pawn>");
		Node[] pawnNodes     = new Node[pawnStrings.length];

		for (int i = 0; i < pawnStrings.length; i ++) {
			String pawnString = pawnStrings[i];
			pawnNodes[i] = parsePawn(pawnString.substring("<pawn>".length()));
		}

		return pawnNodes;
	}

	public Node<Node> parsePawn(String pawnString) {
		Node<Node> pawnNode = Pawn();

		Node<String> colorNode = Color();
		int start = pawnString.indexOf("<color>");
		int end = pawnString.indexOf("</color>");
		colorNode.child(pawnString.substring(start + "<color>".length(), end));
		pawnNode.child(colorNode);

		Node<Integer> idNode = Id();
		start = pawnString.indexOf("<id>");
		end = pawnString.indexOf("</id>");
		idNode.child(Integer.parseInt(pawnString.substring(start + "<id>".length(), end)));
		pawnNode.child(idNode);

		return pawnNode;
	}

	public Node[] parsePieceLocs(String string) {
		String[] pieceLocStrings = string.split("</piece-loc>");
		Node[] pieceLocNodes     = new Node[pieceLocStrings.length];

		for (int i = 0; i < pieceLocStrings.length; i ++) {
			String pieceLocString = pieceLocStrings[i];
			pieceLocNodes[i] = parsePieceLoc(pieceLocString.substring("<piece-loc>".length()));
		}

		return pieceLocNodes;
	}

	public Node<Node> parsePieceLoc(String pieceLocString) {
		Node<Node> pieceLocNode = PieceLoc();

		int start = pieceLocString.indexOf("<pawn>");
		int end = pieceLocString.indexOf("</pawn>");
		pieceLocNode.child(parsePawn(pieceLocString.substring(start + "<pawn>".length(), end)));

		Node<Integer> locNode = Loc();
		start = pieceLocString.indexOf("<loc>");
		end = pieceLocString.indexOf("</loc>");
		locNode.child(Integer.parseInt(pieceLocString.substring(start + "<loc>".length(), end)));
		pieceLocNode.child(locNode);

		return pieceLocNode;
	}

	public static void main(String[] args) {
		new BasicXMLDeserializerTester();
	}

	private static class BasicXMLDeserializerTester extends parcheesi.test.Tester {
		public BasicXMLDeserializerTester() {
			BasicXMLDeserializer deserializer = new BasicXMLDeserializer();

			Node<Node> someBoard = Board().child(
				Start().child(
					Pawn().child(
						Color().child(parcheesi.Color.forPlayer(0).getColorName()),
						Id().child(0)
					),
					Pawn().child(
						Color().child(parcheesi.Color.forPlayer(0).getColorName()),
						Id().child(1)
					),
					Pawn().child(
						Color().child(parcheesi.Color.forPlayer(1).getColorName()),
						Id().child(3)
					)
				),
				Main().child(
					PieceLoc().child(
						Pawn().child(
							Color().child(parcheesi.Color.forPlayer(0).getColorName()),
							Id().child(2)
						),
						Loc().child(5)
					),
					PieceLoc().child(
						Pawn().child(
							Color().child(parcheesi.Color.forPlayer(1).getColorName()),
							Id().child(1)
						),
						Loc().child(10)
					),
					PieceLoc().child(
						Pawn().child(
							Color().child(parcheesi.Color.forPlayer(1).getColorName()),
							Id().child(0)
						),
						Loc().child(15)
					)
				),
				HomeRows().child(
					PieceLoc().child(
						Pawn().child(
							Color().child(parcheesi.Color.forPlayer(1).getColorName()),
							Id().child(2)
						),
						Loc().child(99999)
					)
				),
				Home().child(
					Pawn().child(
						Color().child(parcheesi.Color.forPlayer(0).getColorName()),
						Id().child(3)
					)
				)
			);

			check(
				deserializer.parseBoard(someBoard.toString()).toString().equals(someBoard.toString()),
				"Parsing a node string results in the same node"
			);

			summarize();
		}
	}
}
