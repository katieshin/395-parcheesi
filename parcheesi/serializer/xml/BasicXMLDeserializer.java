package parcheesi.serializer.xml;

import java.util.function.BiFunction;

import static parcheesi.Utils.*;
import static parcheesi.serializer.xml.Element.*;

public class BasicXMLDeserializer implements XMLDeserializer {
	public Node parse(String document) {
		if (document.length() >= 3) {
			String firstNode = getFirstNode(document);
			String firstNodeBody = tagContents(firstNode, document);

			if (firstNode.equals("do-move")) {
				return parse(DoMove(), this::parseDoMove, document);
			} else if (firstNode.equals("start-game")) {
				return StartGame().child(firstNodeBody);
			} else if (firstNode.equals("doubles-penalty")) {
				return DoublesPenalty();
			}
		}

		return new Node.Empty("parse-failure");
	}

	private String getFirstNode(String document) {
		// NOTE: Why the magic 1? This function assumes a valid document and thus skips starting "<".
		return document.substring(1, document.indexOf(">"));
	}

	private String tagContents(String tag, String document) {
		String tagEnd = tag + ">";
		String openTag = "<" + tagEnd;
		String closeTag = "</" + tagEnd;

		int start = document.indexOf(openTag);
		int end = document.indexOf(closeTag);
		return document.substring(start + openTag.length(), end);
	}

	private Node<Node> parseDoMove(Node parent, String document) {
		return parent.child(
			parse(Board(), this::parseBoard, document),
			parse(Dice(), this::parseDice, document)
		);
	}

	private Node parseDice(Node parent, String string) {
		for (String dieString : string.split("</die>")) {
			parent.child(
				Die().child(
					Integer.parseInt(tagContents("die", dieString + "</die>"))
				)
			);
		}

		return parent;
	}

	private Node<Node> parseBoard(Node parent, String document) {
		return parent.child(
			parse(Start(),    this::parsePawns,     document),
			parse(Main(),     this::parsePieceLocs, document),
			parse(HomeRows(), this::parsePieceLocs, document),
			parse(Home(),     this::parsePawns,     document)
		);
	}

	private Node parse(Node parent, BiFunction<Node, String, Node> parser, String document) {
		return parser.apply(parent, tagContents(parent.getName(), document));
	}

	private Node parsePawns(Node parent, String document) {
		for (String pawnString : document.split("</pawn>")) {
			parent.child(
				parse(Pawn(), this::parsePawn, pawnString + "</pawn>")
			);
		}

		return parent;
	}

	private Node parsePawn(Node parent, String pawnString) {
		return parent.child(
			Color().child(tagContents("color", pawnString)),
			Id().child(Integer.parseInt(tagContents("id", pawnString)))
		);
	}

	private Node parsePieceLocs(Node parent, String document) {
		for (String pieceLocString : document.split("</piece-loc>")) {
			parent.child(
				parse(PieceLoc(), this::parsePieceLoc, pieceLocString + "</piece-loc>")
			);
		}

		return parent;
	}

	private Node<Node> parsePieceLoc(Node parent, String pieceLocString) {
		return parent.child(
			parse(Pawn(), this::parsePawn, pieceLocString),
			Loc().child(Integer.parseInt(tagContents("loc", pieceLocString)))
		);
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
				deserializer.parse(Board(), deserializer::parseBoard, someBoard.toString()).toString().equals(someBoard.toString()),
				"Parsing a board node after toString results in equivalent node"
			);

			Node someDice = Dice().child(
				Die().child(5),
				Die().child(2)
			);

			check(
				someDice.toString().equals(
					deserializer.parse(Dice(), deserializer::parseDice, someDice.toString()).toString()
				),
				"Parsing a dice node after toString results in equivalent node"
			);

			Node someDoMove = DoMove().child(someBoard, someDice);

			check(
				deserializer.parse(someDoMove.toString()).toString()
					.equals(someDoMove.toString()),
				"Parsing a do-move node after toString results in equivalent node"
			);

			Node someStartGame = StartGame().child(parcheesi.Color.forPlayer(0).getColorName());

			check(
				deserializer.parse(someStartGame.toString()).toString()
					.equals(someStartGame.toString()),
				"Parsing a start-game node after toString results in equivalent node"
			);

			Node someDoublesPenalty = DoublesPenalty();

			check(
				deserializer.parse(someDoublesPenalty.toString()).toString()
					.equals(someDoublesPenalty.toString()),
				"Parsing a doubles-penalty node after toString results in equivalent node"
			);

			check(
				deserializer.parse("").toString()
					.equals("<parse-failure></parse-failure>"),
				"Parsing empty string results in parse-failure"
			);

			check(
				deserializer.parse("<not-expected></not-expected>").toString()
					.equals("<parse-failure></parse-failure>"),
				"Parsing an unknown top-level node results in parse-failure"
			);

			summarize();
		}
	}
}
